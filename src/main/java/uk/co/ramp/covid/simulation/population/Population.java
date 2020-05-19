/*
 * Paul Bessell
 * This is the principal driver class that initialises and manages a population of People
 */


package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.*;

public class Population {

    private static final Logger LOGGER = LogManager.getLogger(Population.class);

    private final int populationSize;
    private final int nHousehold;
    private final Household[] population;
    private final Person[] aPopulation;
    private CommunalPlace[] cPlaces;
    private int[] shopIndexes;
    private int[] restaurantIndexes;
    private boolean lockdown;
    private boolean rLockdown;
    private int lockdownStart;
    private int lockdownEnd;
    private double socialDist;
    private boolean schoolL;
    private final RandomDataGenerator rng;

    public Population(int populationSize, int nHousehold) {
        this.rng = RNG.get();
        this.populationSize = populationSize;
        this.nHousehold = nHousehold;
        if (this.nHousehold > this.populationSize) LOGGER.warn("More households than population");

        this.population = new Household[this.nHousehold];
        this.aPopulation = new Person[this.populationSize];
        this.lockdownStart = (-1);
        this.lockdownEnd = (-1);
        this.socialDist = 1.0;
        this.schoolL = false;
    }

    // Creates the population of People based on the probabilities of age groups above
    private void createPopulation(BitSet adultIndex, BitSet pensionerIndex,
                                  BitSet childIndex, BitSet infantIndex) {

        ProbabilityDistribution<Integer> dist = new ProbabilityDistribution<>();
        dist.add(PopulationParameters.get().getpAdults(), 0);
        dist.add(PopulationParameters.get().getpPensioners(), 1);
        dist.add(PopulationParameters.get().getpChildren(), 2);
        dist.add(PopulationParameters.get().getpInfants(), 3);

        for (int i = 0; i < this.populationSize; i++) {
            int type = dist.sample();
            switch(type) {
                case 0: {
                    this.aPopulation[i] = new Adult();
                    adultIndex.set(i);
                } break;
                case 1: {
                    this.aPopulation[i] = new Pensioner();
                    pensionerIndex.set(i);
                } break;
                case 2:{
                    this.aPopulation[i] = new Child();
                    childIndex.set(i);
                } break;
                case 3:{
                    this.aPopulation[i] = new Infant();
                    infantIndex.set(i);
                } break;
            }
        }
    }

    // Creates households based on probability of different household types
    private void createHouseholds() {
        ProbabilityDistribution<Household.HouseholdType> p = new ProbabilityDistribution<>();
        p.add(PopulationParameters.get().getpAdultOnly(), Household.HouseholdType.ADULT);
        p.add(PopulationParameters.get().getpPensionerOnly(), Household.HouseholdType.PENSIONER);
        p.add(PopulationParameters.get().getpPensionerAdult(), Household.HouseholdType.ADULTPENSIONER);
        p.add(PopulationParameters.get().getpAdultChildren(), Household.HouseholdType.ADULTCHILD);
        p.add(PopulationParameters.get().getpPensionerChildren(), Household.HouseholdType.PENSIONERCHILD);
        p.add(PopulationParameters.get().getpAdultPensionerChildren(), Household.HouseholdType.ADULTPENSIONERCHILD);


        for (int i = 0; i < nHousehold; i++) {
            Household.HouseholdType t = p.sample();
           population[i] = new Household(t);
        }
    }

    // Checks we have enough people of the right types to perform a household allocation
    private boolean householdAllocationPossible(BitSet adultIndex, BitSet pensionerIndex,
                                                BitSet childIndex, BitSet infantIndex) {
        int adult = 0; int pensioner = 0; int adultPensioner = 0; int adultChild = 0;
        int pensionerChild = 0; int adultPensionerChild = 0;
        for (Household h : population) {
            switch(h.gethType()) {
                case ADULT: adult++; break;
                case PENSIONER: pensioner++; break;
                case ADULTPENSIONER: adultPensioner++; break;
                case ADULTCHILD: adultChild++; break;
                case PENSIONERCHILD: pensionerChild++; break;
                case ADULTPENSIONERCHILD: adultPensionerChild++; break;
            }
        }
        boolean possible = adultIndex.cardinality() > adult + adultPensioner + adultChild + adultPensionerChild
                && pensionerIndex.cardinality() > adultPensioner + pensioner + pensionerChild + adultPensionerChild
                && childIndex.cardinality() + infantIndex.cardinality() > adultChild + pensionerChild + adultPensionerChild;
        return  possible;
    }

