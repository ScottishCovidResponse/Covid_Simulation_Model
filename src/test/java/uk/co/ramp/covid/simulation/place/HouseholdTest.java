package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

public class HouseholdTest {

    Household household;
    int nneighbours;
    int[] neighbourArray;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("parameters/example_params.json");
        new RunModel(123);
        household = new Household(1);
        Person p1 = new Person();
        Person p2 = new Person();
        Person p3 = new Person();
        household.addPerson(p1);
        household.addPerson(p2);
        household.addPerson(p3);
        nneighbours = 5;
        neighbourArray = new int[]{3, 4, 1, 2, 1};
    }

    @Test
    public void testGetNeighbourIndex() {
        int ExpNeighbourIndex = 4;
        household.setNeighbourList(neighbourArray);
        assertEquals("Unexpected neighbours", ExpNeighbourIndex, household.getNeighbourIndex(1));
    }

    @Test
    public void testNNeighbours() {
        int ExpNNeighbour = 5;
        household.setNeighbourList(neighbourArray);
        assertEquals("Unexpected neighbour list", ExpNNeighbour, household.nNeighbours());
    }

    @Test
    public void testGetnType() {
        int expNType = 1;
        assertEquals("Unexpected household nType", expNType, household.getnType());
    }

    @Test
    public void testAddPerson() {
        Person p4 = new Person();
        household.addPerson(p4);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.vPeople.size());
    }

    @Test
    public void testGetHouseholdSize() {
        Person p1 = new Person();
        household.vPeople.add(p1);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getHouseholdSize());
    }

    @Test
    public void testGetPerson() {
        Person p4 = new Person();
        household.addPerson(p4);
        assertEquals("Unexpected person found", p4, household.getPerson(3));
    }

    @Test
    public void testSetNeighbourList() {
        household.setNeighbourList(neighbourArray);
        assertEquals("Unexpected number of neighbours", nneighbours, neighbourArray.length);
    }

    @Test
    public void testSeedInfection() {
        assertTrue("Infection not seeded", household.seedInfection());
    }

    @Test
    public void testCombVectors() {
        Household newHouse = new Household(1);
        Person p4 = new Person();
        newHouse.addPerson(p4);
        household.welcomeNeighbours(newHouse);
        int expSize = 4;
        assertEquals("Unexpected combined list size", expSize, household.combVectors().size());
    }

    @Test
    public void testCycleHouse() {
        int expSize = 3;
        assertEquals("Unexpected household size", expSize, household.cycleHouse().size());
    }

    @Test
    public void testGetDeaths() {
        Person p1 = new Person();
        Person p2 = new Person();
        household.vDeaths.add(p1);
        household.vDeaths.add(p2);
        int expDeaths = 2;
        assertEquals("Unexpected number of deaths", expDeaths, household.getDeaths());
    }

    @Test
    public void testWelcomeNeighbours() {
        Household newHouse = new Household(1);
        Person p1 = new Person();
        Person p2 = new Person();
        newHouse.addPerson(p1);
        newHouse.addPerson(p2);
        household.welcomeNeighbours(newHouse);
        int expSize = 2;
        assertEquals("Unexpected household size", expSize, household.vVisitors.size());

    }

    @Ignore //This test requires repeatable random numbers to pass
    @Test
    public void testSendNeighboursHome() {
        Household newHouse = new Household(1);
        Person p1 = new Person();
        newHouse.addPerson(p1);
        household.welcomeNeighbours(newHouse);
        int expSize = 1;
        assertEquals("Unexpected number of visitors", expSize, household.sendNeighboursHome().size());
    }

    @Test
    public void testShoppingTrip() {
        int expPeople = 3;
        assertEquals("Unexpected number of people shopping", expPeople, household.shoppingTrip().size());
    }

    @Test
    public void testGetInhabitants() {
        int expSize = 3;
        assertEquals("Unexpected number of inhabitants", expSize, household.getInhabitants().size());

    }
}