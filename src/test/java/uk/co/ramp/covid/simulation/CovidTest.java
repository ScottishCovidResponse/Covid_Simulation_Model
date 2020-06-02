package uk.co.ramp.covid.simulation;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CovidTest {

    //Test that a pensioner steps through the infection from latent to death
    @Test
    public void testStepInfectionSymptomatic() throws IOException {
        //Use the default parameters with a mortality rate of 100
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        CovidParameters.get().setMortalityRate(100.0);
        CovidParameters.get().setSymptomProbability(100.0);
        CovidParameters.get().setPensionerProgressionPhase2(100.0);

        CStatus cStatus = null;
        Person pensioner = new Pensioner(65, Person.Sex.MALE);
        Household h = new Household(Household.HouseholdType.PENSIONER, null);
        pensioner.setHome(h);
        Covid virus = new Covid(pensioner);
        virus.forceSymptomatic(true);
        //Test that the person is latent at the end of the latent period
        double latentPeriod = virus.getLatentPeriod() - 1;
        for (int i = 0; i < latentPeriod; i++) {
        	cStatus = virus.stepInfection();
        }
        assertEquals(CStatus.LATENT, cStatus);

        //Test that the person becomes phase1 after the latent period
        cStatus = virus.stepInfection();
        assertEquals(CStatus.PHASE1, cStatus);

        //Test that the person becomes dead after the phase2 period
        double p1Period = virus.getP1();
        double p2Period = virus.getP2();
        for (int i = 0; i < p1Period + p2Period; i++) {
            if(!virus.isDead()) cStatus = virus.stepInfection();
        }

        assertEquals(CStatus.DEAD, cStatus);
    }

    //Test that a child steps through the infection from latent to recovered
    @Test
    public void testStepInfectionRecover() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        CStatus cStatus = null;
        Person child = new Child(6, Person.Sex.FEMALE);
        Household h = new Household(Household.HouseholdType.ADULTCHILD, null);
        child.setHome(h);
        Covid virus = new Covid(child);
        virus.forceSymptomatic(true);

        double latentPeriod = virus.getLatentPeriod();
        double p1Period = virus.getP1();

        //Test that the person becomes recovered after the total infection period
        for (int i = 0; i < latentPeriod + p1Period; i++) {
            cStatus = virus.stepInfection();
        }
      //  cStatus = virus.stepInfection();
        assertEquals(CStatus.RECOVERED, cStatus);
        assertFalse(virus.getIsSymptomatic());
    }

    //Test that a child steps through the infection from Asymtomatic to recovered
    @Test
    public void testStepInfectionAsymptomatic() throws IOException {
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        CovidParameters.get().setSymptomProbability(0.0);
        Person child = new Child(5, Person.Sex.FEMALE);
        Covid virus = new Covid(child);
        virus.forceSymptomatic(false);

        double latentPeriod = virus.getLatentPeriod();
        double asymptomaticPeriod = virus.getAsymptomaticPeriod();

        CStatus cStatus = virus.stepInfection();
        
        //Test that the person is asymptomatic after the total infection period
        for (int i = 0; i < latentPeriod; i++) {
            cStatus = virus.stepInfection();
        }
        assertEquals(CStatus.ASYMPTOMATIC, cStatus);
        
        for (int i = 0; i < asymptomaticPeriod; i++) {
        	cStatus = virus.stepInfection();
        }
        
        assertEquals(CStatus.RECOVERED, cStatus);
        assertFalse(virus.getIsSymptomatic());
    }

    @After
    public void clearParams() {
        CovidParameters.clearParameters();
        PopulationParameters.clearParameters();
    }
}
