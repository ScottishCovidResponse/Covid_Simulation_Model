/*
 * Paul Bessell
 * This is the principal driver class that initialises and manages a population of People
 */


package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.place.*;

import java.util.ArrayList;
import java.util.Random;

public class Population {

    private static final Logger LOGGER = LogManager.getLogger(Population.class);

    public int nInfants;
    public int nChildren;
    public int nAdults;
    public int nPensioners;
    private final int populationSize;
    private final int nHousehold;
    private final Household[] population;
    private final double pInfants; //Proportion of infants in the populaiton
    private final double pChildren; // Proportion of children
    private final double pAdults;
    private final double pPensioners;
    private final double pAdultOnly;
    private final double pPensionerOnly;
    private final double pPensionerAdult;
    private final double pAdultChildren;
    private final Person[] aPopulation;
    private final boolean[] infantIndex;
    private final boolean[] childIndex;
    private final boolean[] adultIndex;
    private final boolean[] pensionerIndex;
    private final boolean[] allocationIndex;
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
        if (this.nHousehold > this.populationSize) System.out.println("More households than population");

        this.population = new Household[this.nHousehold];
        this.pInfants = 0.08; // Another fudge. This defines the probability of a Person being an infant, adult etc, should be replaced by populaiton parameters
        this.pChildren = 0.2;
        this.pAdults = 0.5;
        this.pPensioners = 1 - this.pInfants - this.pChildren - this.pAdults;
        System.out.println("Population proportions pensioners = " + this.pPensioners + "Adults = " + this.pAdults + "Children = " + this.pChildren + "Infants = " + this.pInfants);

        LOGGER.info("Population proportions pensioners = {} Adults = {} Children = {} Infants = {}", pPensioners, pAdults, pChildren, pInfants);

