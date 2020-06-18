package uk.co.ramp.covid.simulation.parameters;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.DateRange;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.util.InvalidProbabilityException;
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

        JsonDeserializer<Probability> pdeserializer = (json, typeOfT, context) -> {
            double p = json.getAsDouble();
            try {
                return new Probability(p);
            } catch (InvalidProbabilityException e) {
                LOGGER.error(e);
                // There doesn't seem to be a way to get the field name here
                // Instead we return null and let the paramterInitiasedChecker print the error
                return null;
            }
        };

        JsonDeserializer<DateRange> dateRangeDeserializer = (json, typeOfT, context) -> {
            JsonObject o = json.getAsJsonObject();
            int s = o.get("start").getAsInt();
            int e = o.get("end").getAsInt();
            return new DateRange(Time.timeFromDay(s), Time.timeFromDay(e));
        };

        gson.registerTypeAdapter(Probability.class, pdeserializer);
        gson.registerTypeAdapter(DateRange.class, dateRangeDeserializer);
        ParameterIO r = gson.create().fromJson(file, ParameterIO.class);

        CovidParameters.setParameters(r.disease);
        PopulationParameters.setParameters(r.population);
    }

    public static void writeParametersToFile(Path outF) {
        GsonBuilder gson = new GsonBuilder().setPrettyPrinting();

        JsonSerializer<Probability> pserializer = (src, typeOfSrc, context) -> new JsonPrimitive(src.asDouble());

        JsonSerializer<DateRange> dateRangeSerializer = (src, typeOfSrc, context) -> {
            JsonObject o = new JsonObject();
            o.addProperty("start", src.getStart().getAbsDay());
            o.addProperty("end", src.getEnd().getAbsDay());
            return o;
        };

        gson.registerTypeAdapter(Probability.class, pserializer);
        gson.registerTypeAdapter(DateRange.class, dateRangeSerializer);

        ParameterIO params = new ParameterIO(CovidParameters.get(), PopulationParameters.get());

        try (Writer writer = new FileWriter(outF.toFile())) {
            gson.create().toJson(params, writer);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
