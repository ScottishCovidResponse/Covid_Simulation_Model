/*
 * Paul Bessell.
 * This Class initialises the model by generating a Population object and simulating Covid spread through the Populaiton
 */

// Testing some changes again
package uk.co.ramp.covid.simulation;

import com.google.gson.JsonParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.Population;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RunModel {

    private static final Logger LOGGER = LogManager.getLogger(RunModel.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            LOGGER.warn("Missing a parameters file");
        } else {
            readParameters(args[0]);
        }

        RunModel mModel = new RunModel();
//mModel.runTest();
       mModel.runBaseline();
        /*mModel.runLockdown();
        mModel.runSchoolLockdown();*/
    }

    public void runTest() {
        LOGGER.info((1.0 / 7.0) / 24.0);
        Population p = new Population(10000, 3000);
        p.populateHouseholds();
        p.summarisePop();
        p.createMixing();
        p.allocatePeople();
        p.seedVirus(10);
        p.setLockdown(40, 100, 0.9);
        p.timeStep(300);
    }

    public List<DailyStats> oneBaselineIter(int populationSize, int nHousehold, int nInfections, int nDays) {
        Population p = new Population(populationSize, nHousehold);
        p.populateHouseholds();
        p.summarisePop();
        p.createMixing();
        p.allocatePeople();
        p.seedVirus(nInfections);

        return p.timeStep(nDays);
    }

    public void runBaseline() throws Exception { // Run and output the baseline scenarios - with no lockdown
        int nIter = 1;
        for (int i = 1; i <= nIter; i++) {
            List<DailyStats> stats = oneBaselineIter(250000, 75000, 100, 365);
            outputCSV("ModelOutputs/Baseline20200429/BaselineOut.csv", i, stats);
        }

    }

    public void runLockdown() throws Exception { // Run and output the scenarios with simple stop-start lockdown
        int nIter = 100;
        for (int i = 1; i <= nIter; i++) {
            Population p = new Population(25000, 7500);
            p.populateHouseholds();
            p.summarisePop();
            p.createMixing();
            p.allocatePeople();
            p.seedVirus(10);
            p.setLockdown(35, 77, 0.8);

            List<DailyStats> stats = p.timeStep(365);
            outputCSV("ModelOutputs//Lockdown20200429//Lockdown_35_77_0.8.csv", i, stats);
        }

        nIter = 100;
        for (int i = 1; i <= nIter; i++) {
            Population p = new Population(25000, 7500);
            p.populateHouseholds();
            p.summarisePop();
            p.createMixing();
            p.allocatePeople();
            p.seedVirus(10);
            p.setLockdown(35, 77, 0.5);

            List<DailyStats> stats = p.timeStep(365);
            outputCSV("ModelOutputs//Lockdown20200429//Lockdown_35_77_0.5.csv", i, stats);
        }
    }

    public void runSchoolLockdown() throws Exception { // Run and output scenarios with continued lockdown and schools reopening
        int nIter = 100;
        for (int i = 1; i <= nIter; i++) {
            Population p = new Population(25000, 7500);
            p.populateHouseholds();
            p.summarisePop();
            p.createMixing();
            p.allocatePeople();
            p.seedVirus(10);
            p.setSchoolLockdown(35, 77, 0.8);

            List<DailyStats> stats = p.timeStep(365);
            outputCSV("ModelOutputs//Lockdown20200429//Lockdown_35_77_0.8_School.csv", i, stats);
        }
    }

    public static void readParameters(String fpath) {
        try {
            ParameterReader.readParametersFromFile(fpath);
        } catch (IOException e) {
            System.err.println("Chould not read from parameters file: " + fpath);
            System.err.println(e);
            System.exit(1);
        } catch (JsonParseException e) {
            System.err.println("Chould not parse JSON from parameters file: " + fpath);
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void outputCSV(String fname, int iter, List<DailyStats> stats) {
        final String[] headers = {"iter,day,H,L,A,P1,P2,D,R"};
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
