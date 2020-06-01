package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.covid.CovidParameters;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

public class Adult extends Person {

    public enum Professions {
        OFFICE, SHOP, HOSPITAL, CONSTRUCTION, TEACHER, RESTAURANT, NURSERY, NONE
    }

    Professions profession;

    public Adult(int age, Sex sex) {
        super(age, sex);
        if (age >= 65 || age < 18) {
            throw new InvalidAgeException("Trying to create an adult outside the correct age range (18-64)");
        }
        setProfession();
    }

    // Allocates adults to different professions
    public void setProfession() {
        ProbabilityDistribution<Professions> p = new ProbabilityDistribution<Professions>();
        p.add(PopulationParameters.get().getpOfficeWorker(), Professions.OFFICE);
        p.add(PopulationParameters.get().getpShopWorker(), Professions.SHOP);
        p.add(PopulationParameters.get().getpHospitalWorker(), Professions.HOSPITAL);
        p.add(PopulationParameters.get().getpConstructionWorker(), Professions.CONSTRUCTION);
        p.add(PopulationParameters.get().getpTeacher(), Professions.TEACHER);
        p.add(PopulationParameters.get().getpRestaurantWorker(), Professions.RESTAURANT);
        p.add(PopulationParameters.get().getpNurseryWorker(), Professions.NURSERY);
        p.add(PopulationParameters.get().getpUnemployed(), Professions.NONE);

        profession = p.sample();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsAdult();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsAdult();
    }

    @Override
    public void allocateCommunalPlace(Places p) {
        switch(profession) {
            case TEACHER: {
                School s = p.getNextSchoolJob();
                setPrimaryPlace(s);
            } break;
            case NURSERY: {
                Nursery s = p.getNextNurseryJob();
                setPrimaryPlace(s);
            } break;
            case SHOP: {
                Shop s = p.getNextShopJob();
                setPrimaryPlace(s);
            } break;
            case CONSTRUCTION: {
                ConstructionSite s = p.getNextConstructionSiteJob();
                setPrimaryPlace(s);
            } break;
            case OFFICE: {
                Office s = p.getNextOfficeJob();
                setPrimaryPlace(s);
            } break;
            case HOSPITAL: {
                Hospital h = p.getNextHospitalJob();
                setPrimaryPlace(h);
            } break;
            case RESTAURANT: {
                Restaurant r = p.getNextRestaurantJob();
                setPrimaryPlace(r);
            } break;
        }
        
        if (getPrimaryCommunalPlace() != null) {
            shifts = getPrimaryCommunalPlace().getShifts();
        }
    }

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().getAdultProgressionPhase2();
    }
}
