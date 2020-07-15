package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class PensionerProperties {
    /** Anyone >= minAgeToEnterCare may be assigned a CareHome with probability pEntersCareHome */
    public Integer minAgeToEnterCare = null;
    public Probability pEntersCareHome = null;
    
    public boolean isValid() {
        return minAgeToEnterCare >= 65 && minAgeToEnterCare <= 100;
    }
}