    // Cycles over all bits of remainingPeople and ensures they are allocated to a household in a greedy fashion
    private void greedyAllocate(BitSet remainingPeople, Set<Household.HouseholdType> types) {
        int i = 0; // Pointer into remaining
        for (int h = 0; h < population.length; h++) {
            Household.HouseholdType htype = population[h].gethType();
            if (types.contains(htype)) {
                i = remainingPeople.nextSetBit(i);
                if (i < 0) {
                    break;
                }
                remainingPeople.clear(i);
                aPopulation[i].setHome(population[h]);
                population[h].addPerson(aPopulation[i]);
            }
        }
    }

    private void probAllocate(BitSet remainingPeople,
                              Set<Household.HouseholdType> types,
                              Map<Integer, Double> probabilities) {
        int i = 0; // Pointer into remaining
        while (!remainingPeople.isEmpty()) {
            for (int h = 0; h < population.length; h++) {
                Household.HouseholdType htype = population[h].gethType();
                if (types.contains(htype)) {
                    double rand = rng.nextUniform(0, 1);
                    double prob_to_add = probabilities.getOrDefault(population[h].getHouseholdSize(), 1.0);
                    if (rand < prob_to_add) {
                        i = remainingPeople.nextSetBit(i);
                        if (i < 0) {
                            break;
                        }
                        aPopulation[i].setHome(population[h]);
                        population[h].addPerson(aPopulation[i]);
                        remainingPeople.clear(i);
                    }
                }
            }
        }
    }

    // We populate houseHolds greedily.
    public void populateHouseholds() throws ImpossibleAllocationException {
        createHouseholds();

        BitSet infantIndex = new BitSet(populationSize);
        BitSet childIndex = new BitSet(populationSize);
        BitSet adultIndex = new BitSet(populationSize);
        BitSet pensionerIndex = new BitSet(populationSize);

        createPopulation(adultIndex, pensionerIndex, childIndex, infantIndex);

        if (!householdAllocationPossible(adultIndex, pensionerIndex, childIndex, infantIndex)) {
            throw new ImpossibleAllocationException("Population distribution cannot populate household distribution");
        }

        // Ensures miminal constraints are met
        greedyAllocate(adultIndex, Household.adultHouseholds);
        greedyAllocate(pensionerIndex, Household.pensionerHouseholds);

        // For OR constraints, e.g. child or infant, we union the bitsets during the greedy algorithm
        // For the probabilistic allocations below, they can have different probabilities.
        BitSet childOrInfant = new BitSet(populationSize);
        childOrInfant.or(childIndex);
        childOrInfant.or(infantIndex);

        greedyAllocate(childOrInfant, Household.childHouseholds);

        // set intersections to allow children/infants to be treated independently again
        childIndex.and(childOrInfant);
        infantIndex.and(childOrInfant);

        probAllocate(adultIndex,
                Household.adultHouseholds,
                PopulationParameters.get().getAdultAllocationPMap());
        probAllocate(pensionerIndex,
                Household.pensionerHouseholds,
                PopulationParameters.get().getPensionerAllocationPMap());
        probAllocate(childIndex,
                Household.childHouseholds,
                PopulationParameters.get().getChildAllocationPMap());
        probAllocate(infantIndex,
                Household.childHouseholds,
                PopulationParameters.get().getInfantAllocationPMap());
    }

    // Used for diagnosing problems with the algorithm for creating households
    public void summarisePop() {
        int total = 0;
        for (int i = 0; i < this.nHousehold; i++) {
            total += this.population[i].getHouseholdSize();
        }
    }


