package uk.co.ramp.covid.simulation.io;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.CovidParameters;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/** The ParameterReader class reads parameters from a provided JSON input file and
 * creates the static parameter classes */
public class ParameterReader {

    /** Read population data from JSON file */
    public static void readParametersFromFile(String path) throws IOException, JsonParseException {
        Reader file = new FileReader(path);
        Gson gson = new Gson();

        CovidParameters cparams = gson.fromJson(file, CovidParameters.class);
        PopulationParameters pparams = gson.fromJson(file, PopulationParameters.class);

        CovidParameters.setParameters(cparams);
        PopulationParameters.setParameters(pparams);
    }
}
