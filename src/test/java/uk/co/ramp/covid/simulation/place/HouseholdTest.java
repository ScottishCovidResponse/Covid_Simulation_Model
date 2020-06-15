package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.place.householdtypes.LargeManyAdultFamily;
import uk.co.ramp.covid.simulation.place.householdtypes.SingleAdult;
import uk.co.ramp.covid.simulation.place.householdtypes.SmallFamily;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.population.Child;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.List;

import static org.junit.Assert.*;

public class HouseholdTest extends SimulationTest {

    Household household;

    //Neighbours
    Household household2;
    Household household3;
    Household household4;

    @Before
    public void initialise() throws JsonParseException {
        household = new LargeManyAdultFamily();
        Adult p1 = new Adult(30, Person.Sex.MALE);
        Adult p2 = new Adult(32, Person.Sex.FEMALE);
        Adult p3 = new Adult(30, Person.Sex.MALE);
        household.addAdult(p1);
        household.addAdult(p2);
        household.addAdult(p3);
        household2 = new SmallFamily();
        household3 = new SmallFamily();
        household4 = new SmallFamily();
    }

    @Test
    public void testNNeighbours() {
        int ExpNNeighbour = 3;
        household.addNeighbour(household2);
        household.addNeighbour(household3);
        household.addNeighbour(household4);
        assertEquals("Unexpected neighbour list", ExpNNeighbour, household.nNeighbours());
    }

    @Test
    public void testAddPerson() {
        Adult p4 = new Adult(40, Person.Sex.FEMALE);
        household.addAdult(p4);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getInhabitants().size());
    }

    @Test
    public void testGetHouseholdSize() {
        Adult p1 = new Adult(50, Person.Sex.MALE);
        household.addAdult(p1);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getHouseholdSize());
    }

    @Test
    public void testSeedInfection() {
        assertTrue("Infection not seeded", household.seedInfection());
    }

    @Test
    public void testSendNeighboursHome() {
        PopulationParameters.get().householdProperties.pVisitorsLeaveHousehold = new Probability(1.0);
        Household h = new SmallFamily();
        Person p1 = new Adult(22, Person.Sex.FEMALE);
        p1.setHome(household);
        h.addPersonNext(p1);
        h.commitMovement();

        h.sendNeighboursHome(new Time(0));
        h.commitMovement();
        assertEquals("Unexpected number of visitors", 0,h.getVisitors().size());
    }

    @Test
    public void testGetInhabitants() {
        int expSize = 3;
        assertEquals("Unexpected number of inhabitants", expSize, household.getInhabitants().size());
    }

    @Test (expected = InvalidHouseholdAllocationException.class)
    public void testInvalidHouseholdAllocation() {
        Household house = new SingleAdult();
        Adult p1 = new Adult(40, Person.Sex.FEMALE);
        Child c1 = new Child(10, Person.Sex.FEMALE);
        house.addAdult(p1);
        house.addChildOrInfant(c1);
    }

    @Test
    public void somePeopleDieAtHome() {
        int population = 10000;
        int nInfections = 200;
        int nIter = 1;
        int nDays = 60;
        int RNGSeed = 42;

        // Make the test more robust by reducing the number of phase 2 patients that will go to hospital
        CovidParameters.get().hospitalisationParameters.pPhase2GoesToHosptial = new Probability(0.2);

        Model m = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(nIter)
                .setnDays(nDays)
                .setRNGSeed(RNGSeed)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run(0);

        int totalHomeDeaths = 0;
        for (DailyStats s : stats.get(0)) {
            totalHomeDeaths += s.getHomeDeaths();
        }
        assertTrue("Some people should die at home", totalHomeDeaths > 0);
    }


}
