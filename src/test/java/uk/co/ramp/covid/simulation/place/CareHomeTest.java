package uk.co.ramp.covid.simulation.place;

import org.junit.Test;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.ImpossibleWorkerDistributionException;
import uk.co.ramp.covid.simulation.population.Pensioner;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.testutil.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

import static org.junit.Assert.*;

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
    public void somePeopleDieInCareHomes() {
        int populationSize = 10000;
        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.8);
        CovidParameters.get().diseaseParameters.pensionerProgressionPhase2 = 100.0;
        CovidParameters.get().diseaseParameters.mortalityRate = 100.0;
        Model m = new Model()
                .setPopulationSize(populationSize)
                .setnInitialInfections(500)
                .setExternalInfectionDays(0)
                .setIters(1)
                .setnDays(60)
                .setRNGSeed(42)
                .setNoOutput();
        
        List<List<DailyStats>> stats = m.run(0);
        
        int careDeaths = 0;
        for (DailyStats s : stats.get(0)) {
            careDeaths += s.getCareHomeDeaths();
        }
        assertTrue(careDeaths > 0);
    }

    @Test
    public void sickResidentsAreQuarantined() {
        int populationSize = 10000;
        PopulationParameters.get().pensionerProperties.pEntersCareHome = new Probability(0.8);

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
        inf.infect();
        
        // Check we adjust transmission
        for (Person p : home.getPeople()) {
            if (p == inf) { continue; }
            if (p.isInCare()) {
                assertEquals(0.0, home.getTransP(inf, p), 0.001);
            } else {
                // PPE Adjustment works
                assertTrue(home.getTransP(inf, null) > home.getTransP(inf, p));
            }
        }
    }

}