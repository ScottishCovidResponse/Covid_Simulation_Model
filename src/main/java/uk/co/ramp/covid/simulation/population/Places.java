package uk.co.ramp.covid.simulation.population;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Helper class to manage communal places of particular types */
public class Places {
    private static final Logger LOGGER = LogManager.getLogger(Places.class);

    private final ProbabilityDistribution<Office> offices;
    private final ProbabilityDistribution<ConstructionSite> constructionSites;
    private final ProbabilityDistribution<Hospital> allHospitals;
    private final ProbabilityDistribution<Hospital> nonCovidHospitals;
    private final ProbabilityDistribution<CovidHospital> covidHospitals;
    private final ProbabilityDistribution<Nursery> nurseries;
    private final ProbabilityDistribution<Restaurant> restaurants;
    private final ProbabilityDistribution<School> schools;
    private final ProbabilityDistribution<Shop> shops;
    private final ProbabilityDistribution<CareHome> careHomes;
    private final ProbabilityDistribution<CareHome> careHomeResidentDist = new ProbabilityDistribution<>();
    
    private boolean officesUnallocated = false;
    private boolean constructionSitesUnallocated = false;
    private boolean hospitalsUnallocated = false;
    private boolean nurseriesUnallocated = false;
    private boolean restaurantsUnallocated = false;
    private boolean schoolsUnallocated = false;
    private boolean shopsUnallocated = false;
    private boolean careHomeUnallocated = false;

    private final List<Place> allPlaces;
    private final List<CommunalPlace> communalPlaces;
    private final List<Household> households;

    private final List<CareHome.CareHomeResidentRange> careHomeResidentRanges =
            PopulationParameters.get().buildingDistribution.careHomeResidentRanges;

    public Places(int populationSize, int numHouseholds) {
        communalPlaces = new ArrayList<>();
        allPlaces = new ArrayList<>();
        households = new ArrayList<>(numHouseholds);

        createHouseholds(numHouseholds);

        offices = new ProbabilityDistribution<>();
        constructionSites = new ProbabilityDistribution<>();
        allHospitals = new ProbabilityDistribution<>();
        nonCovidHospitals = new ProbabilityDistribution<>();
        covidHospitals = new ProbabilityDistribution<>();
        nurseries = new ProbabilityDistribution<>();
        restaurants = new ProbabilityDistribution<>();
        schools = new ProbabilityDistribution<>();
        shops = new ProbabilityDistribution<>();
        careHomes = new ProbabilityDistribution<>();

        createCommunalPlaces(populationSize);
    }

    // This creates the Communal places of different types where people mix
    private void createCommunalPlaces(int populationSize) {
        int nHospitals = populationSize / PopulationParameters.get().buildingDistribution.populationToHospitalsRatio;
        int nSchools = populationSize / PopulationParameters.get().buildingDistribution.populationToSchoolsRatio;
        int nShops = populationSize / PopulationParameters.get().buildingDistribution.populationToShopsRatio;
        int nOffices = populationSize / PopulationParameters.get().buildingDistribution.populationToOfficesRatio;
        int nConstructionSites = populationSize / PopulationParameters.get().buildingDistribution.populationToConstructionSitesRatio;
        int nNurseries = populationSize / PopulationParameters.get().buildingDistribution.populationToNurseriesRatio;
        int nRestaurants = populationSize / PopulationParameters.get().buildingDistribution.populationToRestaurantsRatio;
        int nCareHomes = populationSize / PopulationParameters.get().buildingDistribution.populationToCareHomesRatio;

        createNHospitals(nHospitals);
        createNSchools(nSchools);
        createNShops(nShops);
        createNOffices(nOffices);
        createNConstructionSites(nConstructionSites);
        createNNurseries(nNurseries);
        createNRestaurants(nRestaurants);
        createNCareHomes(nCareHomes);

        LOGGER.info("Total number of communal places = {}", getCommunalPlaces().size());
    }

    private void createHouseholds(int numHouseholds) {
        ProbabilityDistribution<Supplier<Household>> p =
                PopulationParameters.get().householdDistribution.householdTypeDistribution();

        for (int i = 0; i < numHouseholds; i++) {
            Household h = p.sample().get();
            households.add(h);
            allPlaces.add(h);
        }
    }

    public Office getRandomOffice() {
        return offices.sample();
    }

    public ConstructionSite getRandomConstructionSite() {
        return constructionSites.sample();
    }

    public Hospital getRandomHospital() {
        return allHospitals.sample();
    }

    public CovidHospital getRandomCovidHospital() {
        return covidHospitals.sample();
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

    public CareHome getRandomCareHome() {
        return careHomes.sample();
    }

    public Hospital getRandomNonCovidHospital() {
        return nonCovidHospitals.sample();
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
        return getNextWorkplace(allHospitals, () -> hospitalsUnallocated,
                o -> hospitalsUnallocated = o, this::getRandomHospital);
    }

    public Restaurant getNextRestaurantJob() {
        return getNextWorkplace(restaurants, () -> restaurantsUnallocated,
                o -> restaurantsUnallocated = o, this::getRandomRestaurant);
    }

    public CareHome getNextCareHomeJob() {
        return getNextWorkplace(careHomes, () -> careHomeUnallocated,
                o -> careHomeUnallocated = o, this::getRandomCareHome);
    }

    private <T extends CommunalPlace> void createNGeneric(
            BiFunction<CommunalPlace.Size, Integer, T> constructorN,
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
            places.add(constructorN.apply(size, i));
        }
        communalPlaces.addAll(places);
        allPlaces.addAll(places);

        // In the case of 0 buildings we need to expand the probabilities to fill the distribution
        createPlaceDistribution(finalDist, places, s, m, l);
    }

