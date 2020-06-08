package uk.co.ramp.covid.simulation.parameters;

public class PlaceSizeDistribution {
    public Double pSmall = null;
    public Double pMed = null;
    public Double pLarge = null;

    public boolean isValid(String name) {
        /*
        double totalP = pSmall + pMed + pLarge;
        if (!(totalP <= 1 + PopulationParameters.EPSILON && totalP >= 1 - PopulationParameters.EPSILON)) {
            PopulationParameters.LOGGER.error("Building size parameters for " + name + " do not total one");
            return false;
        }
         */
        return true;
    }


}
