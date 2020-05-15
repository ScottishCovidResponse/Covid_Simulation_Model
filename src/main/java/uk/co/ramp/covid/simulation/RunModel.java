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
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RunModel {

    private static final Logger LOGGER = LogManager.getLogger(RunModel.class);
    private static final Map<Integer, RandomDataGenerator> map = new HashMap<>();
    private static int sid;

    public RunModel() {
    }

    //For testing
    public RunModel(int sid) {
        this.sid = sid;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            LOGGER.error("Expected parameters: <population_params.json> <model_params.json>");
            System.exit(-1);
        } else {
            readParameters(args[0]);

            if (!PopulationParameters.get().isValid()) {
                LOGGER.error("Could not read population parameters");
                System.exit(-2);
            }

            if (!CovidParameters.get().isValid()) {
                LOGGER.error("Could not read disease parameters");
                System.exit(-2);
            }

            Model m  = Model.readModelFromFile(args[1]);
            if (!m.isValid()) {
                LOGGER.error("Could not read model parameters");
                System.exit(-2);
            } else {
                m.run();
            }
        }
        sid = 123;
    }


    public static RandomDataGenerator getRng() {
        map.computeIfAbsent(sid, f -> {
            RandomDataGenerator rnd = new RandomDataGenerator();
            rnd.reSeed(sid);
            return rnd;
        });
        return map.get(sid);
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