    // This creates the Communal places of different types where people mix
    public void createMixing() {
        int nHospitals = populationSize / PopulationParameters.get().getHospitalRatio();
        int nSchools = populationSize / PopulationParameters.get().getSchoolsRatio();
        int nShops = populationSize / PopulationParameters.get().getShopsRatio();
        int nOffices = populationSize / PopulationParameters.get().getOfficesRatio();
        int nConstructionSites = populationSize / PopulationParameters.get().getConstructionSiteRatio();
        int nNurseries = populationSize / PopulationParameters.get().getNurseriesRatio();
        int nRestaurants = populationSize / PopulationParameters.get().getRestaurantRatio();
        int nEstablishments = nHospitals + nSchools + nShops + nOffices + nConstructionSites + nNurseries + nRestaurants;
        this.shopIndexes = new int[nShops];
        this.restaurantIndexes = new int[nRestaurants];

        LOGGER.info("Total number of establishments = {}", nEstablishments);

        CommunalPlace[] places = new CommunalPlace[nEstablishments];
        for (int i = 0; i < nEstablishments; i++) {
            if (i < nHospitals) places[i] = new Hospital(i);
            else if (i < nHospitals + nSchools) places[i] = new School(i);
            else if (i < nHospitals + nSchools + nShops) { // Allocate shops and their indexes
                places[i] = new Shop(i);
                this.shopIndexes[i - nHospitals - nSchools] = i;
            } else if (i < nHospitals + nSchools + nShops + nOffices) places[i] = new Office(i);
            else if (i < nHospitals + nSchools + nShops + nOffices + nConstructionSites)
                places[i] = new ConstructionSite(i);
            else if (i < nHospitals + nSchools + nShops + nOffices + nConstructionSites + nNurseries)
                places[i] = new Nursery(i);
            else if (i < nHospitals + nSchools + nShops + nOffices + nConstructionSites + nNurseries + nRestaurants) {
                places[i] = new Restaurant(i);
                this.restaurantIndexes[i - nHospitals - nSchools - nShops - nOffices - nConstructionSites - nNurseries] = i;
            }

        }
        this.cPlaces = places;
    }

    // Allocates people to communal places - work environments
    public void allocatePeople() {
        for (int i = 0; i < this.nHousehold; i++) {
            for (int j = 0; j < this.population[i].getHouseholdSize(); j++) {
                Person cPerson = this.population[i].getPerson(j);
                cPerson.allocateCommunalPlace(this);
            }
        }
        this.assignNeighbours();
    }

    // For selecting a CommunalPlace at random to assign a People to
    public CommunalPlace getRandomPlace() {
        int rnd = rng.nextInt(0, this.cPlaces.length - 1);
        return this.cPlaces[rnd];
    }

    // This method assigns a random number of neighbours to each Household
    public void assignNeighbours() {
        for (int i = 0; i < this.nHousehold; i++) {
            Household cHouse = this.population[i];
            int expectedNeighbours = PopulationParameters.get().getExpectedNeighbours();
            int nneighbours = (int) rng.nextPoisson(expectedNeighbours);
            int[] neighbourArray = new int[nneighbours];
            for (int k = 0; k < nneighbours; k++) {
                int nInt = rng.nextInt(0, this.nHousehold - 1);
                if (nInt == i) k--;
                else neighbourArray[k] = nInt;
            }
            cHouse.setNeighbourList(neighbourArray);
        }
    }


    // Force infections into a defined number of people
    public void seedVirus(int nInfections) {
        for (int i = 1; i <= nInfections; i++) {
            int nInt = rng.nextInt(0, this.nHousehold - 1);
            if (this.population[nInt].getHouseholdSize() > 0) {
                if (!population[nInt].seedInfection()) i--;
            }
            if (this.population[nInt].getHouseholdSize() == 0) i--;
        }
    }

