package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

import java.util.function.Function;

// Household populations
// These values define the probability of a household being an adult only, adult and child household etc
public class HouseholdDistribution {
    public Double householdRatio = null;

    public Double pSingleAdult = null;
    public Double pSmallAdult = null;
    public Double pSingleParent = null;
    public Double pSmallFamily = null;
    public Double pLargeTwoAdultFamily = null;
    public Double pLargeManyAdultFamily = null;
    public Double pLargeAdult = null;
    public Double pAdultPensioner = null;
    public Double pDoubleOlder = null;
    public Double pSingleOlder = null;

    public ProbabilityDistribution<Function<Places, Household>> householdTypeDistribution() {
        ProbabilityDistribution<Function<Places, Household>> p = new ProbabilityDistribution<>();
        p.add(pSingleAdult, SingleAdult::new);
        p.add(pSmallAdult, SmallAdult::new);
        p.add(pSingleParent, SingleParent::new);
        p.add(pSmallFamily, SmallFamily::new);
        p.add(pLargeTwoAdultFamily, LargeTwoAdultFamiy::new);
        p.add(pLargeManyAdultFamily, LargeManyAdultFamily::new);
        p.add(pLargeAdult, LargeAdult::new);
        p.add(pAdultPensioner, AdultPensioner::new);
        p.add(pDoubleOlder, DoubleOlder::new);
        p.add(pSingleOlder, SingleOlder::new);
        return p;
    }

    public boolean isValid() {
        return householdTypeDistribution().isValid() && householdRatio >= 1;
        /*
        boolean probabilitiesValid = PopulationParameters.isValidProbability(pSingleAdult, "pSingleAdult")
                && PopulationParameters.isValidProbability(pSmallAdult, "pSmallAdult")
                && PopulationParameters.isValidProbability(pSingleParent, "pSingleParent")
                && PopulationParameters.isValidProbability(pSmallFamily, "pSmallFamily")
                && PopulationParameters.isValidProbability(pLargeTwoAdultFamily, "pLargeTwoAdultFamily")
                && PopulationParameters.isValidProbability(pLargeManyAdultFamily, "pLargeManyAdultFamily")
                && PopulationParameters.isValidProbability(pLargeAdult, "pLargeAdult")
                && PopulationParameters.isValidProbability(pAdultPensioner, "pAdultPensioner")
                && PopulationParameters.isValidProbability(pDoubleOlder, "pDoubleOlder")
                && PopulationParameters.isValidProbability(pSingleOlder, "pSingleOlder");
        
        return probabilitiesValid;
        */
    }

}
