package uk.co.ramp.covid.simulation.parameters;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.lockdown.LockdownEventGenerator;
import uk.co.ramp.covid.simulation.lockdown.LockdownTypeMaps;
import uk.co.ramp.covid.simulation.place.OpeningTimes;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.*;

import java.io.*;
import java.nio.file.Path;

/** The ParameterIO class reads/writes parameters to/from a provided JSON input file */
public class ParameterIO {
    private static final Logger LOGGER = LogManager.getLogger(ParameterIO.class);
    
    private static Gson gson;

    private final CovidParameters disease;
    private final PopulationParameters population;

    public ParameterIO(CovidParameters disease, PopulationParameters population) {
        this.disease = disease;
        this.population = population;
    }

    /** Read population data from JSON file */
    private static void readParameters(Reader reader) {
        ParameterIO r = getGson().fromJson(reader, ParameterIO.class);
        CovidParameters.setParameters(r.disease);
        PopulationParameters.setParameters(r.population);
    }

    public static void readParametersFromFile(String path) throws IOException, JsonParseException {
        Reader r = new FileReader(path);
        readParameters(r);
    }

    public static void readParametersFromString(String json) throws JsonParseException {
        Reader r = new StringReader(json);
        readParameters(r);
    }

    public static void writeParametersToFile(Path outF) {
        ParameterIO params = new ParameterIO(CovidParameters.get(), PopulationParameters.get());

        try (Writer writer = new FileWriter(outF.toFile())) {
            getGson().toJson(params, writer);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /** Gets a Gson (de)-serializer with custom types enabled */
    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder g = new GsonBuilder().setPrettyPrinting();

            PolymorphicTypeDeserialiser<LockdownEvent> lockdownEvents =
                    new PolymorphicTypeDeserialiser<>(LockdownTypeMaps.getLockdownEventMap());
            PolymorphicTypeDeserialiser<LockdownEventGenerator> lockdownGenerators
                    = new PolymorphicTypeDeserialiser<>(LockdownTypeMaps.getLockdownEventGeneratorMap());
            
            g.registerTypeAdapter(Probability.class, Probability.deserializer);
            g.registerTypeAdapter(Probability.class, Probability.serializer);
            g.registerTypeAdapter(DateRange.class, DateRange.deserializer);
            g.registerTypeAdapter(DateRange.class, DateRange.serializer);
            g.registerTypeAdapter(LockdownEvent.class, lockdownEvents);
            g.registerTypeAdapter(LockdownEventGenerator.class, lockdownGenerators);
            g.registerTypeAdapter(Time.class, Time.deserializer);
            g.registerTypeAdapter(Time.class, Time.serializer);
            g.registerTypeAdapter(OpeningTimes.class, OpeningTimes.deserializer);
            g.registerTypeAdapter(ShiftAllocator.class, ShiftAllocator.deserializer);
            g.registerTypeAdapter(Shifts.class, Shifts.deserializer);
            
            gson = g.create();

            lockdownEvents.setGson(gson);
            lockdownGenerators.setGson(gson);
        } 
        return gson;
    }

    public static String writeParametersToString() {
        ParameterIO params = new ParameterIO(CovidParameters.get(), PopulationParameters.get());
        Writer writer = new StringWriter();
        getGson().toJson(params, writer);
        return writer.toString();
    }
}
