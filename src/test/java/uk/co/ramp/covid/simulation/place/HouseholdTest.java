package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.RNG;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

public class HouseholdTest {

    Household household;
    int nneighbours;
    int[] neighbourArray;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(123);
        household = new Household(Household.HouseholdType.ADULT);
        Person p1 = new Adult();
        Person p2 = new Adult();
        Person p3 = new Adult();
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
        Household.HouseholdType expHType = Household.HouseholdType.ADULT;
        assertEquals("Unexpected household nType", expHType, household.gethType());
    }

    @Test
    public void testAddPerson() {
        Person p4 = new Adult();
        household.addPerson(p4);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getInhabitants().size());
    }

    @Test
    public void testGetHouseholdSize() {
        Person p1 = new Adult();
        household.addPerson(p1);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getHouseholdSize());
    }

    @Test
    public void testGetPerson() {
        Person p4 = new Adult();
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
    public void testCycleHouse() {
        int expSize = 3;
        DailyStats s = new DailyStats(0);
        assertEquals("Unexpected household size", expSize, household.cycleHouse(s).size());
    }

    @Test
    public void testWelcomeNeighbours() {
        Household newHouse = new Household(Household.HouseholdType.ADULT);
        Person p1 = new Adult();
        Person p2 = new Adult();
        newHouse.addPerson(p1);
        newHouse.addPerson(p2);
        household.welcomeNeighbours(newHouse);
        int expSize = 2;
        assertEquals("Unexpected household size", expSize, household.getVisitors().size());

    }
    
    @Test
    public void testSendNeighboursHome() {
        Household newHouse = new Household(Household.HouseholdType.ADULT);
        Person p1 = new Adult();
        newHouse.addPerson(p1);
        p1.setHome(newHouse);
        household.welcomeNeighbours(newHouse);
        int expSize = 1;
        assertEquals("Unexpected number of visitors", expSize, household.sendNeighboursHome());
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