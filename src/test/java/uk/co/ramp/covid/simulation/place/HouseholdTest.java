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
    int nneighbours = 3;

    @Before
    public void initialise() throws JsonParseException, IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        household = new Household(Household.HouseholdType.ADULT, null);
        Person p1 = new Adult(30, Person.Sex.MALE);
        Person p2 = new Adult(32, Person.Sex.FEMALE);
        Person p3 = new Adult(30, Person.Sex.MALE);
        household.addInhabitant(p1);
        household.addInhabitant(p2);
        household.addInhabitant(p3);
        household2 = new Household(Household.HouseholdType.ADULT, null);
        household3 = new Household(Household.HouseholdType.ADULT, null);
        household4 = new Household(Household.HouseholdType.ADULT, null);

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
        Person p4 = new Adult(40, Person.Sex.FEMALE);
        household.addInhabitant(p4);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getInhabitants().size());
    }

    @Test
    public void testGetHouseholdSize() {
        Person p1 = new Adult(50, Person.Sex.MALE);
        household.addInhabitant(p1);
        int expSize = 4;
        assertEquals("Unexpected household size", expSize, household.getHouseholdSize());
    }

    @Test
    public void testSeedInfection() {
        assertTrue("Infection not seeded", household.seedInfection());
    }

    @Test
    public void testSendNeighboursHome() {
        PopulationParameters.get().setHouseholdVisitorLeaveRate(1.0);
        Household h = new Household(Household.HouseholdType.ADULT, null);
        Person p1 = new Adult(22, Person.Sex.FEMALE);
        
        p1.setHome(household);
        h.addPersonNext(p1);
        h.stepPeople();

        int expSize = 1;
        assertEquals("Unexpected number of visitors", expSize, h.sendNeighboursHome(0,0));
    }

    @Test
    public void testGetInhabitants() {
        int expSize = 3;
        assertEquals("Unexpected number of inhabitants", expSize, household.getInhabitants().size());

    }
}
