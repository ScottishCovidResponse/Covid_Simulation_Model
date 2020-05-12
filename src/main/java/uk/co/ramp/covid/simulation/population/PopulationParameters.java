package uk.co.ramp.covid.simulation.population;


import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * PopulationParameters is a singleton class for reading and storing the population parameters
 * used to perform allocation (to household etc).
 *
 * Note: This use of the singleton pattern is not thread safe
 */
public class PopulationParameters {
    private static final Logger LOGGER = LogManager.getLogger(Population.class);
    private static PopulationParameters pp = null;

    // Proportions of each type of person in the population
    private class Population {
        public double pInfants;
        public double pChildren;
        public double pAdults;
        public double pPensioners;

        public Population() {
            // Default parameters
            pInfants = 0.08;
            pChildren = 0.2;
            pAdults = 0.5;
            pPensioners = 1 - pInfants - pChildren - pAdults;
        }
    }

    // Household populations
    // These values define the probability of a household being an adult only, adult and child household etc
    private class Households {
        public double pAdultOnly;
        public double pPensionerOnly;
        public double pPensionerAdult;
        public double pAdultChildren;
        public double pPensionerChildren;
        public double pAdultPensionerChildren;

        public Households() {
            // Defeault paramerts
            pAdultOnly = 0.3;
            pPensionerOnly = 0.1;
            pPensionerAdult = 0.1;
            pAdultChildren = 0.3;
            pPensionerChildren = 0.1;
            pAdultPensionerChildren = 0.1;
        }
    }

    // Household allocation probabilities based on household size and type
    private class AdditionalMembersDistributions {
        public Map<Integer, Double> adultAllocationPMap;
        public Map<Integer, Double> pensionerAllocationPMap;
        public Map<Integer, Double> childAllocationPMap;
        public Map<Integer, Double> infantAllocationPMap;

        public AdditionalMembersDistributions() {
            // Default parameters
            adultAllocationPMap = new HashMap<>();
            adultAllocationPMap.put(1, 0.8);
            adultAllocationPMap.put(2, 0.5);
            adultAllocationPMap.put(3, 0.3);
            adultAllocationPMap.put(4, 0.2);
            adultAllocationPMap.put(5, 0.1);

            pensionerAllocationPMap = new HashMap<>();
            pensionerAllocationPMap.put(1, 0.8);
            pensionerAllocationPMap.put(2, 0.5);
            pensionerAllocationPMap.put(3, 0.3);
            pensionerAllocationPMap.put(4, 0.2);
            pensionerAllocationPMap.put(5, 0.1);

            childAllocationPMap = new HashMap<>();
            childAllocationPMap.put(1, 0.8);
            childAllocationPMap.put(2, 0.5);
            childAllocationPMap.put(3, 0.3);
            childAllocationPMap.put(4, 0.2);
            childAllocationPMap.put(5, 0.1);

            infantAllocationPMap = new HashMap<>();
            infantAllocationPMap.put(1, 0.8);
            infantAllocationPMap.put(2, 0.5);
            infantAllocationPMap.put(3, 0.3);
            infantAllocationPMap.put(4, 0.2);
            infantAllocationPMap.put(5, 0.1);
        }
    }

    private final Population population;
    private final Households households;
    private final AdditionalMembersDistributions additionalMembersDistributions;

    private PopulationParameters() {
        population = new Population();
        households = new Households();
        additionalMembersDistributions = new AdditionalMembersDistributions();

        LOGGER.info("Population proportions pensioners = {} Adults = {} Children = {} Infants = {}",
                population.pPensioners, population.pAdults, population.pChildren, population.pInfants);
    }

    public static PopulationParameters get() {
        if (pp == null) {
            pp = new PopulationParameters();
        }
        return pp;
    }

    /** Read population data from JSON file */
    public static void readParametersFromFile(String path) throws IOException {
        Reader file = new FileReader(path);
        Gson gson = new Gson();
        gson.fromJson(file, PopulationParameters.class);
    }

    public static void printJSON() {
        Gson gson = new Gson();
        String j = gson.toJson(PopulationParameters.get());
        System.out.println(j);
    }

    public double getpInfants() {
        return population.pInfants;
    }

    public double getpChildren() {
        return population.pChildren;
    }

    public double getpAdults() {
        return population.pAdults;
    }

    public double getpopulationensioners() {
        return population.pPensioners;
    }

    public double getpAdultOnly() {
        return households.pAdultOnly;
    }

    public double getpPensionerOnly() {
        return households.pPensionerOnly;
    }

    public double getpPensionerAdult() {
        return households.pPensionerAdult;
    }

    public double getpAdultChildren() {
        return households.pAdultChildren;
    }

    public double getpPensionerChildren() {
        return households.pPensionerChildren;
    }

    public double getpAdultPensionerChildren() {
        return households.pAdultPensionerChildren;
    }

    public Map<Integer, Double> getAdultAllocationPMap() {
        return additionalMembersDistributions.adultAllocationPMap;
    }

    public Map<Integer, Double> getPensionerAllocationPMap() {
        return additionalMembersDistributions.pensionerAllocationPMap;
    }

    public Map<Integer, Double> getChildAllocationPMap() {
        return additionalMembersDistributions.childAllocationPMap;
    }

    public Map<Integer, Double> getInfantAllocationPMap() {
        return additionalMembersDistributions.infantAllocationPMap;
    }
}
