package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

import java.util.Objects;

// Probability an Adult works in a particular job
public class WorkerDistribution {
    /** Probabilities people work in particular jobs */
    public Probability pOffice = null;
    public Probability pShop = null;
    public Probability pHospital = null;
    public Probability pConstruction = null;
    public Probability pTeacher = null;
    public Probability pRestaurant = null;
    public Probability pUnemployed = null;
    public Probability pNursery = null;
    public Probability pCareHome = null;

    /** Determines how many workers are assigned to Large/med/small workplaces
     * For example 50%/30%/20% implies 50% of the workforce are in large offices
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerDistribution that = (WorkerDistribution) o;
        return Objects.equals(pOffice, that.pOffice) &&
                Objects.equals(pShop, that.pShop) &&
                Objects.equals(pHospital, that.pHospital) &&
                Objects.equals(pConstruction, that.pConstruction) &&
                Objects.equals(pTeacher, that.pTeacher) &&
                Objects.equals(pRestaurant, that.pRestaurant) &&
                Objects.equals(pUnemployed, that.pUnemployed) &&
                Objects.equals(pNursery, that.pNursery) &&
                Objects.equals(pCareHome, that.pCareHome) &&
                Objects.equals(sizeAllocation, that.sizeAllocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pOffice, pShop, pHospital, pConstruction, pTeacher, pRestaurant, pUnemployed, pNursery, pCareHome, sizeAllocation);
    }
}
