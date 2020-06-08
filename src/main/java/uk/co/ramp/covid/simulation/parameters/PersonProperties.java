package uk.co.ramp.covid.simulation.parameters;

public class PersonProperties {
    public Double pQuarantine = null;
    public Double pTransmission = null;

    public boolean isValid() {
        return true;
        /*return PopulationParameters.isValidProbability(pQuarantine, "pQuarantine")
                && PopulationParameters.isValidProbability(pTransmission, "pTransmission");

         */
    }
}
