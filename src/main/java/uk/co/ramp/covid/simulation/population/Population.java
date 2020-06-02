/*
 * Paul Bessell
 * This is the principal driver class that initialises and manages a population of People
 */


package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.RStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;
import uk.co.ramp.covid.simulation.util.RNG;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Population {

    private static final Logger LOGGER = LogManager.getLogger(Population.class);

    private final int populationSize;
    private final int numHouseholds;

    private final ArrayList<Household> households;
    private final ArrayList<Person> allPeople;
    private Places places;
    private boolean lockdown;
    private boolean rLockdown;
    private int lockdownStart;
    private int lockdownEnd;
    private double socialDist;
    private boolean schoolL;
    private final RandomDataGenerator rng;

    public Population(int populationSize) throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        this.rng = RNG.get();
        this.populationSize = populationSize;

        this.numHouseholds = (int) (populationSize / PopulationParameters.get().getHouseholdRatio());

        if (numHouseholds == 0) {
            throw new ImpossibleAllocationException("No households requested");
        }
        if (numHouseholds > populationSize) {
            throw new ImpossibleAllocationException("More households than people requested");
        }

        this.households = new ArrayList<>(numHouseholds);
        this.allPeople = new ArrayList<>(populationSize);
        this.places = new Places();
        this.lockdownStart = (-1);
        this.lockdownEnd = (-1);
        this.socialDist = 1.0;
        this.schoolL = false;

        allocatePopulation();
    }
    
    private void allocatePopulation() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        populateHouseholds();
        createMixing();
        allocatePeople();
        assignNeighbours();
    }

    // Creates the population of People based on the probabilities of age groups above
    private void createPopulation(BitSet adultIndex, BitSet pensionerIndex,
                                  BitSet childIndex, BitSet infantIndex) {
        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(PopulationParameters.get().getPopulation());
        for (int i = 0; i < this.populationSize; i++) {
            PopulationDistribution.SexAge t = dist.sample();
            if (t.getAge() < 5) {
                allPeople.add(new Infant(t.getAge(), t.getSex()));
                infantIndex.set(i);
            } else if (t.getAge() < 18) {
                allPeople.add(new Child(t.getAge(), t.getSex()));
                childIndex.set(i);
            } else if (t.getAge() < 65) {
                allPeople.add(new Adult(t.getAge(), t.getSex()));
                adultIndex.set(i);
            } else {
                allPeople.add(new Pensioner(t.getAge(), t.getSex()));
                pensionerIndex.set(i);
            }
        }
    }

    // Creates households based on probability of different household types
    private void createHouseholds() {
        ProbabilityDistribution<Class> p = new ProbabilityDistribution<>();
        p.add(PopulationParameters.get().getpSingleAdult(), SingleAdult.class);
        p.add(PopulationParameters.get().getpSmallAdult(), SmallAdult.class);
        p.add(PopulationParameters.get().getpSingleParent(), SingleParent.class);
        p.add(PopulationParameters.get().getpSmallFamily(), SmallFamily.class);
        p.add(PopulationParameters.get().getpLargeFamily(), LargeFamily.class);
        p.add(PopulationParameters.get().getpLargeAdult(), LargeAdult.class);
        p.add(PopulationParameters.get().getpOlderSmaller(), OlderSmaller.class);
        p.add(PopulationParameters.get().getpSingleOlder(), SingleOlder.class);

        for (int i = 0; i < numHouseholds; i++) {
            Class htype = p.sample();
            HouseholdType t = null;
            try {
                t = (HouseholdType) htype.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                
            }

            households.add(new Household(t, places));
        }
    }

    // Checks we have enough people of the right types to perform a household allocation
    /*
    private boolean householdAllocationPossible(BitSet adultIndex, BitSet pensionerIndex,
                                                BitSet childIndex, BitSet infantIndex) {
        int adult = 0; int pensioner = 0; int adultPensioner = 0; int adultChild = 0;
        int pensionerChild = 0; int adultPensionerChild = 0;
        for (Household h : households) {
            switch(h.gethType()) {
                case ADULT: adult++; break;
                case PENSIONER: pensioner++; break;
                case ADULTPENSIONER: adultPensioner++; break;
                case ADULTCHILD: adultChild++; break;
                case PENSIONERCHILD: pensionerChild++; break;
                case ADULTPENSIONERCHILD: adultPensionerChild++; break;
            }
        }

        int adultHouseholds = adult + adultPensioner + adultChild + adultPensionerChild;
        int pensionerHouseholds = adultPensioner + pensioner + pensionerChild + adultPensionerChild;
        int childHouseholds = adultChild + pensionerChild + adultPensionerChild;
        
        boolean possible = adultIndex.cardinality() > adultHouseholds
                && pensionerIndex.cardinality() > pensionerHouseholds
                && childIndex.cardinality() + infantIndex.cardinality() > childHouseholds
                // We need to ensure everyone has somewhere to go
                && (adultIndex.cardinality() > 0 ? adultHouseholds > 0 : true)
                && (pensionerIndex.cardinality() > 0 ? pensionerHouseholds > 0 : true)
                && (childIndex.cardinality() > 0 ? childHouseholds > 0 : true);

        return  possible;
    } */

    // Cycles over all bits of remainingPeople and ensures they are allocated to a household in a greedy fashion

    /*
    private void greedyAllocate(BitSet remainingPeople) {
        int i = 0; // Pointer into remaining
        for (Household h : households) {
            HouseholdType htype = h.getHouseholdType();
            if (types.contains(htype)) {
                i = remainingPeople.nextSetBit(i);
                if (i < 0) {
                    break;
                }
                remainingPeople.clear(i);
                h.addInhabitant(allPeople.get(i));
            }
        }
    }

    private void probAllocate(BitSet remainingPeople,
                              Set<Household.HouseholdType> types,
                              Map<Integer, Double> probabilities) {
        int i = 0; // Pointer into remaining
        while (!remainingPeople.isEmpty()) {
            for (Household h : households) {
                Household.HouseholdType htype = h.gethType();
                if (types.contains(htype)) {
                    double rand = rng.nextUniform(0, 1);
                    double prob_to_add = probabilities.getOrDefault(h.getHouseholdSize(), 1.0);
                    if (rand < prob_to_add) {
                        i = remainingPeople.nextSetBit(i);
                        if (i < 0) {
                            break;
                        }
                        h.addInhabitant(allPeople.get(i));
                        remainingPeople.clear(i);
                    }
                }
            }
        }
    }

    // We populate houseHolds greedily.
    private void populateHouseholds() throws ImpossibleAllocationException {
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

     */

    private void populateHouseholds() throws ImpossibleAllocationException {
        createHouseholds();

        BitSet infantIndex = new BitSet(populationSize);
        BitSet childIndex = new BitSet(populationSize);
        BitSet adultIndex = new BitSet(populationSize);
        BitSet pensionerIndex = new BitSet(populationSize);

        createPopulation(adultIndex, pensionerIndex, childIndex, infantIndex);

        BitSet childOrInfant = new BitSet(populationSize);
        childOrInfant.or(childIndex);
        childOrInfant.or(infantIndex);

        // Fill requirements first
        for (Household h : households) {
            HouseholdType htype = h.getHouseholdType();
            while (htype.adultRequired()) {
                int i = adultIndex.nextSetBit(0);
                if (i < 0) {
                    throw new ImpossibleAllocationException("Population distribution cannot populate household distribution");
                }
                adultIndex.clear(i);
                h.addInhabitant(allPeople.get(i));
                htype.addAdult();
            }
            while (htype.childRequired()) {
                int i = childOrInfant.nextSetBit(0);
                if (i < 0) {
                    throw new ImpossibleAllocationException("Population distribution cannot populate household distribution");
                }
                childOrInfant.clear(i);
                h.addInhabitant(allPeople.get(i));
                htype.addChild();
            }
            while (htype.pensionerRequired()) {
                int i = pensionerIndex.nextSetBit(0);
                if (i < 0) {
                    throw new ImpossibleAllocationException("Population distribution cannot populate household distribution");
                }
                pensionerIndex.clear(i);
                h.addInhabitant(allPeople.get(i));
                htype.addPensioner();
            }
        }

        // Now fill in anyone who is missing
        while (!adultIndex.isEmpty() || !pensionerIndex.isEmpty() || !childOrInfant.isEmpty()) {
            for (Household h : households) {
                HouseholdType htype = h.getHouseholdType();
                if (htype.adultAllowed()) {
                    int i = adultIndex.nextSetBit(0);
                    if (i >= 0) {
                        adultIndex.clear(i);
                        h.addInhabitant(allPeople.get(i));
                        htype.addAdult();
                    }
                }
                if (htype.childAllowed()) {
                    int i = childOrInfant.nextSetBit(0);
                    if (i >= 0) {
                        childOrInfant.clear(i);
                        h.addInhabitant(allPeople.get(i));
                        htype.addChild();
                    }
                
                }
                if (htype.pensionerAllowed()) {
                    int i = pensionerIndex.nextSetBit(0);
                    if (i >= 0) {
                        pensionerIndex.clear(i);
                        h.addInhabitant(allPeople.get(i));
                        htype.addPensioner();
                    }
                }
            }
        }
    }

    // This creates the Communal places of different types where people mix
    private void createMixing() {
        int nHospitals = populationSize / PopulationParameters.get().getHospitalRatio();
        int nSchools = populationSize / PopulationParameters.get().getSchoolsRatio();
        int nShops = populationSize / PopulationParameters.get().getShopsRatio();
        int nOffices = populationSize / PopulationParameters.get().getOfficesRatio();
        int nConstructionSites = populationSize / PopulationParameters.get().getConstructionSiteRatio();
        int nNurseries = populationSize / PopulationParameters.get().getNurseriesRatio();
        int nRestaurants = populationSize / PopulationParameters.get().getRestaurantRatio();

        places.createNHospitals(nHospitals);
        places.createNSchools(nSchools);
        places.createNShops(nShops);
        places.createNOffices(nOffices);
        places.createNConstructionSites(nConstructionSites);
        places.createNNurseries(nNurseries);
        places.createNRestaurants(nRestaurants);

        LOGGER.info("Total number of establishments = {}", places.getAllPlaces().size());
    }

    // Allocates people to communal places - work environments
    public void allocatePeople() throws ImpossibleWorkerDistributionException {
        for (Household h : households) {
            for (Person p : h.getPeople() ) {
                p.allocateCommunalPlace(places);
            }
        }

        // Sometimes given parameters/randomness it's not possible to staff everywhere. In this case we throw an error.
        for (CommunalPlace p : places.getAllPlaces()) {
            if (!p.isFullyStaffed()) {
                throw new ImpossibleWorkerDistributionException("Not enough workers to fill required positions");
            }
        }
    }

    // This method assigns a random number of neighbours to each Household
    public void assignNeighbours() {
        for (Household cHouse : households) {
            int expectedNeighbours = PopulationParameters.get().getExpectedNeighbours();
            int nneighbours = (int) rng.nextPoisson(expectedNeighbours);
            for (int k = 0; k < nneighbours; k++) {

                Household neighbour = households.get(rng.nextInt(0, households.size() - 1));

                // Cannot be a neighbour of ourselves
                if (neighbour == cHouse) {
                    k--;
                    continue;
                }

                // Avoid duplicate neighbours
                if (cHouse.isNeighbour(neighbour)) {
                    k--;
                    continue;
                }

                cHouse.addNeighbour(neighbour);
            }
        }
    }


    // Force infections into a defined number of people
    public void seedVirus(int nInfections) {
        for (int i = 1; i <= nInfections; i++) {
            int nInt = rng.nextInt(0, numHouseholds - 1);
            if (households.get(nInt).getHouseholdSize() > 0) {
                if (!households.get(nInt).seedInfection()) i--;
            }
            if (households.get(nInt).getHouseholdSize() == 0) i--;
        }
    }
    
    public void timeStep(Time t, DailyStats dStats) {
        households.forEach(h -> h.doInfect(t, dStats));
        places.getAllPlaces().forEach(p -> p.doInfect(t, dStats));

        // There is a potential to introduce parallelism here if required by using parallelStream (see below).
        // Note we currently cannot parallelise movement as the ArrayLists for capturing moves are not thread safe
        // households.parallelStream().forEach(h -> h.doInfect(dStats));
        // places.getAllPlaces().parallelStream().forEach(p -> p.doInfect(dStats));

        // Movement places people in "next" buffers (to avoid people moving twice in an hour)
        households.forEach(h -> h.doMovement(t, lockdown));
        places.getAllPlaces().forEach(p -> p.doMovement(t, lockdown));

        households.forEach(h -> h.stepPeople());
        places.getAllPlaces().forEach(p -> p.stepPeople());
    }

    // Step through nDays in 1 hour time steps
    public List<DailyStats> simulate(int nDays) {
        List<DailyStats> stats = new ArrayList<>(nDays);
        Time t = new Time();
        boolean rprinted = false;
        for (int i = 0; i < nDays; i++) {
            DailyStats dStats = new DailyStats(t);
            implementLockdown(t);
            LOGGER.info("Day = {}, Lockdown = {}", t.getAbsDay(), lockdown);
            for (int k = 0; k < 24; k++) {
                timeStep(t, dStats);
                t = t.advance();
            }
            households.forEach(h -> h.dayEnd());
            stats.add(this.processCases(dStats));

            if (!rprinted) {
                rprinted = handleR(dStats, t.getAbsDay());
            }

        }
        return stats;
    }

    /** Log the R value for the first 5% of recoveries or lockdown */
    private boolean handleR(DailyStats s, int absDay) {
        if (s.getRecovered() >= populationSize * 0.05 || isLockdown()) {
            RStats rs = new RStats(this);
            LOGGER.info("R0 in initial stage: " + rs.getMeanRBefore(absDay));
            return true;
        }
        return false;
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

    // Tests on each daily time step whether to do anything with the lockdown
    private void implementLockdown(Time t) {
        int day = t.getAbsDay();
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

    // Sets the social distancing to parameters within the CommunalPlaces
    private void socialDistancing() {
        for (CommunalPlace cPlace : places.getAllPlaces()) {
            cPlace.adjustSDist(this.socialDist);
        }
    }

    // This method generates output at the end of each day
    private DailyStats processCases(DailyStats stats) {
        for (Person p : allPeople) {
            stats.processPerson(p);
        }
        stats.log();
        return stats;
    }

    // This sets the schools exempt from lockdown if that is triggered. Somewhat fudged at present by setting the schools to be KeyPremises - not entirely what thta was intended for, but it works
    private void schoolExemption() {
        for (School s : places.getSchools()) {
                s.overrideKeyPremises(true);
        }
        for (Nursery n : places.getNurseries()) {
            n.overrideKeyPremises(true);
        }
    }

    public ArrayList<Household> getHouseholds() {
        return households;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getNumHouseholds() {
        return numHouseholds;
    }

    public ArrayList<Person> getAllPeople() {
        return allPeople;
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

    public Places getPlaces() {
        return places;
    }
}
