package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

// Probability an Adult works in a particular job
public class WorkerDistribution {
    public Probability pOffice = null;
    public Probability pShop = null;
    public Probability pHospital = null;
    public Probability pConstruction = null;
    public Probability pTeacher = null;
    public Probability pRestaurant = null;
    public Probability pUnemployed = null;
    public Probability pNursery = null;
    public Probability pCareHome = null;

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
        p.add(pCareHome, Adult.Professions.CAREHOME);
        p.add(pUnemployed, Adult.Professions.NONE);
        return p;
    }


    public boolean isValid() {
        return professionDistribution().isValid();
    }
}
