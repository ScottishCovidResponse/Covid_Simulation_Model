package uk.co.ramp.covid.simulation.place;

import org.junit.Test;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Pensioner;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import static org.junit.Assert.*;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

public class CareHomeTest extends SimulationTest {

    @Test
    public void somePensionersInCare() {
        int populationSize = 10000;
        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.8);
        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        
        int inCare = 0;
        for (CareHome h : pop.getPlaces().getCareHomes()) {
            for (Person p : h.getPeople()) {
                if (p.isInCare()) {
                    inCare++;
                }
            }
        }
        assertNotEquals(0, inCare);
    }

    @Test
    public void noPensionersInCare() {
        int populationSize = 10000;
        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.0);
        Population pop = PopulationGenerator.genValidPopulation(populationSize);

        int inCare = 0;
        for (CareHome h : pop.getPlaces().getCareHomes()) {
            for (Person p : h.getPeople()) {
                if (p.isInCare()) {
                    inCare++;
                }
            }
        }
        assertEquals(0, inCare);
    }

    @Test
    public void sickResidentsAreQuarantined() {
        int populationSize = 10000;
        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.8);
        // There can be lots of people in the care home so we crank this up to avoid getting almost 0 transmission probs
        PopulationParameters.get().buildingProperties.careHomeTransmissionConstant = 100.0;
        CovidParameters.get().diseaseParameters.pSymptomaticCasePensioner = new Probability(1.0);
        CovidParameters.get().diseaseParameters.pensionerProgressionPhase2 = 100.0;
        // Makes it less likely we get symptoms before we are infectious
        CovidParameters.get().diseaseParameters.meanSymptomDelay = 0.1;
        CovidParameters.get().diseaseParameters.meanLatentPeriod = 0.5;

        Population pop = PopulationGenerator.genValidPopulation(populationSize);
        pop.seedVirus(100);

        // Find a pensioner to infect
        Pensioner inf = null;
        CareHome home = null;
        for (CareHome h : pop.getPlaces().getCareHomes()) {
            if (inf != null) {
                break;
            }
            for (Person p : h.getPeople()) {
                if (p instanceof Pensioner) {
                    inf = (Pensioner) p;
                    home = h;
                    break;
                }
            }
        }
        assert inf != null;
        inf.infect();
        inf.getcVirus().forceSymptomatic(true);
        
        Time t = new Time(0);
        for (int i = 0; i <= inf.getcVirus().getSymptomDelay(); i++) {
            inf.getcVirus().stepInfection(t);
            t = t.advance();
        }

        // Not quarantined instantly
        for (Person p : home.getPeople()) {
            if (p == inf) { continue; }
            assertTrue(home.getTransP(t, inf, p) > 0);
        }

        // Step till quarantine
        for (int i = 0; i <= CovidParameters.get().careHomeParameters.hoursAfterSymptomsBeforeQuarantine; i++) {
            inf.getcVirus().stepInfection(t);
            t = t.advance();
        }

        // Quarantined
        for (Person p : home.getPeople()) {
            if (p == inf) {
                continue;
            }
            if (p.isInCare()) {
                assertEquals(0.0, home.getTransP(t, inf, p), 0.001);
            } else {
                // PPE Adjustment
                assertTrue(home.getTransP(t, inf, null) > home.getTransP(new Time(0), inf, p));
            }
        }
    }

    //Test that people do not leave care homes
    @Test
    public void testSendHome() {
        Time time = new Time(0);
        CareHome ch = new CareHome(CommunalPlace.Size.MED);
        ch.addPerson(new Pensioner(80, FEMALE));
        ch.addPerson(new Pensioner(85, MALE));
        ch.determineMovement(time,  false, null);
        ch.commitMovement();
        int expPeople = 2;
        assertEquals("People Unexpectedly left the care home", expPeople, ch.getNumPeople());
    }

}