    // Step through nDays in 1 hour time steps
    public List<DailyStats> timeStep(int nDays) {
        List<DailyStats> stats = new ArrayList<>(nDays);
        for (int i = 0; i < nDays; i++) {
            DailyStats dStats = new DailyStats(i);
            int dWeek = (i + 1) % 7;
            this.implementLockdown(i);
            LOGGER.info("Lockdown = {}", this.lockdown);
            for (int k = 0; k < 24; k++) {
                this.cycleHouseholds(dWeek, k, dStats);
                this.cyclePlaces(k, dStats);
                this.returnShoppers(k);
                this.returnRestaurant(k);
                this.shoppingTrip(dWeek, k);
                if (!this.rLockdown) this.restaurantTrip(dWeek, k);
            }
            stats.add(this.processCases(dStats));
        }
        return stats;
    }

    // Basically a method to set the instance variables. Could also do this through an overloaded constructor, but I rather prefer this way of doing things
    public void setLockdown(int start, int end, double socialDist) {
        if (start >= 0) {
            this.lockdownStart = start;
        }
        if (end >= 0) this.lockdownEnd = end;
        this.socialDist = socialDist;
    }

    // This is a really cack handed way of implementing the school lockdown. IT needs improving
// TODO Sort this out 
    public void setSchoolLockdown(int start, int end, double socialDist) {
        if (start >= 0) {
            this.lockdownStart = start;
        }
        if (end >= 0) this.lockdownEnd = end;
        this.socialDist = socialDist;
        this.schoolL = true;
    }

    // Tests on each daily time step whether to do anything wiht the  lockdown
    private void implementLockdown(int day) {
        if (day == this.lockdownStart) {
            this.lockdown = true;
            this.rLockdown = true;
            this.socialDistancing();
        }
        if (day == this.lockdownEnd) {
            if (!this.schoolL) this.lockdown = false;
            if (this.schoolL) this.schoolExemption();
        }
    }

    // Sets the social distancing to parameters wihtin the CommunalPlaces
    private void socialDistancing() {
        for (CommunalPlace cPlace : this.cPlaces) {
            cPlace.adjustSDist(this.socialDist);
        }
    }

    // This method generates output at the end of each day
    private DailyStats processCases(DailyStats stats) {
        for (Household cHouse : population) {
            for (Person p : cHouse.getInhabitants()) {
                stats.processPerson(p);
            }
            for (Person p: cHouse.getVisitors()) {
               stats.processPerson(p);
            }
            stats.incrementDeaths(cHouse.getDeaths());
        }
        stats.log();
        return stats;
    }

    // Step through the households to identify individual movements to CommunalPlaces
    private void cycleHouseholds(int day, int hour, DailyStats stats) {
        for (Household household : this.population) {
            ArrayList<Person> vHouse = household.cycleHouse(stats);
            this.cycleMovements(vHouse, day, hour);
            this.returnNeighbours(household);
            if (!this.lockdown) this.cycleNeighbours(household);
        }
    }

    // For each household processes any movements to Communal Places that are relevant
    private void cycleMovements(ArrayList<Person> vHouse, int day, int hour) {
        int i = 0;
        while (i < vHouse.size()) {
            Person nPers = vHouse.get(i);
            if (nPers.hasPrimaryCommunalPlace() && !nPers.getQuarantine()) {
                boolean visit = nPers.getPrimaryCommunalPlace().checkVisit(nPers, hour, day, this.lockdown);
                if (visit) {
                    vHouse.remove(i);
                    i--;
                }
            }
            i++;
        }
    }

    // This sets the schools exempt from lockdown if that is triggered. Somewhat fudged at present by setting the schools to be KeyPremises - not entirely what thta was intended for, but it works
    private void schoolExemption() {
        for (CommunalPlace cPlace : this.cPlaces) {
            if (cPlace instanceof School || cPlace instanceof Nursery) {
                cPlace.overrideKeyPremises(true);
            }
        }
    }

    // People returning ome at the end of the day
    private void cyclePlaces(int hour, DailyStats stats) {
        for (CommunalPlace cPlace : this.cPlaces) {
            ArrayList<Person> retPeople = cPlace.cyclePlace(hour, stats);
            for (Person cPers : retPeople) {
                cPers.returnHome();
            }
        }
    }

