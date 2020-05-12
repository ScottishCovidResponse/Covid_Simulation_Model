package uk.co.ramp.covid.simulation;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * PopulationParameters is a singleton class for reading and storing the covid disease parameters
 *
 * Note: This use of the singleton pattern is not thread safe
 */
public class CovidParameters {
    public static CovidParameters cparams = null;

    public static CovidParameters get() {
        if (cparams == null) {
            cparams = new CovidParameters();
        }
        return cparams;
    }

    private class DiseaseParameters {
        public int meanLatentPeriod;
        public int meanAsymptomaticPeriod;
        public int meanPhase1DurationMild;
        public int meanPhase1DurationSevere;
        public double mortalityRate;
        public double childProgressionPhase2;
        public double adultProgressionPhase2;
        public double pensionerProgressionPhase2;

        DiseaseParameters () {
            meanLatentPeriod = 7;
            meanAsymptomaticPeriod = 1;
            meanPhase1DurationMild = 5;
            meanPhase1DurationSevere = 10;
            mortalityRate = 0.01;
            childProgressionPhase2 = 0.02;
            adultProgressionPhase2 = 0.15;
            pensionerProgressionPhase2 = 0.8;
        }

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

    /** Read population data from JSON file */
    public static void readParametersFromFile(String path) throws IOException, JsonParseException {
        Reader file = new FileReader(path);
        Gson gson = new Gson();
        cparams = gson.fromJson(file, CovidParameters.class);
    }

    // Getters
    public int getMeanLatentPeriod () {
        return diseaseParameters.meanLatentPeriod;
    }

    public int getMeanAsymptomaticPeriod () {
        return diseaseParameters.meanAsymptomaticPeriod;
    }

    public int getMeanPhase1DurationMild () {
        return diseaseParameters.meanPhase1DurationMild;
    }

    public int getMeanPhase1DurationSevere () {
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
}