        this.nInfants = 0;
        this.nChildren = 0;
        this.nAdults = 0;
        this.nPensioners = 0;
        this.aPopulation = new Person[this.populationSize];
        this.pAdultOnly = 0.3; // Currently a fudge - these values define the probability of a household being an adult only, adult and child household etc
        this.pPensionerOnly = 0.1;
        this.pPensionerAdult = 0.1;
        this.pAdultChildren = 0.5;
        this.infantIndex = new boolean[this.populationSize]; // These are indexes to assign membership to different groups - speeds up searching later
        this.childIndex = new boolean[this.populationSize];
        this.adultIndex = new boolean[this.populationSize];
        this.pensionerIndex = new boolean[this.populationSize];
        this.allocationIndex = new boolean[this.populationSize];
        this.lockdownStart = (-1);
        this.lockdownEnd = (-1);
        this.socialDist = 1.0;
        this.schoolL = false;
    }

    private Person createInfant() {
        return new Infant();
    }

    private Person createChild() {
        return new Child();
    }

    private Person createAdult() {
        return new Adult();
    }

    private Person createPensioner() {
        return new Pensioner();
    }

    // Creates the population of People based on the probabilities of age groups above
    private void createPopulation() {
        for (int i = 0; i < this.populationSize; i++) {
            double rand = Math.random();
            if (rand < this.pInfants) {
                this.aPopulation[i] = this.createInfant();
                this.infantIndex[i] = true;
                this.nInfants++;
            } else if (rand - this.pInfants < this.pChildren) {
                this.aPopulation[i] = this.createChild();
                this.childIndex[i] = true;
                this.nChildren++;
            } else if (rand - this.pInfants - this.pChildren < this.pAdults) {
                this.aPopulation[i] = this.createAdult();
                this.adultIndex[i] = true;
                this.nAdults++;
            } else {
                this.aPopulation[i] = this.createPensioner();
                this.pensionerIndex[i] = true;
                this.nPensioners++;
            }
        }

    }

    // Creates households based on probability of different household types
    private void createHouseholds() {
        for (int i = 0; i < this.nHousehold; i++) {
            double rand = Math.random();
            if (rand < this.pAdultOnly) {
                this.population[i] = this.createHousehold(1);
            } else if (rand - this.pAdultOnly < this.pPensionerOnly) {
                this.population[i] = this.createHousehold(2);
            } else if (rand - this.pAdultOnly - this.pPensionerOnly < this.pPensionerAdult) {
                this.population[i] = this.createHousehold(3);
            } else {
                this.population[i] = this.createHousehold(4);
            }
        }
    }

    // This bit of code really isn't necessary
    private Household createHousehold(int ntype) {
        return new Household(ntype);
    }


    // This is very slow and long  winded - for populating households based on the population age groups && It doesn't quite work as I would wish
    public void populateHouseholds() {
        this.createHouseholds();
        this.createPopulation();

        for (int i = 0; i < this.nHousehold; i++) {
            int cType = this.population[i].getnType();
            for (int k = 0; k < this.populationSize; k++) {
                if (cType == 1) {
                    if (!this.allocationIndex[k] && this.adultIndex[k]) {
                        this.allocationIndex[k] = true;
                        aPopulation[k].setHIndex(i);
                        this.population[i].addPerson(aPopulation[k]);
                    }
                }
                if (cType == 2) {
                    if (!this.allocationIndex[k] && this.pensionerIndex[k]) {
                        this.allocationIndex[k] = true;
                        aPopulation[k].setHIndex(i);
                        this.population[i].addPerson(aPopulation[k]);
                    }
                }
                if (cType == 3) {
                    if (!this.allocationIndex[k] && this.adultIndex[k]) {
                        this.allocationIndex[k] = true;
                        aPopulation[k].setHIndex(i);
                        this.population[i].addPerson(aPopulation[k]);
                        for (int l = 0; l < this.populationSize; l++) {
                            if (!this.allocationIndex[l] && this.pensionerIndex[l]) {
                                this.allocationIndex[l] = true;
                                aPopulation[k].setHIndex(i);
                                this.population[i].addPerson(aPopulation[l]);
                                l = this.populationSize + 1;
                            }
                        }
                    }
                }
                if (cType == 4) {
                    if (!this.allocationIndex[k] && this.adultIndex[k]) {
                        this.allocationIndex[k] = true;
                        aPopulation[k].setHIndex(i);
                        this.population[i].addPerson(aPopulation[k]);
                        for (int l = 0; l < this.populationSize; l++) {
                            if (!this.allocationIndex[l] && this.childIndex[l]) {
                                this.allocationIndex[l] = true;
                                aPopulation[k].setHIndex(i);
                                this.population[i].addPerson(aPopulation[l]);
                                l = this.populationSize + 1;
                            }
                        }

                    }
                }

                k = this.populationSize + 1;
            }
        }
        // Reallocate remaining population
        for (int i = 0; i < this.populationSize; i++) {
            if (!this.allocationIndex[i]) {
                while (!this.allocationIndex[i]) {
                    if (this.infantIndex[i] || this.childIndex[i]) {
                        for (int k = 0; k < this.nHousehold; k++) {
                            double rand = Math.random();
                            if (population[k].getnType() == 4) {
                                if (rand < 1 / (double) this.nHousehold) {
                                    this.allocationIndex[i] = true;
                                    aPopulation[i].setHIndex(k);

                                    this.population[k].addPerson(this.aPopulation[i]);
                                    k = this.populationSize + 1;
                                }
                            }
                        }
                    }
                    if (this.adultIndex[i]) {
                        for (int k = 0; k < this.nHousehold; k++) {
                            double rand = Math.random();
                            if (population[k].getnType() != 2) {
                                if (rand < 1 / (double) this.nHousehold) {
                                    this.allocationIndex[i] = true;
                                    aPopulation[i].setHIndex(k);

                                    this.population[k].addPerson(this.aPopulation[i]);
                                    k = this.populationSize + 1;
                                }
                            }
                        }
                    }
                    if (this.pensionerIndex[i]) {
                        for (int k = 0; k < this.nHousehold; k++) {
                            double rand = Math.random();
                            if (population[k].getnType() == 2 || population[k].getnType() == 3) {
                                if (rand < 1 / (double) this.nHousehold) {
                                    this.allocationIndex[i] = true;
                                    aPopulation[i].setHIndex(k);

                                    this.population[k].addPerson(this.aPopulation[i]);
                                    k = this.populationSize + 1;
                                }
                            }
                        }
                    }

                }
            }
        }
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
        int nHospitals = this.populationSize / 10000;
        int nSchools = this.populationSize / 2000;
        int nShops = this.populationSize / 500;
        int nOffices = this.populationSize / 250;
        int nConstructionSites = this.populationSize / 1000;
        int nNurseries = this.populationSize / 2000;
        int nRestaurants = this.populationSize / 1000;
        int nEstablishments = nHospitals + nSchools + nShops + nOffices + nConstructionSites + nNurseries + nRestaurants;
        this.shopIndexes = new int[nShops];
        this.restaurantIndexes = new int[nRestaurants];

        System.out.println(nEstablishments);

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
                if (cPerson.nursery) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Nursery)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.school) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof School)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.shopWorker) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Shop)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.constructionWorker) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof ConstructionSite)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.officeWorker) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Office)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.hospitalWorker) {

                    CommunalPlace property = this.getRandom();
                    while (!(property instanceof Hospital)) property = this.getRandom();
                    cPerson.setMIndex(property.getIndex());

                }
                if (cPerson.restaurant) {

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
            //System.out.println(nneighbours);
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
            System.out.println("Day = " + i);
            int dWeek = (i + 1) % 7;
            this.implementLockdown(i);
            System.out.println("Lockdown = " + this.lockdown);
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
            System.out.println("HERE");
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
                if (cPers.cStatus() == CStatus.HEALTHY) healthy++;
                if (cPers.cStatus() == CStatus.LATENT) exposed++;
                if (cPers.cStatus() == CStatus.ASYMPTOMATIC) asymptomatic++;
                if (cPers.cStatus() == CStatus.PHASE1) phase1++;
                if (cPers.cStatus() == CStatus.PHASE2) phase2++;
                if (cPers.cStatus() == CStatus.RECOVERED) recovered++;
            }
            dead += cHouse.getDeaths();
        }
        System.out.println("Day = " + day + " Healthy = " + healthy + " Latent = " + exposed + " Asymptomatic = " + asymptomatic + " Phase 1 = " + phase1 + " Phase 2 = " + phase2 + " Dead = " + dead + " Recovered = " + recovered);
        return day + "," + healthy + "," + exposed + "," + asymptomatic + "," + phase1 + "," + phase2 + "," + dead + "," + recovered;
    }

    // Step through the households to identify individual movements to CommunalPlaces
    private void cycleHouseholds(int day, int hour) {
        for (Household household : this.population) {
            ArrayList<Person> vHouse = household.cycleHouse();
            this.cycleMovements(vHouse, day, hour);
            this.retrunNeighbours(household);
            if (!this.lockdown) this.cycleNieghbours(household);
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
                System.out.println("HERE");
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
    private void cycleNieghbours(Household cHouse) {
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
    private void retrunNeighbours(Household cHouse) {
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
}