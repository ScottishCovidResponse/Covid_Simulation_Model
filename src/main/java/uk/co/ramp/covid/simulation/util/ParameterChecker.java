package uk.co.ramp.covid.simulation.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class ParameterChecker {
    private static final Logger LOGGER = LogManager.getLogger(ParameterChecker.class);

    public static boolean isValid (Object o) {
        try {
            return fieldsValid(o);
        } catch (IllegalAccessException e) {
            LOGGER.warn(e);
        }
        return false;
    }

    private static boolean fieldsValid (Object o) throws IllegalAccessException {
        boolean res = true;
        for (Field f : o.getClass().getFields()) {
            if (f.get(o) == null) {
                LOGGER.warn("Uninitialised parameter: " + f.getName());
                res = false;
            }
        }
        return res;
    }

    public static boolean isValidProbability(Double val, String name) {
        if(val < 0 || val > 1) {
            LOGGER.error(name + " is not a valid probability");
            return false;
        }
        return true;
    }
}
