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

        // Example model run
        Model m = new Model()
                .setIters(1)
                .setnDays(365)
                .setPopulationSize(10000)
                .setnHouseholds(3000)
                .setnInfections(10)
                .setOutputFile("ModelOutputs/Baseline20200429/BaselineOut.csv");
                //.setLockdown(40,80,0.9);
                //.setSchoolLockdown(40,80,0.9);

        m.run();
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


}
