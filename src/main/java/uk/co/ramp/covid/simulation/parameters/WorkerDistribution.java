package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

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

    public ProbabilityDistribution<Adult.Professions> professionDistribution() {
        ProbabilityDistribution<Adult.Professions> p = new ProbabilityDistribution<>();
        p.add(pOffice, Adult.Professions.OFFICE);
        p.add(pShop, Adult.Professions.SHOP);
        p.add(pHospital, Adult.Professions.HOSPITAL);
        p.add(pConstruction, Adult.Professions.CONSTRUCTION);
        p.add(pTeacher, Adult.Professions.TEACHER);
        p.add(pRestaurant, Adult.Professions.RESTAURANT);
        p.add(pNursery, Adult.Professions.NURSERY);
        p.add(pUnemployed, Adult.Professions.NONE);
        return p;
    }


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
        */

        return professionDistribution().isValid();
    }
}
