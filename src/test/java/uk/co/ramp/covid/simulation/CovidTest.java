package uk.co.ramp.covid.simulation;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.io.ParameterReader;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.RNG;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CovidTest {

    //Test that a pensioner steps through the infection from latent to death
    @Test
    public void testStepInfection() throws IOException {
        RNG.seed(321);
        //Use the test file with a mortality rate of 100
        ParameterReader.readParametersFromFile("src/test/resources/test_params.json");
        CStatus cStatus = null;
        Person pensioner = new Pensioner();
        Covid virus = new Covid(pensioner);

        //Test that the person is latent at the end of the latent period
        int latentPeriod = virus.getLatentPeriod() * 24 - 1;
        for (int i = 0; i < latentPeriod; i++) {
            cStatus = virus.stepInfection();
        }
        assertEquals(CStatus.LATENT, cStatus);

        //Test that the person becomes asymptomatic in the next time period
        cStatus = virus.stepInfection();
        assertEquals(CStatus.ASYMPTOMATIC, cStatus);

        //Test that the person becomes phase1 after the asymptomatic period
        double asymptomaticPeriod = virus.getAsymptomaticPeriod() * 24;
        for (int i = 0; i < asymptomaticPeriod; i++) {
            cStatus = virus.stepInfection();
        }
        assertEquals(CStatus.PHASE1, cStatus);

        //Test that the person becomes dead after the phase1 period
        double p1Period = virus.getP1() * 24;
        for (int i = 0; i < p1Period; i++) {
            cStatus = virus.stepInfection();
        }
        assertEquals(CStatus.DEAD, cStatus);
    }

    //Test that a child steps through the infection from latent to recovered
    @Test
    public void testStepInfectionRecover() throws IOException {
        RNG.seed(321);
        ParameterReader.readParametersFromFile("src/test/resources/default_params.json");
        CStatus cStatus = null;
        Person child = new Child();
        Covid virus = new Covid(child);

        int latentPeriod = virus.getLatentPeriod() * 24;
        double asymptomaticPeriod = virus.getAsymptomaticPeriod() * 24;
        double p1Period = virus.getP1() * 24;

        //Test that the person becomes recovered after the total infection period
        for (int i = 0; i < latentPeriod + asymptomaticPeriod + p1Period; i++) {
            cStatus = virus.stepInfection();
        }
        assertEquals(CStatus.RECOVERED, cStatus);
    }


    @After
    public void clearParams() {
        CovidParameters.clearParameters();
        PopulationParameters.clearParameters();
    }
}