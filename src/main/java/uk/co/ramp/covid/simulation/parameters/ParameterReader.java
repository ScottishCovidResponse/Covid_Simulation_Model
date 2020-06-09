package uk.co.ramp.covid.simulation.parameters;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.InvalidProbabilityException;
import uk.co.ramp.covid.simulation.util.Probability;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/** The ParameterReader class reads parameters from a provided JSON input file and
 * creates the static parameter classes */
public class ParameterReader {
    private static final Logger LOGGER = LogManager.getLogger(ParameterReader.class);

    private CovidParameters disease;
    private PopulationParameters population;

    /** Read population data from JSON file */
    public static void readParametersFromFile(String path) throws IOException, JsonParseException {
        Reader file = new FileReader(path);
        GsonBuilder gson = new GsonBuilder();

        JsonDeserializer<Probability> pdeserializer = (json, typeOfT, context) -> {
            Double p = json.getAsDouble();
            try {
                Probability res = new Probability(p);
                return res;
            } catch (InvalidProbabilityException e) {
                LOGGER.error(e);
                // There doesn't seem to be a way to get the field name here
                // Instead we return null and let the paramterInitiasedChecker print the error
                return null;
            }
        };

        gson.registerTypeAdapter(Probability.class, pdeserializer);
        ParameterReader r = gson.create().fromJson(file, ParameterReader.class);

        CovidParameters.setParameters(r.disease);
        PopulationParameters.setParameters(r.population);
    }
}
