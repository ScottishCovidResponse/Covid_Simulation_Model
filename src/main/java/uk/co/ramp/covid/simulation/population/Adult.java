package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.CovidParameters;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

public class Adult extends Person {

    public enum Professions {
        OFFICE, SHOP, HOSPITAL, CONSTRUCTION, TEACHER, RESTAURANT, NONE
    }

    Professions profession;

    public Adult() {
        setProfession();
        shifts = Shifts.getAllTimes();
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
        p.add(PopulationParameters.get().getpUnemployed(), Professions.NONE);

        profession = p.sample();
        // There is special logic for shop workers returning home
        if (profession == Professions.SHOP) {
            super.setShopWorker();
        }
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
                setPrimaryPlace(p.getRandomSchool());
            } break;
            case SHOP: {
                setPrimaryPlace(p.getRandomShop());
            } break;
            case CONSTRUCTION: {
                setPrimaryPlace(p.getRandomConstructionSite());
            } break;
            case OFFICE: {
                setPrimaryPlace(p.getRandomOffice());
            } break;
            case HOSPITAL: {
                setPrimaryPlace(p.getRandomHospital());
            } break;
            case RESTAURANT: {
                setPrimaryPlace(p.getRandomRestaurant());
            } break;
        }
    }

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().getAdultProgressionPhase2();
    }
}
