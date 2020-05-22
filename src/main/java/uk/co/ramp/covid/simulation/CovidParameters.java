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
        public Double probabilitySymptoms = null;
        public Double meanSymptomDelay = null;
        public Double meanInfectiousDuration = null;
        public Double phase1Betaa = null;
        public Double phase1Betab = null;        
        public Double mortalityRate = null;
        public Double childProgressionPhase2 = null;
        public Double adultProgressionPhase2 = null;
        public Double pensionerProgressionPhase2 = null;

        @Override
        public String toString() {
            return "DiseaseParameters{" +
                    "meanLatentPeriod=" + meanLatentPeriod +
                    ", meanAsymptomaticPeriod=" + meanAsymptomaticPeriod +
                    ", probabilitySymptoms=" + probabilitySymptoms +
                    ", meanSymptomDelay=" + meanSymptomDelay +
                    ", meanInfectiousDuration=" + meanInfectiousDuration +
                    ", phase1Betaa=" + phase1Betaa +
                    ", phase1Betab=" + phase1Betab +                    
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

    public double getSymptomProbability () {
        return diseaseParameters.probabilitySymptoms;
    }

    public double getSymptomDelay () {
        return diseaseParameters.meanSymptomDelay;
    }

    public double getInfectiousPeriod () {
        return diseaseParameters.meanInfectiousDuration;
    }

    public double getphase1Betaa () {
        return diseaseParameters.phase1Betaa;
    }

    public double getphase1Betab () {
        return diseaseParameters.phase1Betab;
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
