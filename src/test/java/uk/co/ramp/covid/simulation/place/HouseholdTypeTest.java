package uk.co.ramp.covid.simulation.place;

import org.junit.Test;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

public class HouseholdTypeTest extends SimulationTest {

    @Test
    public void testAdultPensioner() {
        Household household = new AdultPensioner(null);
        assertTrue(household.adultRequired());
        assertTrue(household.pensionerRequired());
        assertFalse(household.childRequired());

        household.addAdult(new Adult(50, FEMALE));
        household.addPensioner(new Pensioner(66, MALE));

        assertFalse(household.adultRequired());
        assertFalse(household.pensionerRequired());
    }

    @Test
    public void testDoubleOlder() {
        Household household = new DoubleOlder(null);
        assertFalse(household.adultRequired());
        assertTrue(household.pensionerRequired());
        assertFalse(household.childRequired());

        household.addPensioner(new Pensioner(70, FEMALE));
        assertTrue(household.pensionerRequired());
        assertFalse(household.additionalPensionersAllowed());
        assertFalse(household.additionalAdultAnyAgeAllowed());
        household.addPensioner(new Pensioner(66, MALE));

        assertFalse(household.adultRequired());
        assertFalse(household.pensionerRequired());
        assertFalse(household.additionalPensionersAllowed());
        assertFalse(household.additionalChildrenAllowed());
        assertFalse(household.additionalAdultAnyAgeAllowed());
    }

    @Test
    public void testLargeAdult() {
        Household household = new LargeAdult(null);
        assertTrue(household.adultAnyAgeRequired());
        assertFalse(household.pensionerRequired());
        assertFalse(household.childRequired());

        household.addAdult(new Adult(30, FEMALE));
        household.addAdult(new Adult(50, FEMALE));
        household.addPensioner(new Pensioner(66, MALE));

        assertFalse(household.adultAnyAgeRequired());
    }

    @Test
    public void testLargeManyAdult() {
        Household household = new LargeManyAdultFamily(null);
        assertTrue(household.adultAnyAgeRequired());
        assertTrue(household.pensionerRequired());
        assertTrue(household.childRequired());

        household.addAdult(new Adult(30, FEMALE));
        household.addAdult(new Adult(50, FEMALE));
        household.addPensioner(new Pensioner(66, MALE));
        household.addChildOrInfant(new Infant(1, MALE));

        assertFalse(household.adultAnyAgeRequired());
        assertFalse(household.childRequired());
        assertTrue(household.additionalAdultAnyAgeAllowed());
        assertTrue(household.additionalChildrenAllowed());
        assertTrue(household.additionalPensionersAllowed());
    }

    @Test
    public void testLargeTwoAdultFamily() {
        Household household = new LargeTwoAdultFamily(null);
        assertTrue(household.adultAnyAgeRequired());
        assertTrue(household.childRequired());
        assertFalse(household.pensionerRequired());
        assertFalse(household.additionalAdultsAllowed());

        household.addPensioner(new Pensioner(65, FEMALE));
        household.addAdult(new Adult(64, MALE));
        household.addChildOrInfant(new Infant(1, MALE));
        household.addChildOrInfant(new Child(10, FEMALE));
        household.addChildOrInfant(new Child(17, MALE));

        assertFalse(household.adultAnyAgeRequired());
        assertFalse(household.childRequired());
        assertFalse(household.additionalAdultAnyAgeAllowed());
        assertFalse(household.additionalPensionersAllowed());
        assertTrue(household.additionalChildrenAllowed());
    }

    @Test
    public void testSingleAdult() {
        Household household = new SingleAdult(null);
        assertTrue(household.adultRequired());
        assertFalse(household.pensionerRequired());
        assertFalse(household.childRequired());
        assertFalse(household.additionalAdultAnyAgeAllowed());

        household.addAdult(new Adult(30, FEMALE));

        assertFalse(household.adultRequired());
    }

    @Test
    public void testSingleOlder() {
        Household household = new SingleOlder(null);
        assertFalse(household.adultRequired());
        assertTrue(household.pensionerRequired());
        assertFalse(household.childRequired());
        assertFalse(household.additionalAdultAnyAgeAllowed());

        household.addPensioner(new Pensioner(80, FEMALE));

        assertFalse(household.pensionerRequired());
    }

    @Test
    public void testSingleParent() {
        Household household = new SingleParent(null);
        assertTrue(household.adultAnyAgeRequired());
        assertTrue(household.childRequired());
        assertFalse(household.pensionerRequired());

        household.addAdult(new Adult(34, MALE));
        household.addChildOrInfant(new Infant(4, MALE));
        household.addChildOrInfant(new Child(5, FEMALE));
        household.addChildOrInfant(new Child(15, MALE));

        assertFalse(household.adultAnyAgeRequired());
        assertFalse(household.childRequired());
        assertFalse(household.additionalAdultAnyAgeAllowed());
        assertFalse(household.additionalPensionersAllowed());
        assertTrue(household.additionalChildrenAllowed());
    }

    @Test
    public void testSmallAdult() {
        Household household = new SmallAdult(null);
        assertTrue(household.adultRequired());
        assertFalse(household.adultAnyAgeRequired());
        assertFalse(household.childRequired());
        assertFalse(household.pensionerRequired());
        assertFalse(household.additionalAdultAnyAgeAllowed());
        assertFalse(household.additionalPensionersAllowed());
        assertFalse(household.additionalChildrenAllowed());

        household.addAdult(new Adult(34, MALE));
        assertTrue(household.adultRequired());
        household.addAdult(new Adult(30, FEMALE));

        assertFalse(household.adultRequired());
    }

    @Test
    public void testSmallFamily() {
        Household household = new SmallFamily(null);
        assertTrue(household.adultAnyAgeRequired());
        assertTrue(household.childRequired());
        assertFalse(household.adultRequired());
        assertFalse(household.pensionerRequired());
        assertFalse(household.additionalAdultsAllowed());
        assertFalse(household.additionalAdultAnyAgeAllowed());

        household.addAdult(new Adult(34, MALE));
        assertTrue(household.adultAnyAgeRequired());

        household.addAdult(new Adult(32, FEMALE));
        household.addChildOrInfant(new Infant(1, MALE));
        assertFalse(household.childRequired());
        assertTrue(household.additionalChildrenAllowed());

        household.addChildOrInfant(new Child(10, FEMALE));

        assertFalse(household.adultAnyAgeRequired());
        assertFalse(household.additionalChildrenAllowed());
    }
}
