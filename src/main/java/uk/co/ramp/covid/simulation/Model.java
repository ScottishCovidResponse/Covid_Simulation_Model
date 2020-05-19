package uk.co.ramp.covid.simulation;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.population.ImpossibleAllocationException;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;

/** A uk.co.ramp.covid.simulation.Model represents a particular run of the model with some given parameters
 */
public class Model {
    private static final Logger LOGGER = LogManager.getLogger(Model.class);


    private class Lockdown {
        public Integer start = null;
        public Integer end = null;
        public Double socialDistance = null;

        public Lockdown(int start, int end, double socialDistance) {
            this.start = start;
            this.end = end;
            this.socialDistance = socialDistance;
        }

        public boolean isValid() {
            boolean valid = true;
            if (start == null) {
                LOGGER.warn("Uninitialised model parameter: start");
                valid = false;
            }
            if (end == null) {
                LOGGER.warn("Uninitialised model parameter: end");
                valid = false;
            }
            if (socialDistance == null) {
                LOGGER.warn("Uninitialised model parameter: socialDistance");
                valid = false;
            }
            return valid;
        }
    }

    private boolean outputDisabled;
    private Integer populationSize = null;
    private Integer nHouseholds = null;
    private Integer nInfections = null;
    private Integer nDays = null;
    private Integer nIters = null;
    private Integer rngSeed = null;
    private String outputFile = null;
    private Lockdown lockDown = null;
    private Lockdown schoolLockDown = null;

    public Model() {}

    // Builder style interface
    public Model setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public Model setnHouseholds(int nHouseholds) {
        this.nHouseholds = nHouseholds;
        return this;
    }

    public Model setnInfections(int nInfections) {
        this.nInfections = nInfections;
        return this;
    }

    public Model setnDays(int nDays) {
        this.nDays = nDays;
        return this;
    }

    public Model setIters(int iters) {
        this.nIters = iters;
        return this;
    }

    public Model setNoOutput() {
        this.outputDisabled = true;
        return this;
    }

    public Model setRNGSeed(int seed) {
        this.rngSeed = seed;
        return this;
    }

    public Model setSchoolLockdown(int start, int end, double socialDistance) {
        this.schoolLockDown = new Lockdown(start, end, socialDistance);
        return this;
    }

    public Model setLockdown(int start, int end, double socialDistance) {
        this.lockDown = new Lockdown(start, end, socialDistance);
        return this;
    }

    public Model setOutputFile(String fname) {
        this.outputFile = fname;
        return this;
    }

    public boolean isValid() {
        boolean valid = true;

        if (populationSize == null) {
            LOGGER.warn("Uninitialised model parameter: populationSize");
            valid = false;
        }
        if (nIters == null) {
            LOGGER.warn("Uninitialised model parameter: nIters");
            valid = false;
        }
        if (nHouseholds == null) {
            LOGGER.warn("Uninitialised model parameter: nHouseholds");
            valid = false;
        }
        if (nInfections == null) {
            LOGGER.warn("Uninitialised model parameter: nInfections");
            valid = false;
        }
        if (nDays == null) {
            LOGGER.warn("Uninitialised model parameter: nDays");
            valid = false;
        }
        if (!outputDisabled) {
            if (outputFile == null) {
                LOGGER.warn("Uninitialised model parameter: outputFile");
                valid = false;
            }
        }
        // Handle optional args
        if (lockDown != null) {
            if (!lockDown.isValid()) {
                LOGGER.warn("lockDown parameters invalid");
                valid = false;
            }
        }
        if (schoolLockDown != null) {
            if (!schoolLockDown.isValid()) {
                LOGGER.warn("schoollockDown parameters invalid");
                valid = false;
            }
        }

        return valid;
    }

    /** Runs the model with the given parameters. Returns null if the parameters are missing or invalid */
    public List<List<DailyStats>> run() {
        if (!isValid()) {
            throw new InvalidParametersException("Invalid model parameters");
        }


        List<List<DailyStats>> stats = new ArrayList<>(nIters);
        for (int i = 0; i < nIters; i++) {
            Population p = new Population(populationSize, nHouseholds);

            // As households/person types are determined probabilistically in some cases it can be
            // impossible to populate all houseolds, e.g. 50 ADULT households and only 49 ADULTS.
            // He we return an empty run to indicate that the parameters etc are okay, but we used an unlucky random
            // seed (this can be accounted for when processing the output).
            try {
                p.populateHouseholds();
            } catch (ImpossibleAllocationException e) {
                LOGGER.error(e);
                break;
            }

            p.summarisePop();
            p.createMixing();
            p.allocatePeople();
            p.seedVirus(nInfections);
            if (lockDown != null) {
                p.setLockdown(lockDown.start, lockDown.end, lockDown.socialDistance);
            }
            if (schoolLockDown != null) {
                p.setSchoolLockdown(schoolLockDown.start, schoolLockDown.end, schoolLockDown.socialDistance);
            }
            stats.add(p.timeStep(nDays));
        }

        if (!outputDisabled) {
            if (outputFile != null) {
                outputCSV(stats);
            }
        }

        return stats;
    }

    public void outputCSV(List<List<DailyStats>> stats) {
    final String[] headers = {"iter", "day", "H", "L", "A", "P1", "P2", "D", "R",
                              "ICs", "IHos","INur","IOff","IRes","ISch","ISho","IHome",
                              "IAdu","IPen","IChi","Iinf",
                              "DAdul","DPen","Dchi","Dinf"  };
        try {
            FileWriter out = new FileWriter(outputFile);
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
            for (int i = 0; i < nIters; i++) {
                for (DailyStats s : stats.get(i)) {
                    s.appendCSV(printer, i);
                }
            }
            out.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    // Also allows reading from json file
    public static Model readModelFromFile(String path) throws IOException, JsonParseException {
        Reader file = new FileReader(path);
        Gson gson = new Gson();
        Model m = gson.fromJson(file, Model.class);
        return m;
    }

}
