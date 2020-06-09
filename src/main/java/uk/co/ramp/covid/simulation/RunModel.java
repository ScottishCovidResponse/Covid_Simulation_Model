/*
 * Paul Bessell.
 * This Class initialises the model by generating a Population object and simulating Covid spread through the Populaiton
 */

// Testing some changes again
package uk.co.ramp.covid.simulation;

import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.ParameterReader;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;

import java.io.IOException;

public class RunModel {
    private static final Logger LOGGER = LogManager.getLogger(RunModel.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            LOGGER.error("Expected parameters: <population_params.json> <model_params.json> <simulationID>");
            System.exit(-1);
        } else {
            String populationParamsFile = args[0];
            String modelParamsFile = args[1];
            int simulationID = Integer.parseInt(args[2]);
            
            readParameters(populationParamsFile);

            if (!PopulationParameters.get().isValid()) {
                LOGGER.error("Could not read population parameters");
                System.exit(-2);
            }

            if (!CovidParameters.get().isValid()) {
                LOGGER.error("Could not read disease parameters");
                System.exit(-2);
            }

            Model m  = Model.readModelFromFile(modelParamsFile);
            m.optionallyGenerateRNGSeed();

            if (!m.isValid()) {
                LOGGER.error("Could not read model parameters");
                System.exit(-2);
            } else {
                m.run(simulationID);
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