    private <T extends CommunalPlace> void createPlaceDistribution(ProbabilityDistribution<T> finalDist,
                                                                   List<T> places) {
        int s = 0, m = 0, l = 0;
        for (T h : places) {
            switch (h.getSize()) {
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
        }
        createPlaceDistribution(finalDist, places, s, m, l);
    }

    private <T extends CommunalPlace> void createPlaceDistribution(ProbabilityDistribution<T> finalDist,
                                                                   List<T> places, int s, int m, int l) {
        double lprob = PopulationParameters.get().workerDistribution.sizeAllocation.pLarge.asDouble();
        double mprob = PopulationParameters.get().workerDistribution.sizeAllocation.pMed.asDouble();
        double sprob = PopulationParameters.get().workerDistribution.sizeAllocation.pSmall.asDouble();

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
        }
    }

    private void createNOffices(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.officeSizeDistribution.sizeDistribution();
        createNGeneric((s, x) -> new Office(s), n, p, offices);
    }

    // Hospitals creation has some special features to distinguish those that are designated Covid wards
    private void createNHospitals(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.hospitalSizeDistribution.sizeDistribution();

        BiFunction<CommunalPlace.Size, Integer, Hospital> cons = (s, i) -> {
            if (i % PopulationParameters.get().buildingDistribution.covidHospitalRatio == 0) {
                return new CovidHospital(s);
            } else {
                return new Hospital(s);
            }
        };

        createNGeneric(cons, n, p, allHospitals);

        // Hospitals are also cached based on their type to allow easy access for appts/covid cases
        // Limited use of instanceof to ensure a safe cast
        List<CovidHospital> chospitals = new ArrayList<>();
        List<Hospital> nonCHospitals = new ArrayList<>();
        for (Hospital h : getAllHospitals()) {
            if (h instanceof CovidHospital) {
                chospitals.add((CovidHospital) h);
            } else {
                nonCHospitals.add(h);
            }
        }

        createPlaceDistribution(covidHospitals, chospitals);
        createPlaceDistribution(nonCovidHospitals, nonCHospitals);
    }

    private void createNSchools(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.schoolSizeDistribution.sizeDistribution();
        createNGeneric((s, x) -> new School(s), n, p, schools);
    }

    private void createNNurseries(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.nurserySizeDistribution.sizeDistribution();
        createNGeneric((s, x) -> new Nursery(s), n, p, nurseries);
    }

    private void createNRestaurants(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.restaurantSizeDistribution.sizeDistribution();
        createNGeneric((s, x) -> new Restaurant(s), n, p, restaurants);
    }

    private void createNShops(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.shopSizeDistribution.sizeDistribution();
        createNGeneric((s, x) -> new Shop(s), n, p, shops);
    }

    private void createNConstructionSites(int n) {
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.constructionSiteSizeDistribution.sizeDistribution();
        createNGeneric((s, x) -> new ConstructionSite(s), n, p, constructionSites);
    }

    private void createNCareHomes(int n) {
        // Note this size distribution is for *staff* not residents, who are handled in the getCareHomeForResident function
        ProbabilityDistribution<CommunalPlace.Size> p =
                PopulationParameters.get().buildingDistribution.careHomeSizeDistribution.sizeDistribution();

        createNGeneric(
                (s, i) -> {
                    CareHome.CareHomeResidentRange r = careHomeResidentRanges.get(i % careHomeResidentRanges.size());
                    return new CareHome(s, r);
                },
                n, p, careHomes);

        createCareHomeResidentDistribution();
    }

    private void createCareHomeResidentDistribution() {
        // We create a distribution based on the size of the range, e.g. 60-110 resident home are more
        // likely than 21-40 resident homes.
        // Probabilities are calculated by normalising the ranges: range / totalRange,
        // Then bucketing by dividing by the numWithRange

        int[] numberOfType = new int[careHomeResidentRanges.size()];
        for (CareHome h : getCareHomes()) {
            for (int i = 0; i < careHomeResidentRanges.size(); i++) {
                if (h.getResidentRange().equals(careHomeResidentRanges.get(i))) {
                    numberOfType[i]++;
                }
            }
        }

        int rangeSum = careHomeResidentRanges.stream().map(r -> r.getRange()).reduce(0, Integer::sum);

        for (CareHome h : getCareHomes()) {
            for (int i = 0; i < careHomeResidentRanges.size(); i++) {
                if (h.getResidentRange().equals(careHomeResidentRanges.get(i))) {
                    double p = (double) careHomeResidentRanges.get(i).getRange() / rangeSum  / numberOfType[i];
                    careHomeResidentDist.add(p, h);
                }
            }
        }
    }

    public List<CommunalPlace> getCommunalPlaces() {
        return this.communalPlaces;
    }

    public List<Place> getAllPlaces() {
        return this.allPlaces;
    }

    public List<Office> getOffices() {
        return offices.toList();
    }

    public List<ConstructionSite> getConstructionSites() {
        return constructionSites.toList();
    }

    public List<Hospital> getAllHospitals() {
        return allHospitals.toList();
    }

    public List<CovidHospital> getCovidHospitals() {
        return covidHospitals.toList();
    }

    public List<Hospital> getNonCovidHospitals() {
        return nonCovidHospitals.toList();
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

    public List<CareHome> getCareHomes() {
        return careHomes.toList();
    }

    public List<Household> getHouseholds() {
        return households;
    }

    public CareHome getCareHomeForResident() {
        // Allocate all min person requirements first
        for (CareHome h : getCareHomes()) {
            if (h.residentsNeeded()) {
                return h;
            }
        }

        // Randomly assign once min person reqs are met
        return careHomeResidentDist.sample();
    }
}
