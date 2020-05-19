package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Helper class to manage communal places of particular types */
public class Places {

    private List<Office> offices;
    private List<ConstructionSite> constructionSites;
    private List<Hospital> hospitals;
    private List<Nursery> nurseries;
    private List<Restaurant> restaurants;
    private List<School> schools;
    private List<Shop> shops;

    public List<Office> getOffices() {
        return offices;
    }

    public List<ConstructionSite> getConstructionSites() {
        return constructionSites;
    }

    public List<Hospital> getHospitals() {
        return hospitals;
    }

    public List<Nursery> getNurseries() {
        return nurseries;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public List<School> getSchools() {
        return schools;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public List<CommunalPlace> getAll() {
        return all;
    }

    private List<CommunalPlace> all;

    public Places() {
        offices = new ArrayList<>();
        constructionSites = new ArrayList<>();
        hospitals = new ArrayList<>();
        nurseries = new ArrayList<>();
        restaurants = new ArrayList<>();
        schools = new ArrayList<>();
        shops = new ArrayList<>();
        all = new ArrayList<>();
    }

    public void addOffice(Office o) {
        offices.add(o);
        all.add(o);
    }

    public void addConstructionSite(ConstructionSite s) {
        constructionSites.add(s);
        all.add(s);
    }

    public void addHospital(Hospital h) {
        hospitals.add(h);
        all.add(h);
    }

    public void addNursery(Nursery n) {
        nurseries.add(n);
        all.add(n);
    }

    public void addRestaurant(Restaurant r) {
        restaurants.add(r);
        all.add(r);
    }

    public void addSchool(School s) {
        schools.add(s);
        all.add(s);
    }

    public void addShop(Shop s) {
        shops.add(s);
        all.add(s);
    }

    // We often need a random place
    private <T> T getRandom(List<T> s) {
        int i = (int) RNG.get().nextInt(0, s.size() - 1);
        return s.get(i);
    }

    public Office getRandomOffice() {
        return getRandom(offices);
    }

    public ConstructionSite getRandomConstructionSite() {
        return getRandom(constructionSites);
    }

    public Hospital getRandomHospital() {
        return getRandom(hospitals);
    }

    public Nursery getRandomNursery() {
        return getRandom(nurseries);
    }

    public Restaurant getRandomRestaurant() {
        return getRandom(restaurants);
    }

    public School getRandomSchool() {
        return getRandom(schools);
    }

    public Shop getRandomShop() {
        return getRandom(shops);
    }
    
    public void createNOffices(int n) {
        for (int i = 0; i < n; i++) {
            addOffice(new Office());
        }
    }

    public void createNHospitals(int n) {
        for (int i = 0; i < n; i++) {
            addHospital(new Hospital());
        }
    }

    public void createNSchools(int n) {
        for (int i = 0; i < n; i++) {
            addSchool(new School());
        }
    }
    public void createNNurseries(int n) {
        for (int i = 0; i < n; i++) {
            addNursery(new Nursery());
        }
    }

    public void createNRestaurants(int n) {
        for (int i = 0; i < n; i++) {
            addRestaurant(new Restaurant());
        }
    }

    public void createNShops(int n) {
        for (int i = 0; i < n; i++) {
            addShop(new Shop());
        }
    }

    public void createNConstructionSites(int n) {
        for (int i = 0; i < n; i++) {
            addConstructionSite(new ConstructionSite());
        }
    }

    public List<CommunalPlace> getAllPlaces() {
        return this.all;
    }
}
