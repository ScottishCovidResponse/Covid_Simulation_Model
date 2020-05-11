package uk.co.ramp.covid.simulation.population;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private double pInfants;
    private double pChildren;
    private double pAdults;
    private double pPensioners;

    // Household populations
    // These values define the probability of a household being an adult only, adult and child household etc
    private double pAdultOnly;
    private double pPensionerOnly;
    private double pPensionerAdult;
    private double pAdultChildren;

    // Household allocation probabilities based on household size and type
    private Map<Integer, Double> adultAllocationPMap;
    private Map<Integer, Double> pensionerAllocationPMap;
    private Map<Integer, Double> childAllocationPMap;
    private Map<Integer, Double> infantAllocationPMap;

    private PopulationParameters() {
        pInfants = 0.08;
        pChildren = 0.2;
        pAdults = 0.5;
        pPensioners = 1 - this.pInfants - this.pChildren - this.pAdults;

        LOGGER.info("Population proportions pensioners = {} Adults = {} Children = {} Infants = {}",
                pPensioners, pAdults, pChildren, pInfants);

        pAdultOnly = 0.3;
        pPensionerOnly = 0.1;
        pPensionerAdult = 0.1;
        pAdultChildren = 0.5;

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

    public static PopulationParameters get() {
        if (pp == null) {
            pp = new PopulationParameters();
        }
        return pp;
    }

    public double getpInfants() {
        return pInfants;
    }

    public double getpChildren() {
        return pChildren;
    }

    public double getpAdults() {
        return pAdults;
    }

    public double getpPensioners() {
        return pPensioners;
    }

    public double getpAdultOnly() {
        return pAdultOnly;
    }

    public double getpPensionerOnly() {
        return pPensionerOnly;
    }

    public double getpPensionerAdult() {
        return pPensionerAdult;
    }

    public double getpAdultChildren() {
        return pAdultChildren;
    }

    public Map<Integer, Double> getAdultAllocationPMap() {
        return adultAllocationPMap;
    }

    public Map<Integer, Double> getPensionerAllocationPMap() {
        return pensionerAllocationPMap;
    }

    public Map<Integer, Double> getChildAllocationPMap() {
        return childAllocationPMap;
    }

    public Map<Integer, Double> getInfantAllocationPMap() {
        return infantAllocationPMap;
    }

}
