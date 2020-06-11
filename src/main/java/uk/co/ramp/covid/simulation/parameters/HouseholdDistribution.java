package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

import java.util.function.Supplier;

// Household populations
// These values define the probability of a household being an adult only, adult and child household etc
public class HouseholdDistribution {
    public Double householdRatio = null;

    public Probability pSingleAdult = null;
    public Probability pSmallAdult = null;
    public Probability pSingleParent = null;
    public Probability pSmallFamily = null;
    public Probability pLargeTwoAdultFamily = null;
    public Probability pLargeManyAdultFamily = null;
    public Probability pLargeAdult = null;
    public Probability pAdultPensioner = null;
    public Probability pDoubleOlder = null;
    public Probability pSingleOlder = null;

    public ProbabilityDistribution<Supplier<Household>> householdTypeDistribution() {
        ProbabilityDistribution<Supplier<Household>> p = new ProbabilityDistribution<>();
        p.add(pSingleAdult, SingleAdult::new);
        p.add(pSmallAdult, SmallAdult::new);
        p.add(pSingleParent, SingleParent::new);
        p.add(pSmallFamily, SmallFamily::new);
        p.add(pLargeTwoAdultFamily, LargeTwoAdultFamily::new);
        p.add(pLargeManyAdultFamily, LargeManyAdultFamily::new);
        p.add(pLargeAdult, LargeAdult::new);
        p.add(pAdultPensioner, AdultPensioner::new);
        p.add(pDoubleOlder, DoubleOlder::new);
        p.add(pSingleOlder, SingleOlder::new);
        return p;
    }

    public boolean isValid() {
        return householdTypeDistribution().isValid() && householdRatio >= 1;
    }

}
