package uk.co.ramp.covid.simulation.place;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.Adult;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.util.RNG;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

public class HouseholdTest {

    Household household;

    //Neighbours
    Household household2;
    Household household3;
    Household household4;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        RNG.seed(123);
        household = new Household(Household.HouseholdType.ADULT);
        Person p1 = new Adult();
        Person p2 = new Adult();
        Person p3 = new Adult();
        household.addInhabitant(p1);
        household.addInhabitant(p2);
        household.addInhabitant(p3);
        household2 = new Household(Household.HouseholdType.ADULT);
        household3 = new Household(Household.HouseholdType.ADULT);
        household4 = new Household(Household.HouseholdType.ADULT);

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
    public void testGetnType() {
        Household.HouseholdType expHType = Household.HouseholdType.ADULT;
        assertEquals("Unexpected household nType", expHType, household.gethType());
    }

    @Test
    public void testAddPerson() {
        Person p4 = new Adult();
        household.addInhabitant(p4);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getInhabitants().size());
    }

    @Test
    public void testGetHouseholdSize() {
        Person p1 = new Adult();
        household.addInhabitant(p1);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getHouseholdSize());
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
        newHouse.addInhabitant(p1);
        newHouse.addInhabitant(p2);
        household.welcomeNeighbours(newHouse);
        int expSize = 2;
        assertEquals("Unexpected household size", expSize, household.getVisitors().size());

    }
    
    @Test
    public void testSendNeighboursHome() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/test_params.json");
        PopulationParameters.get().setVisitorLeaveRate(1.0);
        Household newHouse = new Household(Household.HouseholdType.ADULT);
        Person p1 = new Adult();
        newHouse.addInhabitant(p1);
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