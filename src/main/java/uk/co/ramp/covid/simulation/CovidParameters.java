package uk.co.ramp.covid.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.lang.reflect.Field;

/**
 * PopulationParameters is a singleton class for reading and storing the covid disease parameters
 *
 * Note: This use of the singleton pattern is not thread safe
 */
public class CovidParameters {
    private static final Logger LOGGER = LogManager.getLogger(CovidParameters.class);
    public static CovidParameters cparams = null;

    public static CovidParameters get() {
        if (cparams == null) {
            throw new InvalidParametersException("Invalid COVID parameters");
        }
        return cparams;
    }

    private static class DiseaseParameters {
        public Double meanLatentPeriod = null;
        public Double meanAsymptomaticPeriod = null;
        public Double meanPhase1DurationMild = null;
        public Double meanPhase1DurationSevere = null;

        public Double mortalityRate = null;
        public Double childProgressionPhase2 = null;
        public Double adultProgressionPhase2 = null;
        public Double pensionerProgressionPhase2 = null;

        @Override
        public String toString() {
            return "DiseaseParameters{" +
                    "meanLatentPeriod=" + meanLatentPeriod +
                    ", meanAsymptomaticPeriod=" + meanAsymptomaticPeriod +
                    ", meanPhase1DurationMild=" + meanPhase1DurationMild +
                    ", meanPhase1DurationSevere=" + meanPhase1DurationSevere +
                    ", mortalityRate=" + mortalityRate +
                    ", childProgressionPhase2=" + childProgressionPhase2 +
                    ", adultProgressionPhase2=" + adultProgressionPhase2 +
                    ", pensionerProgressionPhase2=" + pensionerProgressionPhase2 +
                    '}';
        }

    }

    private final DiseaseParameters diseaseParameters;

    public CovidParameters() {
        diseaseParameters = new DiseaseParameters();
    }

    public static void setParameters(CovidParameters p) {
        cparams = p;
    }
    public static void clearParameters() {
        cparams = null;
    }

    // Getters
    public double getMeanLatentPeriod () {
        return diseaseParameters.meanLatentPeriod;
    }

    public double getMeanAsymptomaticPeriod () {
        return diseaseParameters.meanAsymptomaticPeriod;
    }

    public double getMeanPhase1DurationMild () {
        return diseaseParameters.meanPhase1DurationMild;
    }

    public double getMeanPhase1DurationSevere () {
        return diseaseParameters.meanPhase1DurationSevere;
    }

    public double getMortalityRate () {
        return diseaseParameters.mortalityRate;
    }

    public double getChildProgressionPhase2 () {
        return diseaseParameters.childProgressionPhase2;
    }

    public double getAdultProgressionPhase2 () {
        return diseaseParameters.adultProgressionPhase2;
    }

    public double getPensionerProgressionPhase2 () {
        return diseaseParameters.pensionerProgressionPhase2;
    }

    @Override
    public String toString() {
        return "CovidParameters{" + "\n" +
                diseaseParameters + "\n" +
                '}';
    }

    public boolean isValid() {
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        return checker.isValid(diseaseParameters);
    }

    public static class ParameterInitialisedChecker {

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
}
