package uk.co.ramp.covid.simulation.parameters;

public class InfantProperties {
    public Double pAttendsNursery = null;

    public boolean isValid() {
        //return PopulationParameters.isValidProbability(pAttendsNursery, "pAttendsNursery");
        return true;
    }
}
