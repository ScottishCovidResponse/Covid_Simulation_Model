package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** Helper class to manage communal places of particular types */
public class Places {

    private ProbabilityDistribution<Office> offices;
    private ProbabilityDistribution<ConstructionSite> constructionSites;
    private ProbabilityDistribution<Hospital> hospitals;
    private ProbabilityDistribution<Nursery> nurseries;
    private ProbabilityDistribution<Restaurant> restaurants;
    private ProbabilityDistribution<School> schools;
    private ProbabilityDistribution<Shop> shops;
    
    private boolean officesUnallocated = false;
    private boolean constructionSitesUnallocated = false;
    private boolean hospitalsUnallocated = false;
    private boolean nurseriesUnallocated = false;
    private boolean restaurantsUnallocated = false;
    private boolean schoolsUnallocated = false;
    private boolean shopsUnallocated = false;

    private List<CommunalPlace> all;

    public Places() {
        offices = new ProbabilityDistribution<>();
        constructionSites = new ProbabilityDistribution<>();
        hospitals = new ProbabilityDistribution<>();
        nurseries = new ProbabilityDistribution<>();
        restaurants = new ProbabilityDistribution<>();
        schools = new ProbabilityDistribution<>();
        shops = new ProbabilityDistribution<>();
        all = new ArrayList<>();
    }

    public Office getRandomOffice() {
        return offices.sample();
    }

    public ConstructionSite getRandomConstructionSite() {
        return constructionSites.sample();
    }

    public Hospital getRandomHospital() {
        return hospitals.sample();
    }

    public Nursery getRandomNursery() {
        return nurseries.sample();
    }

    public Restaurant getRandomRestaurant() {
        return restaurants.sample();
    }

    public School getRandomSchool() {
        return schools.sample();
    }

    public Shop getRandomShop() {
        return shops.sample();
    }
    
    private <T extends CommunalPlace> T getNextWorkplace(ProbabilityDistribution<T> places,
                                           Supplier<Boolean> getUnallocated,
                                           Consumer<Boolean> setAllocated,
                                           Supplier<T> rand) {
        if (getUnallocated.get()) {
            for (T p : places.toList()) {
                if (!p.isFullyStaffed()) {
                    return p;
                }
            }
            setAllocated.accept(true);
        }
        return rand.get();
    }
    
    public Office getNextOfficeJob() {
        return getNextWorkplace(offices, () -> officesUnallocated,
                o -> officesUnallocated = o, this::getRandomOffice);
    }

    public School getNextSchoolJob() {
        return getNextWorkplace(schools, () -> schoolsUnallocated,
                o -> schoolsUnallocated = o, this::getRandomSchool);
    }

    public ConstructionSite getNextConstructionSiteJob() {
        return getNextWorkplace(constructionSites, () -> constructionSitesUnallocated,
                o -> constructionSitesUnallocated = o, this::getRandomConstructionSite);
    }

    public Nursery getNextNurseryJob() {
        return getNextWorkplace(nurseries, () -> nurseriesUnallocated,
                o -> nurseriesUnallocated = o, this::getRandomNursery);
    }

    public Shop getNextShopJob() {
        return getNextWorkplace(shops, () -> shopsUnallocated,
                o -> shopsUnallocated = o, this::getRandomShop);
    }

    public Hospital getNextHospitalJob() {
        return getNextWorkplace(hospitals, () -> hospitalsUnallocated,
                o -> hospitalsUnallocated = o, this::getRandomHospital);
    }

    public Restaurant getNextRestaurantJob() {
        return getNextWorkplace(restaurants, () -> restaurantsUnallocated,
                o -> restaurantsUnallocated = o, this::getRandomRestaurant);
    }

