package uk.co.ramp.covid.simulation;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.population.Population;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** A uk.co.ramp.covid.simulation.Model represents a particular run of the model with some given parameters
 */
public class Model {
    private static final Logger LOGGER = LogManager.getLogger(Model.class);

    private class Lockdown {
        public int start;
        public int end;
        public double socialDistance;

        public Lockdown(int start, int end, double socialDistance) {
            this.start = start;
            this.end = end;
            this.socialDistance = socialDistance;
        }
    }

    private int populationSize;
    private int nHouseholds;
    private int nInfections;
    private int nDays;
    private int nIters;
    private String outputFile;
    private Lockdown lockDown = null;
    private Lockdown schoolLockDown = null;

    public Model() {};

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

    public List<List<DailyStats>> run() {
        List<List<DailyStats>> stats = new ArrayList<>(nIters);
        for (int i = 0; i < nIters; i++) {
            Population p = new Population(populationSize, nHouseholds);
            p.populateHouseholds();
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

        if (outputFile != null) {
            for (int i = 0; i < nIters; i++) {
                outputCSV(outputFile, i, stats.get(i));
            }
        }

        return stats;
    }

    public void outputCSV(String fname, int iter, List<DailyStats> stats) {
        final String[] headers = {"iter","day","H","L","A","P1","P2","D","R"};
        try {
            FileWriter out = new FileWriter(fname);
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
            for (DailyStats s : stats) {
                s.appendCSV(printer, iter);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

}
