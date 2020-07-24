package uk.co.ramp.covid.simulation.util;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class Probability {
    private static final Logger LOGGER = LogManager.getLogger(Probability.class);

    private double p;

    public Probability(double p) {
        set(p);
    }
    
    public double asDouble() { return p; }
    
    public void set(double p) {
        if (p < 0.0 || p > 1.0) {
            throw new InvalidProbabilityException("Trying to initialise a probability with p = " + p);
        }
        this.p = p;
    }

    public Probability adjust(double adj) {
       return new Probability(p * adj);
    }
    
    public boolean sample() {
        return RNG.get().nextUniform(0.0,1.0) < p;
    }

    public static JsonDeserializer<Probability> deserializer = (json, typeOfT, context) -> {
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

    public static JsonSerializer<Probability> serializer =
            (src, typeOfSrc, context) -> new JsonPrimitive(src.asDouble());

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Probability that = (Probability) o;
        return Double.compare(that.p, p) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(p);
    }
}
