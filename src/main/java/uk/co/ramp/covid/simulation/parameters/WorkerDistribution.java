package uk.co.ramp.covid.simulation.parameters;

// Probability an Adult works in a particular job
public class WorkerDistribution {
    public Double pOffice = null;
    public Double pShop = null;
    public Double pHospital = null;
    public Double pConstruction = null;
    public Double pTeacher = null;
    public Double pRestaurant = null;
    public Double pUnemployed = null;
    public Double pNursery = null;

    public PlaceSizeDistribution sizeAllocation = null;

    public boolean isValid() {
        /*
        boolean probabilitiesValid = PopulationParameters.isValidProbability(pOffice, "pOffice")
                && PopulationParameters.isValidProbability(pShop, "pShop")
                && PopulationParameters.isValidProbability(pHospital, "pHospital")
                && PopulationParameters.isValidProbability(pConstruction, "pConstruction")
                && PopulationParameters.isValidProbability(pTeacher, "pTeacher")
                && PopulationParameters.isValidProbability(pRestaurant, "pRestaurant")
                && PopulationParameters.isValidProbability(pNursery, "pNursery")
                && PopulationParameters.isValidProbability(pUnemployed, "pUnemployed");

        double totalP = pOffice + pShop + pHospital + pConstruction + pTeacher + pRestaurant + pNursery + pUnemployed;
        if(!(totalP <= 1 + PopulationParameters.EPSILON && totalP >= 1 - PopulationParameters.EPSILON)) {
            PopulationParameters.LOGGER.error("Worker allocation parameter probabilities do not total one");
            return false;
        }
        */

        return true;
    }
}
