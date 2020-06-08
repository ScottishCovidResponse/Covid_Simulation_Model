package uk.co.ramp.covid.simulation.parameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.io.ParameterReader;

import java.lang.reflect.Field;

public class ParameterInitialisedChecker {
    private static final Logger LOGGER = LogManager.getLogger(ParameterInitialisedChecker.class);

    public boolean isValid (Object o) {
        try {
            return fieldsValid(o);
        } catch (IllegalAccessException e) {
            LOGGER.warn(e);
        }
        return false;
    }

    private boolean fieldsValid (Object o) throws IllegalAccessException {
        boolean res = true;
        for (Field f : o.getClass().getFields()) {
            if (f.get(o) == null) {
                LOGGER.warn("Uninitialised parameter: " + f.getName());
                res = false;
            }
        }
        return res;
    }
}
