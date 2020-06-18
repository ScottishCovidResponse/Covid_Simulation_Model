package uk.co.ramp.covid.simulation.parameters;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.DateRange;
import uk.co.ramp.covid.simulation.util.Probability;

import java.io.*;
import java.nio.file.Path;

/** The ParameterIO class reads/writes parameters to/from a provided JSON input file */
public class ParameterIO {
    private static final Logger LOGGER = LogManager.getLogger(ParameterIO.class);

    private final CovidParameters disease;
    private final PopulationParameters population;

    public ParameterIO(CovidParameters disease, PopulationParameters population) {
        this.disease = disease;
        this.population = population;
    }

    /** Read population data from JSON file */
    public static void readParametersFromFile(String path) throws IOException, JsonParseException {
        Reader file = new FileReader(path);
        GsonBuilder gson = new GsonBuilder();

        gson.registerTypeAdapter(Probability.class, Probability.deserializer);
        gson.registerTypeAdapter(DateRange.class, DateRange.deserializer);
        ParameterIO r = gson.create().fromJson(file, ParameterIO.class);

        CovidParameters.setParameters(r.disease);
        PopulationParameters.setParameters(r.population);
    }

    public static void writeParametersToFile(Path outF) {
        GsonBuilder gson = new GsonBuilder().setPrettyPrinting();

        gson.registerTypeAdapter(Probability.class, Probability.serializer);
        gson.registerTypeAdapter(DateRange.class, DateRange.serializer);

        ParameterIO params = new ParameterIO(CovidParameters.get(), PopulationParameters.get());

        try (Writer writer = new FileWriter(outF.toFile())) {
            gson.create().toJson(params, writer);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
