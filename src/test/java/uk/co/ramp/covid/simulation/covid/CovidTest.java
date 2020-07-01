package uk.co.ramp.covid.simulation.covid;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.householdtypes.SingleOlder;
import uk.co.ramp.covid.simulation.place.householdtypes.SmallFamily;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CovidTest extends SimulationTest {

    //Test that a pensioner steps through the infection from latent to death
    @Test
    public void testStepInfectionSymptomatic() {
        //Use the default parameters with a mortality rate of 100
        CovidParameters.get().diseaseParameters.caseMortalityRate = 1.0;
        CovidParameters.get().diseaseParameters.pSymptomaticCasePensioner = new Probability(1.0);
        CovidParameters.get().diseaseParameters.pensionerProgressionPhase2 = 100.0;

        CStatus cStatus = null;
        Person pensioner = new Pensioner(85, Person.Sex.MALE);
        Household h = new SingleOlder();
        pensioner.setHome(h);
        Covid virus = new Covid(pensioner);
        virus.forceSymptomatic(true);
        //Test that the person is latent at the end of the latent period
        double latentPeriod = virus.getLatentPeriod() - 1;
        Time t = new Time();
        for (int i = 0; i < latentPeriod; i++) {
        	cStatus = virus.stepInfection(t);
        	t.advance();
        }
        assertEquals(CStatus.LATENT, cStatus);

        //Test that the person becomes phase1 after the latent period
        cStatus = virus.stepInfection(t);
        assertEquals(CStatus.PHASE1, cStatus);

        //Test that the person becomes dead after the phase2 period
        double p1Period = virus.getP1();
        double p2Period = virus.getP2();
        for (int i = 0; i < latentPeriod + p1Period + p2Period-1; i++) {
            if(!virus.isDead()) cStatus = virus.stepInfection(t);
            t.advance();
        }
        assertTrue(virus.isSymptomatic());
        virus.stepInfection(t.advance());
        virus.stepInfection(t.advance());
        assertEquals(CStatus.DEAD, cStatus);
    }

    //Test that a child steps through the infection from latent to recovered
    @Test
    public void testStepInfectionRecover() {
        CStatus cStatus = null;
        Time t = new Time();
        Person child = new Child(6, Person.Sex.FEMALE);
        Household h = new SmallFamily();
        child.setHome(h);
        Covid virus = new Covid(child);
        virus.forceSymptomatic(true);


        double latentPeriod = virus.getLatentPeriod();
        double p1Period = virus.getP1();

        //Test that the person becomes recovered after the total infection period
        for (int i = 0; i < latentPeriod + p1Period; i++) {
            cStatus = virus.stepInfection(t);
        }
      //  cStatus = virus.stepInfection();
        assertEquals(CStatus.RECOVERED, cStatus);
        assertFalse(virus.isSymptomatic());
    }

    //Test that a child steps through the infection from Asymtomatic to recovered
    @Test
    public void testStepInfectionAsymptomatic() {
        CovidParameters.get().diseaseParameters.pSymptomaticCaseAdult = new Probability(0);
        Person child = new Child(5, Person.Sex.FEMALE);
        Time t = new Time();
        Covid virus = new Covid(child);
        virus.forceSymptomatic(false);

        double latentPeriod = virus.getLatentPeriod();
        double asymptomaticPeriod = virus.getAsymptomaticPeriod();

        CStatus cStatus = virus.stepInfection(t);
        
        //Test that the person is asymptomatic after the total infection period
        for (int i = 0; i < latentPeriod; i++) {
            cStatus = virus.stepInfection(t);
        }
        assertEquals(CStatus.ASYMPTOMATIC, cStatus);
        
        for (int i = 0; i < asymptomaticPeriod; i++) {
        	cStatus = virus.stepInfection(t);
        }
        
        assertEquals(CStatus.RECOVERED, cStatus);
        assertFalse(virus.isSymptomatic());
    }

    @Test
    public void testCovidStatus() throws ImpossibleWorkerDistributionException {
        int populationSize = 12000;
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.seedVirus(100);
        p.allocatePeople();
        
        p.setPostHourHook((pop, time) -> {
            // Check if every infected person's status is correct
            for (Person person : pop.getAllPeople()) {
                if (person.isinfected()) {
                    assertFalse(person.cStatus().equals(CStatus.HEALTHY) ||
                            person.cStatus().equals(CStatus.RECOVERED));
                }
            }
        } );
        
        p.simulateFromTime(new Time(0), 7);
    }



    @After
    public void clearParams() {
        CovidParameters.clearParameters();
        PopulationParameters.clearParameters();
    }
}
