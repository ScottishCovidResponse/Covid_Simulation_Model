/*
 * Paul Bessell
 * This is the principal driver class that initialises and manages a population of People
 */


package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.lockdown.LockdownController;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.output.RStats;
import uk.co.ramp.covid.simulation.output.network.ContactsWriter;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.HouseholdProperties;
import uk.co.ramp.covid.simulation.parameters.PopulationDistribution;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Population {

    private static final Logger LOGGER = LogManager.getLogger(Population.class);

    private final int populationSize;
    private final int numHouseholds;

    private final ArrayList<Person> allPeople;
    private final Transport publicTransport;
    private final Places places;

    private final LockdownController lockdownController;
    
    private final InfectionSeeder seeder;

    private final RandomDataGenerator rng;

    private boolean rPrinted = false;
    private boolean shouldPrintR = false;

    // Hook to make it easier to test properties after each hour
    private BiConsumer<Population, Time> postHourHook;

    public Population(int populationSize) throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        this.rng = RNG.get();
        this.populationSize = populationSize;

        this.numHouseholds = (int) (populationSize / PopulationParameters.get().householdDistribution.populationToHouseholdsRatio);

        if (numHouseholds == 0) {
            throw new ImpossibleAllocationException("No households requested");
        }
        if (numHouseholds > populationSize) {
            throw new ImpossibleAllocationException("More households than people requested");
        }
        
        this.allPeople = new ArrayList<>(populationSize);
        this.places = new Places(populationSize, numHouseholds);

        postHourHook = (p,t) -> {};

        lockdownController = new LockdownController();

        publicTransport = new Transport(populationSize);
        
        seeder = new InfectionSeeder(this);

        allocatePopulation();
    }
    
    private void allocatePopulation() throws ImpossibleAllocationException, ImpossibleWorkerDistributionException {
        populateHouseholds();
        allocatePeople();
        allocateCareHomes();
        assignNeighbours();
        assignPublicTransport();
    }

    private void assignPublicTransport() {
        for (Household h : getHouseholds()) {
            h.determinePublicTransportTakers(publicTransport);
        }
    }

    private void allocateCareHomes() throws ImpossibleAllocationException {
        for (Household h : getHouseholds()) {
            h.trySendPensionersToCare(getPlaces());
        }

        for (CareHome h : getPlaces().getCareHomes()) {
            if (!h.residentsInRange()) {
                throw new ImpossibleAllocationException("Carehome with invalid number of residents");
            }
        }
    }

    // Creates the population of People based on the probabilities of age groups above
    private void createPopulation(BitSet adultIndex, BitSet pensionerIndex,
                                  BitSet childIndex, BitSet infantIndex) {
        PopulationDistribution dist = new PopulationDistribution();
        dist.readFromMap(PopulationParameters.get().populationDistribution);
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

    // We know based on the bitset indexes that this cast is safe so ignore warnings here
    @SuppressWarnings("unchecked")
    private <T extends Person> void allocateRequired(BitSet remaining, Supplier<Boolean> required, Consumer<T> add)
            throws ImpossibleAllocationException {
        int i = 0;
        while (required.get()) {
            i = remaining.nextSetBit(i);
            if (i < 0) {
                throw new ImpossibleAllocationException(
                        "Population distribution cannot populate household distribution");
            }
            remaining.clear(i);
            add.accept((T) allPeople.get(i));
        }
    }

    // We know based on the bitset indexes that this cast is safe so ignore warnings here
    @SuppressWarnings("unchecked")
    private  <T extends Person> void allocateAllowed(BitSet remaining, Supplier<Boolean> allowed, Consumer<T> add) {
        int i = 0;
        if (allowed.get()) {
            i = remaining.nextSetBit(i);
            if (i >= 0) {
                remaining.clear(i);
                add.accept((T) allPeople.get(i));
            }
        }
    }

    private void populateHouseholds() throws ImpossibleAllocationException {
        BitSet infantIndex = new BitSet(populationSize);
        BitSet childIndex = new BitSet(populationSize);
        BitSet adultIndex = new BitSet(populationSize);
        BitSet pensionerIndex = new BitSet(populationSize);

        createPopulation(adultIndex, pensionerIndex, childIndex, infantIndex);

        // When allocating we often treat children and infants as one type, likewise with adults/pensioners
        BitSet childOrInfant = new BitSet(populationSize);
        childOrInfant.or(childIndex);
        childOrInfant.or(infantIndex);

        BitSet adultAnyAge = new BitSet(populationSize);
        adultAnyAge.or(adultIndex);
        adultAnyAge.or(pensionerIndex);

        // Fill requirements first
        for (Household h : places.getHouseholds()) {
            allocateRequired(adultAnyAge, h::adultAnyAgeRequired, h::addAdultOrPensioner);

            // Set intersections keep adultAnyAge in sync with adults/pensioners
            adultIndex.and(adultAnyAge);
            pensionerIndex.and(adultAnyAge);

            allocateRequired(adultIndex, h::adultRequired, h::addAdult);
            allocateRequired(pensionerIndex, h::pensionerRequired, h::addPensioner);

            // The only way to know what might have changed with adults/pensioners is to just recalculate the set
            adultAnyAge = new BitSet(populationSize);
            adultAnyAge.or(adultIndex);
            adultAnyAge.or(pensionerIndex);

            allocateRequired(childOrInfant, h::childRequired, h::addChildOrInfant);
        }

        // Now fill in anyone who is missing
        while (!adultIndex.isEmpty() || !pensionerIndex.isEmpty() || !childOrInfant.isEmpty()) {
            for (Household h : places.getHouseholds()) {
                allocateAllowed(adultAnyAge, h::additionalAdultAnyAgeAllowed, h::addAdultOrPensioner);
                adultIndex.and(adultAnyAge);
                pensionerIndex.and(adultAnyAge);

                allocateAllowed(adultIndex, h::additionalAdultsAllowed, h::addAdult);
                allocateAllowed(pensionerIndex, h::additionalPensionersAllowed, h::addPensioner);

                adultAnyAge = new BitSet(populationSize);
                adultAnyAge.or(adultIndex);
                adultAnyAge.or(pensionerIndex);

                allocateAllowed(childOrInfant, h::additionalChildrenAllowed, h::addChildOrInfant);
            }
        }

    }



    // Allocates people to communal places - work environments
    public void allocatePeople() throws ImpossibleWorkerDistributionException {
        for (Household h : places.getHouseholds()) {
            for (Person p : h.getPeople() ) {
                p.allocateCommunalPlace(places);
            }
        }

        // Sometimes given parameters/randomness it's not possible to staff everywhere. In this case we throw an error.
        for (CommunalPlace p : places.getCommunalPlaces()) {
            if (!p.isFullyStaffed()) {
                throw new ImpossibleWorkerDistributionException("Not enough workers to fill required positions");
            }
        }
    }

    private void createNeighbourGroupDistributions(ProbabilityDistribution<List<Household>> adultDist,
                                                   ProbabilityDistribution<List<Household>> familyDist,
                                                   ProbabilityDistribution<List<Household>> pensionerDist) {
        List<Household> adultGroup = new ArrayList<>();
        List<Household> familyGroup = new ArrayList<>();
        List<Household> pensionerGroup = new ArrayList<>();

        for (Household h : places.getHouseholds()) {
            switch (h.getNeighbourGroup()) {
                case ADULT: adultGroup.add(h); break;
                case FAMILY: familyGroup.add(h); break;
                case PENSIONER: pensionerGroup.add(h); break;
            }
        }

        HouseholdProperties hprops = PopulationParameters.get().householdProperties;
        adultDist.add(hprops.pNeighbourFromSameGroup, adultGroup);
        adultDist.add(hprops.pNeighbourFromOtherGroup, pensionerGroup);
        adultDist.add(hprops.pNeighbourFromOtherGroup, familyGroup);

        familyDist.add(hprops.pNeighbourFromSameGroup, familyGroup);
        familyDist.add(hprops.pNeighbourFromOtherGroup, pensionerGroup);
        familyDist.add(hprops.pNeighbourFromOtherGroup, adultGroup);

        pensionerDist.add(hprops.pNeighbourFromSameGroup, pensionerGroup);
        pensionerDist.add(hprops.pNeighbourFromOtherGroup, familyGroup);
        pensionerDist.add(hprops.pNeighbourFromOtherGroup, adultGroup);
    }

    // This method assigns a random number of neighbours to each Household
    public void assignNeighbours() {
        ProbabilityDistribution<List<Household>> adultDist = new ProbabilityDistribution<>();
        ProbabilityDistribution<List<Household>> familyDist = new ProbabilityDistribution<>();
        ProbabilityDistribution<List<Household>> pensionerDist = new ProbabilityDistribution<>();

        createNeighbourGroupDistributions(adultDist, familyDist, pensionerDist);

        for (Household h : places.getHouseholds()) {
            int expectedNeighbours = PopulationParameters.get().householdProperties.expectedNeighbours;
            int nneighbours = (int) rng.nextPoisson(expectedNeighbours);

            ProbabilityDistribution<List<Household>> dist = null;
            switch (h.getNeighbourGroup()) {
                case ADULT: dist = adultDist; break;
                case FAMILY: dist = familyDist; break;
                case PENSIONER: dist = pensionerDist; break;
            }

            for (int k = 0; k < nneighbours; k++) {
                List<Household> neighbourGroup = dist.sample();
                
                Household neighbour = neighbourGroup.get(rng.nextInt(0, neighbourGroup.size() - 1));

                // Cannot be a neighbour of ourselves
                if (neighbour == h) {
                    k--;
                    continue;
                }

                // Avoid duplicate neighbours
                if (h.isNeighbour(neighbour)) {
                    k--;
                    continue;
                }

                h.addNeighbour(neighbour);
            }
        }
    }


    public void timeStep(Time t, DailyStats dStats) {
        timeStep(t, dStats, null);
    }
    
    public void timeStep(Time t, DailyStats dStats, ContactsWriter contactsWriter) {
        for (Household h : places.getHouseholds()) {
            h.handleSymptomaticCases(t);
        }

        /* Movement is performed in two steps:
         *  1. Determine where people will move to in the next hour using determineMovement which places
         *     them in the "nextPeople" list of the place they are moving to
         *  2. Commit the movement by swapping the "nextPeople" and "people" (in commitMovement below)
         *
         *  This approach avoids ordering effects and makes it harder to accidentally move someone twice
         *  (as we only ever iterate over "people" never "nextPeople")
         *
         *  Importantly, for each time step *every* person must move, even if this is back to the same place.
         *
         *  The movement itself is determined on a per-place basis, for example, shops have additional functionality
         *  compared with offices, to allow /visitors/ to leave at some rate. New places should override determineMovement
         *  to implement custom movement functionality.
         */
        for (Place p : places.getAllPlaces()) {
            p.doInfect(t, dStats, contactsWriter);
            p.determineMovement(t, dStats, getPlaces());
        }

        publicTransport.doInfect(t, dStats, contactsWriter);

        for (Place p : places.getAllPlaces()) {
            p.commitMovement();
        }
        
        if (contactsWriter != null) {
            contactsWriter.finishTimeStep(t);
        }

    }
    
    public List<DailyStats> simulateFromTime(Time startTime, int nDays) {
        return simulateFromTime(startTime, nDays, null);
    }

    // Step through nDays in 1 hour time steps
    public List<DailyStats> simulateFromTime(Time startTime, int nDays, ContactsWriter contactsWriter) {
        List<DailyStats> stats = new ArrayList<>(nDays);
        Time t = startTime;
        
        // To ensure we disallow neighbour visits on day 0 if required, we need to implement
        // lockdown first here (events are popped once they are done so this only happens once on day 0)
        lockdownController.implementLockdown(t);
        places.getHouseholds().forEach(Household::determineDailyNeighbourVisit);

        for (int i = 0; i < nDays; i++) {
            DailyStats dStats = new DailyStats(t);
            lockdownController.implementLockdown(t);

            seeder.seedInfections(t, dStats);

            LOGGER.info("Day = {}", t.getAbsDay());
            for (int k = 0; k < 24; k++) {
                timeStep(t, dStats, contactsWriter);
                t = t.advance();
                postHourHook.accept(this, t);
            }
            places.getHouseholds().forEach(Household::dayEnd);

            // At the end of each day we also determine possible hospital visits for the next day
            for (Person p : allPeople) {
                p.deteremineHospitalVisits(t, places);
            }

            stats.add(this.processCases(dStats));

            if (!rPrinted) {
                if (shouldPrintR || dStats.recovered.get() >= populationSize * 0.05) {
                    printR(dStats, t.getAbsDay());
                }
            }

        }
        return stats;
    }

    public List<DailyStats> simulate(int nDays) {
        return simulate(nDays, null);
    }

    // Step through nDays in 1 hour time steps
    public List<DailyStats> simulate(int nDays, ContactsWriter contactsWriter) {
        return simulateFromTime(new Time(), nDays, contactsWriter);
    }

    /** Log the R value for the first 5% of recoveries or lockdown */
    private void printR(DailyStats s, int absDay) {
        if (s.recovered.get() >= populationSize * 0.05) {
            RStats rs = new RStats(this);
            LOGGER.info("R0 in initial stage: " + rs.getMeanRBefore(absDay));
            rPrinted = true;
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

    public List<Household> getHouseholds() {
        return places.getHouseholds();
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

    public Places getPlaces() {
        return places;
    }

    public LockdownController getLockdownController() {
        return lockdownController;
    }

    public void setPostHourHook(BiConsumer<Population, Time> postHourHook) {
        this.postHourHook = postHourHook;
    }
    
    public void setShouldPrintR() {
        shouldPrintR = true;
    }
    
    public InfectionSeeder getSeeder() { return seeder; }
}
