package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

public class Adult extends Person {

    public enum Professions {
        OFFICE, SHOP, HOSPITAL, CONSTRUCTION, TEACHER, RESTAURANT, NONE
    }

    public Adult() {
        this.setProfession();
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

        Professions t = p.sample();

        switch(t) {
            case OFFICE: super.setOfficeWorker(true); break;
            case SHOP: super.setShopWorker(true); break;
            case HOSPITAL: super.setHospitalWorker(true); break;
            case CONSTRUCTION: super.setConstructionWorker(true); break;
            case TEACHER: super.setTeacher(true); break;
            case RESTAURANT: super.setRestaurant(true); break;
        }
    }
}