    private <T extends CommunalPlace> void createNGeneric(
            Function<CommunalPlace.Size,T> constructor,
            int n,
            ProbabilityDistribution<CommunalPlace.Size> sizeDist,
            ProbabilityDistribution<T> finalDist) {

        List<T> places = new ArrayList<>();
        int s = 0, m = 0, l = 0;
        for (int i = 0; i < n; i++) {
            CommunalPlace.Size size = sizeDist.sample();
            switch (size) {
                case SMALL:
                    s++;
                    break;
                case MED:
                    m++;
                    break;
                case LARGE:
                    l++;
                    break;
            }
            places.add(constructor.apply(size));
        }
        
        double lprob =  PopulationParameters.get().getpAllocateLarge();
        double mprob =  PopulationParameters.get().getpAllocateMed();
        double sprob =  PopulationParameters.get().getpAllocateSmall();

        // In the case of 0 buildings we need to expand the probabilities to fill the distribution
        if (l == 0 && m == 0) {
            sprob = 1;
        }
        else if (l == 0 && s == 0) {
            mprob = 1;
        }
        else if (m == 0 && s == 0) {
            lprob = 1;
        }
        else if (l == 0) {
            sprob += lprob/2;
            mprob += lprob/2;
        }
        else if (m == 0) {
            sprob += mprob/2;
            lprob += mprob/2;
        }
        else if (s == 0) {
            mprob += sprob/2;
            lprob += sprob/2;
        }

        double pl = 0;
        double pm = 0;
        double ps = 0;

        if (l > 0) {
            pl = lprob / l;
        }
        if (m > 0) {
            pm = mprob / m;
        }
        if (s > 0) {
            ps = sprob / s;
        }

        for (T p : places) {
            switch (p.getSize()) {
                case SMALL:
                    finalDist.add(ps, p);
                    break;
                case MED:
                    finalDist.add(pm, p);
                    break;
                case LARGE:
                    finalDist.add(pl, p);
                    break;
            }
            all.add(p);
        }
    }

    public void createNOffices(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution();
        p.add(PopulationParameters.get().getpOfficeSmall(), CommunalPlace.Size.SMALL);
        p.add(PopulationParameters.get().getpOfficeMed(), CommunalPlace.Size.MED);
        p.add(PopulationParameters.get().getpOfficeLarge(), CommunalPlace.Size.LARGE);
        createNGeneric(s -> new Office(s), n, p, offices);
    }

    public void createNHospitals(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution();
        p.add(PopulationParameters.get().getpHospitalSmall(), CommunalPlace.Size.SMALL);
        p.add(PopulationParameters.get().getpHospitalMed(), CommunalPlace.Size.MED);
        p.add(PopulationParameters.get().getpHospitalLarge(), CommunalPlace.Size.LARGE);
        createNGeneric(s -> new Hospital(s), n, p, hospitals);
    }

    public void createNSchools(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution();
        p.add(PopulationParameters.get().getpSchoolSmall(), CommunalPlace.Size.SMALL);
        p.add(PopulationParameters.get().getpSchoolMed(), CommunalPlace.Size.MED);
        p.add(PopulationParameters.get().getpSchoolLarge(), CommunalPlace.Size.LARGE);
        createNGeneric(s -> new School(s), n, p, schools);
    }
    public void createNNurseries(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution();
        p.add(PopulationParameters.get().getpNurserySmall(), CommunalPlace.Size.SMALL);
        p.add(PopulationParameters.get().getpNurseryMed(), CommunalPlace.Size.MED);
        p.add(PopulationParameters.get().getpNurseryLarge(), CommunalPlace.Size.LARGE);
        createNGeneric(s -> new Nursery(s), n, p, nurseries);
    }

    public void createNRestaurants(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution();
        p.add(PopulationParameters.get().getpRestaurantSmall(), CommunalPlace.Size.SMALL);
        p.add(PopulationParameters.get().getpRestaurantMed(), CommunalPlace.Size.MED);
        p.add(PopulationParameters.get().getpRestaurantLarge(), CommunalPlace.Size.LARGE);
        createNGeneric(s -> new Restaurant(s), n, p, restaurants);
    }

    public void createNShops(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution();
        p.add(PopulationParameters.get().getpShopSmall(), CommunalPlace.Size.SMALL);
        p.add(PopulationParameters.get().getpShopMed(), CommunalPlace.Size.MED);
        p.add(PopulationParameters.get().getpShopLarge(), CommunalPlace.Size.LARGE);
        createNGeneric(s -> new Shop(s), n, p, shops);
    }

    public void createNConstructionSites(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution();
        p.add(PopulationParameters.get().getpConstructionSiteSmall(), CommunalPlace.Size.SMALL);
        p.add(PopulationParameters.get().getpConstructionSiteMed(), CommunalPlace.Size.MED);
        p.add(PopulationParameters.get().getpConstructionSiteLarge(), CommunalPlace.Size.LARGE);
        createNGeneric(s -> new ConstructionSite(s), n, p, constructionSites);
    }

    public List<CommunalPlace> getAllPlaces() {
        return this.all;
    }

    public List<Office> getOffices() {
        return offices.toList();
    }

    public List<ConstructionSite> getConstructionSites() {
        return constructionSites.toList();
    }

    public List<Hospital> getHospitals() {
        return hospitals.toList();
    }

    public List<Nursery> getNurseries() {
        return nurseries.toList();
    }

    public List<Restaurant> getRestaurants() {
        return restaurants.toList();
    }

    public List<School> getSchools() {
        return schools.toList();
    }

    public List<Shop> getShops() {
        return shops.toList();
    }
}
