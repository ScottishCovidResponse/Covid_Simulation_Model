package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
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
        ProbabilityDistribution<Professions> p = PopulationParameters.get().workerDistribution.professionDistribution();
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
        CommunalPlace primaryPlace = null;
        switch(profession) {
            case TEACHER: {
                primaryPlace = p.getRandomSchool();
            } break;
            case NURSERY: {
                primaryPlace = p.getRandomNursery();
            } break;
            case SHOP: {
                primaryPlace = p.getRandomShop();
            } break;
            case CONSTRUCTION: {
                primaryPlace = p.getRandomConstructionSite();
            } break;
            case OFFICE: {
                primaryPlace = p.getRandomOffice();
            } break;
            case HOSPITAL: {
                primaryPlace = p.getRandomHospital();
            } break;
            case RESTAURANT: {
                primaryPlace = p.getRandomRestaurant();
            } break;
            case NONE: {
                // do nothing
            }
        }
        if (primaryPlace != null) {
            setPrimaryPlace(primaryPlace);
            shifts = primaryPlace.getShifts();
        }
    }

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().diseaseParameters.adultProgressionPhase2;
    }
}
