/*
 * Paul Bessell
 * This is the principal driver class that initialises and manages a population of People
 */


package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.place.*;

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

    public Population(int populationSize, int nHousehold) {
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
        double pInfants = PopulationParameters.get().getpInfants();
        double pChildren = PopulationParameters.get().getpChildren();
        double pAdults = PopulationParameters.get().getpAdults();

        for (int i = 0; i < this.populationSize; i++) {
            double rand = Math.random();
            if (rand < pInfants) {
                this.aPopulation[i] = new Infant();
                infantIndex.set(i);
            } else if (rand - pInfants < pChildren) {
                this.aPopulation[i] = new Child();
                childIndex.set(i);
            } else if (rand - pInfants - pChildren < pAdults) {
                this.aPopulation[i] = new Adult();
                adultIndex.set(i);
            } else {
                this.aPopulation[i] = new Pensioner();
                pensionerIndex.set(i);
            }
        }
    }

    // Creates households based on probability of different household types
    private void createHouseholds() {
        // Java doesn't have built-in Pairs, so we need to define our own
        class ProbPair  implements Comparable<ProbPair> {
            public double prob; public int householdType;
            public ProbPair(double p, int ntype) {
                prob = p;
                householdType = ntype;
            }
            public int compareTo(ProbPair p) { return Double.compare(prob, p.prob); }
        };

        //  pmap works as an associative list to allow us to easy assign probabilities with any number of household types.
        List<ProbPair> pmap = new ArrayList();
        pmap.add(new ProbPair(PopulationParameters.get().getpAdultOnly(), 1));
        pmap.add(new ProbPair(PopulationParameters.get().getpPensionerOnly(), 2));
        pmap.add(new ProbPair(PopulationParameters.get().getpPensionerAdult(), 3));
        pmap.add(new ProbPair(PopulationParameters.get().getpAdultChildren(), 4));
        pmap.add(new ProbPair(PopulationParameters.get().getpPensionerChildren(), 5));
        pmap.add(new ProbPair(PopulationParameters.get().getpAdultPensionerChildren(), 6));

        Collections.sort(pmap, Collections.reverseOrder());

        for (int i = 0; i < this.nHousehold; i++) {
            double rand = Math.random();
            for (ProbPair p : pmap ) {
                if (rand < p.prob) {
                    population[i] = new Household(p.householdType);
                    break;
                }
                rand = rand - p.prob;
            }
        }
    }

    // Checks we have enough people of the right types to perform a household allocation
    private boolean householdAllocationPossible(BitSet adultIndex, BitSet pensionerIndex,
                                                BitSet childIndex, BitSet infantIndex) {
        int adult = 0; int pensioner = 0; int adultPensioner = 0; int adultChild = 0;
        int pensionerChild = 0; int adultPensionerChild = 0;
        for (Household h : population) {
            switch(h.getnType()) {
                case 1:
                    adult++;
                    break;
                case 2:
                    pensioner++;
                    break;
                case 3:
                    adultPensioner++;
                    break;
                case 4:
                    adultChild++;
                    break;
                case 5:
                    pensionerChild++;
                    break;
                case 6:
                    adultPensionerChild++;
                    break;
            }
        }
        return adultIndex.cardinality() <= adult + adultPensioner + adultChild + adultPensionerChild
                || pensionerIndex.cardinality() <= adultPensioner + pensioner + pensionerChild
                || childIndex.cardinality() <= adultChild + pensionerChild + adultPensionerChild
                || infantIndex.cardinality() <= adultChild + pensionerChild + adultPensionerChild;
    }

    // Cycles over all bits of remainingPeople and ensures they are allocated to a household in a greedy fashion
    private void greedyAllocate(BitSet remainingPeople, Set<Integer> types) {
        int i = 0; // Pointer into remaining
        for (int h = 0; h < population.length; h++) {
            int htype = population[h].getnType();
            if (types.contains(htype)) {
                i = remainingPeople.nextSetBit(i);
                if (i < 0) {
                    break;
                }
                remainingPeople.clear(i);
                aPopulation[i].setHIndex(h);
                population[h].addPerson(aPopulation[i]);
            }
        }
    }

    private void probAllocate(BitSet remainingPeople, Set<Integer> types, Map<Integer, Double> probabilities) {
        int i = 0; // Pointer into remaining
        while (!remainingPeople.isEmpty()) {
            for (int h = 0; h < population.length; h++) {
                int htype = population[h].getnType();
                if (types.contains(htype)) {
                    double rand = Math.random();
                    double prob_to_add = probabilities.getOrDefault(population[h].getHouseholdSize(), 1.0);
                    if (rand < prob_to_add) {
                        i = remainingPeople.nextSetBit(i);
                        if (i < 0) {
                            break;
                        }
                        aPopulation[i].setHIndex(h);
                        population[h].addPerson(aPopulation[i]);
                        remainingPeople.clear(i);
                    }
                }
            }
        }
    }

    // We populate houseHolds greedily.
    public void populateHouseholds() {
        createHouseholds();

        BitSet infantIndex = new BitSet(populationSize);
        BitSet childIndex = new BitSet(populationSize);
        BitSet adultIndex = new BitSet(populationSize);
        BitSet pensionerIndex = new BitSet(populationSize);

        createPopulation(adultIndex, pensionerIndex, childIndex, infantIndex);

        assert householdAllocationPossible(adultIndex, pensionerIndex, childIndex, infantIndex)
                : "Population distribution cannot populate household distribution";

        // Ensures miminal constraints are met
        greedyAllocate(adultIndex, new HashSet<>(Arrays.asList(1, 3, 4, 6)));
        greedyAllocate(pensionerIndex, new HashSet<>(Arrays.asList(2, 3, 5, 6)));

        // For OR constraints, e.g. child or infant, we union the bitsets during the greedy algorithm
        // For the probabilistic allocations below, they can have different probabilities.
        BitSet childOrInfant = new BitSet(populationSize);
        childOrInfant.or(childIndex);
        childOrInfant.or(infantIndex);

        greedyAllocate(childOrInfant, new HashSet<>(Arrays.asList(4, 5, 6)));

        // set intersections to allow children/infants to be treated independently again
        childIndex.and(childOrInfant);
        infantIndex.and(childOrInfant);

        probAllocate(adultIndex,
                new HashSet<Integer>(Arrays.asList(1, 3, 4, 6)),
                PopulationParameters.get().getAdultAllocationPMap());
        probAllocate(pensionerIndex,
                new HashSet<Integer>(Arrays.asList(2, 3, 5, 6)),
                PopulationParameters.get().getPensionerAllocationPMap());
        probAllocate(childIndex,
                new HashSet<Integer>(Arrays.asList(4, 5, 6)),
                PopulationParameters.get().getChildAllocationPMap());
        probAllocate(infantIndex,
                new HashSet<Integer>(Arrays.asList(4, 5, 6)),
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
                if (cPerson.isNursery()) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Nursery)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.isSchool()) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof School)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.isShopWorker()) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Shop)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.isConstructionWorker()) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof ConstructionSite)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.isOfficeWorker()) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Office)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.isHospitalWorker()) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Hospital)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.isRestaurant()) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Restaurant)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }

            }
        }
        this.assignNeighbours();
    }

    // For selecting a CommunalPlace at random to assign a People to
    private CommunalPlace getRandom() {
        int rnd = new Random().nextInt(this.cPlaces.length);
        return this.cPlaces[rnd];
    }

    // This method assigns a random number of neighbours to each Household
    private void assignNeighbours() {
        for (int i = 0; i < this.nHousehold; i++) {
            Household cHouse = this.population[i];
            int nneighbours = new PoissonDistribution(3).sample(); // Sample a number of neighbours based on mean of three neighbours
            int[] neighbourArray = new int[nneighbours];
            for (int k = 0; k < nneighbours; k++) {
                int nInt = new Random().nextInt(this.nHousehold);
                if (nInt == i) k--;
                else neighbourArray[k] = nInt;
            }
            cHouse.setNeighbourList(neighbourArray);
        }
    }


    // Force infections into a defined number of people
    public void seedVirus(int nInfections) {
        for (int i = 1; i <= nInfections; i++) {
            int nInt = new Random().nextInt(this.nHousehold);
            if (this.population[nInt].getHouseholdSize() > 0) {
                if (!population[nInt].seedInfection()) i--;
            }
            if (this.population[nInt].getHouseholdSize() == 0) i--;
        }
    }

    // Step through nDays in 1 hour time steps
    public ArrayList<String> timeStep(int nDays) {
        ArrayList<String> outV = new ArrayList<>();
        for (int i = 0; i < nDays; i++) {
            LOGGER.info("Day = {}", i);
            int dWeek = (i + 1) % 7;
            this.implementLockdown(i);
            LOGGER.info("Lockdown = {}", this.lockdown);
            for (int k = 0; k < 24; k++) {
                this.cycleHouseholds(dWeek, k);
                this.cyclePlaces(dWeek, k);
                this.returnShoppers(k);
                this.returnRestaurant(k);
                this.shoppingTrip(dWeek, k);
                if (!this.rLockdown) this.restaurantTrip(dWeek, k);
            }
            outV.add(this.processCases(i));
        }
        return outV;
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
    private String processCases(int day) {
        int healthy = 0;
        int exposed = 0;
        int asymptomatic = 0;
        int phase1 = 0;
        int phase2 = 0;
        int dead = 0;
        int recovered = 0;

        for (Household cHouse : this.population) {
            ArrayList<Person> vHouse = cHouse.combVectors();
            for (Person cPers : vHouse) {
                switch (cPers.cStatus()) {
                    case HEALTHY -> healthy++;
                    case LATENT -> exposed++;
                    case ASYMPTOMATIC -> asymptomatic++;
                    case PHASE1 -> phase1++;
                    case PHASE2 -> phase2++;
                    case RECOVERED -> recovered++;
                    default -> LOGGER.info("Invalid Status");
                }
            }
            dead += cHouse.getDeaths();
        }
        LOGGER.info("Healthy = {} Latent = {} Asymptomatic = {} Phase 1 = {} Phase 2 = {} Dead = {} Recovered = {}", healthy, exposed, asymptomatic,phase1, phase2, dead, recovered);
        return day + "," + healthy + "," + exposed + "," + asymptomatic + "," + phase1 + "," + phase2 + "," + dead + "," + recovered;
    }

    // Step through the households to identify individual movements to CommunalPlaces
    private void cycleHouseholds(int day, int hour) {
        for (Household household : this.population) {
            ArrayList<Person> vHouse = household.cycleHouse();
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
            if (nPers.getMIndex() >= 0 && !nPers.getQuarantine()) {
                boolean visit = this.cPlaces[nPers.getMIndex()].checkVisit(nPers, hour, day, this.lockdown);
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
    private void cyclePlaces(int day, int hour) {
        for (CommunalPlace cPlace : this.cPlaces) {
            ArrayList<Person> retPeople = cPlace.cyclePlace(hour, day);
            for (Person cPers : retPeople) {
                population[cPers.getHIndex()].addPerson(cPers);
            }
        }
    }

    // Go through neighbours and see if they visit anybody
    private void cycleNeighbours(Household cHouse) {
        int visitIndex = -1; // Set a default for this here.

        if (cHouse.nNeighbours() > 0 && cHouse.getHouseholdSize() > 0) {
            int k = 0;
            while (k < cHouse.nNeighbours()) {
                if (Math.random() < (1.0 / 7.0 / 24.0)) {
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
            this.population[nPers.getHIndex()].addPerson(nPers);
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
                if (Math.random() < visitProb) {
                    vNext = household.shoppingTrip();
                }
                if (vNext != null) {
                    int shopSample = new Random().nextInt(this.shopIndexes.length);
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
                    this.population[nPers.getHIndex()].addPerson(nPers);
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
                if (Math.random() < visitProb) {
                    vNext = household.shoppingTrip(); // This method is fine for our purposes here
                }
                if (vNext != null) {
                    int shopSample = new Random().nextInt(this.restaurantIndexes.length);
                    ((Restaurant) this.cPlaces[this.restaurantIndexes[shopSample]]).shoppingTrip(vNext);
                }
                vNext = null;
            }
        }
    }

    // People return from dinner
    private void returnRestaurant(int hour) {
        for (int shopIndex : this.shopIndexes) {
            ArrayList<Person> vCurr = ((Shop) this.cPlaces[shopIndex]).sendHome(hour);
            if (vCurr != null) {
                for (Person nPers : vCurr) {
                    this.population[nPers.getHIndex()].addPerson(nPers);
                }
            }
        }
    }

    public Household[] getPopulation() {
        return population;
    }
}
