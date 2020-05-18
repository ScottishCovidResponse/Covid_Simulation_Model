package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

public class Adult extends Person {

    public enum Professions {
        OFFICE, SHOP, HOSPITAL, CONSTRUCTION, TEACHER, RESTAURANT, NONE
    }

    Professions profession;

    public Adult() { setProfession(); }

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
    public void allocateCommunalPlace(Population p) {
        switch(profession) {
            case TEACHER: {
                CommunalPlace property = p.getRandomPlace();
                while (!(property instanceof School)) property = p.getRandomPlace();
                this.setMIndex(property.getIndex());
            } break;
            case SHOP: {
                CommunalPlace property = p.getRandomPlace();
                while (!(property instanceof Shop)) property = p.getRandomPlace();
                this.setMIndex(property.getIndex());
            } break;
            case CONSTRUCTION: {
                CommunalPlace property = p.getRandomPlace();
                while (!(property instanceof ConstructionSite)) property = p.getRandomPlace();
                this.setMIndex(property.getIndex());
            } break;
            case OFFICE: {
                CommunalPlace property = p.getRandomPlace();
                while (!(property instanceof Office)) property = p.getRandomPlace();
                this.setMIndex(property.getIndex());
            } break;
            case HOSPITAL: {
                CommunalPlace property = p.getRandomPlace();
                while (!(property instanceof Hospital)) property = p.getRandomPlace();
                this.setMIndex(property.getIndex());
            } break;
            case RESTAURANT: {
                CommunalPlace property = p.getRandomPlace();
                while (!(property instanceof Restaurant)) property = p.getRandomPlace();
                this.setMIndex(property.getIndex());
            } break;
        }
    }
}
