package uk.co.ramp.covid.simulation.parameters;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.Probability;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public class ParameterWriter {
    private static final Logger LOGGER = LogManager.getLogger(ParameterWriter.class);

    private CovidParameters disease;
    private PopulationParameters population;

    ParameterWriter(CovidParameters c, PopulationParameters p) {
        disease = c;
        population = p;
    }

    public static void writeParameersToFile(Path outF) {
        GsonBuilder gson = new GsonBuilder().setPrettyPrinting();

        JsonSerializer<Probability> pserializer = (src, typeOfSrc, context) -> new JsonPrimitive(src.asDouble());
        gson.registerTypeAdapter(Probability.class, pserializer);
        
        ParameterWriter params = new ParameterWriter (CovidParameters.get(), PopulationParameters.get());

        try (Writer writer = new FileWriter(outF.toFile())) {
            gson.create().toJson(params, writer);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}