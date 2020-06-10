package uk.co.ramp.covid.simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.parameters.ParameterIO;
import uk.co.ramp.covid.simulation.population.ImpossibleAllocationException;
import uk.co.ramp.covid.simulation.population.ImpossibleWorkerDistributionException;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private Integer nInitialInfections = null;
    private Integer externalInfectionDays = null;
    private Integer nDays = null;
    private Integer nIters = null;
    private Integer rngSeed = null;
    private String outputDirectory = null;
    private Lockdown lockDown = null;
    private Lockdown schoolLockDown = null;

    public Model() {}

    // Builder style interface
    public Model setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public Model setnInitialInfections(int nInitialInfections) {
        this.nInitialInfections = nInitialInfections;
        return this;
    }

    public Model setExternalInfectionDays(int n) {
        this.externalInfectionDays = n;
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

    public Integer getRNGSeed() {
        return rngSeed;
    }

    public Model setSchoolLockdown(int start, int end, double socialDistance) {
        this.schoolLockDown = new Lockdown(start, end, socialDistance);
        return this;
    }

    public Model setLockdown(int start, int end, double socialDistance) {
        this.lockDown = new Lockdown(start, end, socialDistance);
        return this;
    }

    public Model setOutputDirectory(String fname) {
        this.outputDirectory = fname;
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
        if (nInitialInfections == null) {
            LOGGER.warn("Uninitialised model parameter: nInitialInfections");
            valid = false;
        }
        if (externalInfectionDays == null) {
            LOGGER.warn("Uninitialised model parameter: externalInfectionDays");
            valid = false;
        }
        if (nDays == null) {
            LOGGER.warn("Uninitialised model parameter: nDays");
            valid = false;
        }
        if (rngSeed == null) {
            LOGGER.warn("Uninitialised model parameter: rngSeed");
            valid = false;
        }
        if (!outputDisabled) {
            if (outputDirectory == null) {
                LOGGER.warn("Uninitialised model parameter: outputDirectory");
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
    public List<List<DailyStats>> run(int simulationID) {
        if (!isValid()) {
            throw new InvalidParametersException("Invalid model parameters");
        }

        RNG.seed(rngSeed + simulationID);

        List<List<DailyStats>> stats = new ArrayList<>(nIters);
        for (int i = 0; i < nIters; i++) {
            // As households/person types are determined probabilistically in some cases it can be
            // impossible to populate all houseolds, e.g. 50 ADULT households and only 49 ADULTS.
            // He we return an empty run to indicate that the parameters etc are okay, but we used an unlucky random
            // seed (this can be accounted for when processing the output).
            Population p;
            try {
                p = new Population(populationSize);
            } catch (ImpossibleAllocationException e) {
                LOGGER.error(e);
                break;
            } catch (ImpossibleWorkerDistributionException e) {
                LOGGER.error(e);
                break;
            }


            p.setExternalInfectionDays(externalInfectionDays);
            p.seedVirus(nInitialInfections);
            if (lockDown != null) {
                p.setLockdown(lockDown.start, lockDown.end, lockDown.socialDistance);
            }
            if (schoolLockDown != null) {
                p.setSchoolLockdown(schoolLockDown.start, schoolLockDown.end, schoolLockDown.socialDistance);
            }

            List<DailyStats> iterStats = p.simulate(nDays);
            for (DailyStats s : iterStats) {
                s.determineRValues(p);
            }

            
            stats.add(iterStats);

        }

        if (!outputDisabled) {
            writeOutput(simulationID, stats);
        }

        return stats;
    }
    
    private void writeOutput(int iterId, List<List<DailyStats>> s) {
        Path outP;

        if (outputDirectory.equals("")) {
            outP = FileSystems.getDefault().getPath(".");
        } else {
            DateTimeFormatter tsFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
            String timeStamp = tsFormatter.format(LocalDateTime.now());
            outP = FileSystems.getDefault().getPath(outputDirectory, timeStamp);
            if (!outP.toFile().mkdirs()) {
                LOGGER.error("Could not create output directory: " + outP);
                return;
            }
        }
        
        // By here the output directory will be available
        outputCSV(outP.resolve("out.csv"), iterId, s);
        ParameterIO.writeParametersToFile(outP.resolve("population_params.json"));
        outputModelParams(outP.resolve("model_params.json"));
       
    }

    private void outputModelParams(Path outF) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(outF.toFile())) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }


    public void outputCSV(Path outF, int startIterID, List<List<DailyStats>> stats) {
    final String[] headers = {"iter", "day", "H", "L", "A", "P1", "P2", "D", "R", "ISeed",
                              "ICs_W","IHos_W","INur_W","IOff_W","IRes_W","ISch_W","ISho_W","IHome_I",
                              "ICs_V","IHos_V","INur_V","IOff_V","IRes_V","ISch_V","ISho_V","IHome_V",
                              "IAdu","IPen","IChi","IInf",
                              "DAdul","DPen","DChi","DInf", "SecInfections", "GenerationTime" };
        try {
            FileWriter out = new FileWriter(outF.toFile());
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
            for (int i = 0; i < nIters; i++) {
                for (DailyStats s : stats.get(i)) {
                    s.appendCSV(printer, startIterID + i);
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

    public void optionallyGenerateRNGSeed() {
        if (rngSeed == null) {
            rngSeed = RNG.generateRandomSeed();
            LOGGER.warn("Generated RNG seed: " + rngSeed);
        }
    }
}