    // Go through neighbours and see if they visit anybody
    private void cycleNeighbours(Household cHouse) {
        int visitIndex = -1; // Set a default for this here.

        if (cHouse.nNeighbours() > 0 && cHouse.getHouseholdSize() > 0) {
            int k = 0;
            while (k < cHouse.nNeighbours()) {
                if (rng.nextUniform(0, 1) < PopulationParameters.get().getNeighbourVisitFreq()) {
                    visitIndex = k; // This sets the probability of a neighbour visit as once per week
                }
                k++;
            }
        }
        if (visitIndex > (-1)) {
            this.population[cHouse.getNeighbourIndex(visitIndex)].welcomeNeighbours(cHouse);
        }
    }

    // Neighbours returning home
    private void returnNeighbours(Household cHouse) {
        ArrayList<Person> vReturn = cHouse.sendNeighboursHome();
        for (Person nPers : vReturn) {
            nPers.returnHome();
        }
    }

    // People go shopping
    private void shoppingTrip(int day, int hour) {
        int openingTime = 9;
        int closingTime = 17;
        double visitFrequency = 3.0 / 7.0; // BAsed on three visits per week to shops
        double visitProb = visitFrequency / 8.0;
        if (this.lockdown) visitProb = visitProb * 0.5;
        ArrayList<Person> vNext = null;

        if (hour >= openingTime && hour < closingTime) {
            for (Household household : this.population) {
                if (rng.nextUniform(0, 1) < visitProb) {
                    vNext = household.shoppingTrip();
                }
                if (vNext != null) {
                    int shopSample = rng.nextInt(0,this.shopIndexes.length - 1);
                    ((Shop) this.cPlaces[this.shopIndexes[shopSample]]).shoppingTrip(vNext);
                }
                vNext = null;
            }
        }
    }

    // People return from shopping
    private void returnShoppers(int hour) {
        for (int shopIndex : this.shopIndexes) {
            ArrayList<Person> vCurr = ((Shop) this.cPlaces[shopIndex]).sendHome(hour);
            if (vCurr != null) {
                for (Person nPers : vCurr) {
                    nPers.returnHome();
                }
            }
        }
    }

    // People go out for dinner
    private void restaurantTrip(int day, int hour) {
        int openingTime = 10;
        int closingTime = 22;
        int startDay = 3;
        int endDay = 7;
        double visitFrequency = 2.0 / 7.0; // Based on three visits per week to shops
        double visitProb = visitFrequency / 12.0;
        ArrayList<Person> vNext = null;

        if (hour >= openingTime && hour < closingTime && startDay >= day && endDay <= day) {
            for (Household household : this.population) {
                if (rng.nextUniform(0, 1) < visitProb) {
                    vNext = household.shoppingTrip(); // This method is fine for our purposes here
                }
                if (vNext != null) {
                    int shopSample = rng.nextInt(0, this.restaurantIndexes.length - 1);
                    ((Restaurant) this.cPlaces[this.restaurantIndexes[shopSample]]).shoppingTrip(vNext);
                }
                vNext = null;
            }
        }
    }

    // People return from dinner
    private void returnRestaurant(int hour) {
        // TODO CHECK-ME: Should this be restaurantIndexes?
        for (int shopIndex : this.shopIndexes) {
            ArrayList<Person> vCurr = ((Shop) this.cPlaces[shopIndex]).sendHome(hour);
            if (vCurr != null) {
                for (Person nPers : vCurr) {
                   nPers.returnHome();
                }
            }
        }
    }

    public Household[] getPopulation() {
        return population;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getnHousehold() {
        return nHousehold;
    }

    public Person[] getaPopulation() {
        return aPopulation;
    }

    public CommunalPlace[] getcPlaces() {
        return cPlaces;
    }

    public int[] getShopIndexes() {
        return shopIndexes;
    }

    public int[] getRestaurantIndexes() {
        return restaurantIndexes;
    }

    public boolean isLockdown() {
        return lockdown;
    }

    public boolean isrLockdown() {
        return rLockdown;
    }

    public int getLockdownStart() {
        return lockdownStart;
    }

    public int getLockdownEnd() {
        return lockdownEnd;
    }

    public double getSocialDist() {
        return socialDist;
    }

    public boolean isSchoolL() {
        return schoolL;
    }
}
