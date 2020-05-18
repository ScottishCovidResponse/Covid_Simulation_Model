/*
 * Paul Bessell.
 * This Class initialises the model by generating a Population object and simulating Covid spread through the Populaiton
 */

// Testing some changes again
package uk.co.ramp.covid.simulation;

import com.google.gson.JsonParseException;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.Population;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RunModel {
    private static final Logger LOGGER = LogManager.getLogger(RunModel.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            LOGGER.error("Expected parameters: <population_params.json> <model_params.json> [rngseed]");
            System.exit(-1);
        } else {
            readParameters(args[0]);
            Model m  = Model.readModelFromFile(args[1]);

            if (args.length == 3) {
                m.setRNGSeed(Integer.parseInt(args[2]));
            }

            if (!m.isValid()) {
                LOGGER.error("Could not read model parameters");
                System.exit(-1);
            } else {
                m.run();
            }
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


}
