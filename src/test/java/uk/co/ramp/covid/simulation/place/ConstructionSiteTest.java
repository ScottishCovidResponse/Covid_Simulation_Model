package uk.co.ramp.covid.simulation.place;

import org.junit.Test;

import com.google.gson.JsonParseException;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.*;
import uk.co.ramp.covid.simulation.util.PopulationGenerator;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static uk.co.ramp.covid.simulation.population.Person.Sex.FEMALE;
import static uk.co.ramp.covid.simulation.population.Person.Sex.MALE;

public class ConstructionSiteTest extends SimulationTest {

    @Test
    public void testConstructionSiteTransProb() throws JsonParseException {
        ConstructionSite constructionSite = new ConstructionSite(CommunalPlace.Size.MED);
        double expProb = PopulationParameters.get().buildingProperties.baseTransmissionConstant;
        double delta = 0.01;
        assertEquals("Unexpected construction site TransProb", expProb, constructionSite.transConstant, delta);
    }

    @Test
    public void testNoConstructionSites() throws JsonParseException {
        //The input ConstructionSites ratio is set very high so that there are no construction sites.
        //Check that each person's primary place is never set to construction site
        PopulationParameters.get().buildingDistribution.populationToConstructionSitesRatio = 100000;
        Population p = PopulationGenerator.genValidPopulation(10000);

        ArrayList<Person> allPeople = p.getAllPeople();
        for (Person allPerson : allPeople) {
            allPerson.allocateCommunalPlace(p.getPlaces());
            assertFalse("Primary communal place cannot be a construction site", allPerson.getPrimaryCommunalPlace() instanceof ConstructionSite);
        }
    }

    @Test
    public void testNoCSInfections() throws JsonParseException {
        //The input ConstructionSites ratio is set very high so that there are no construction sites.
        //Check that there are no infections on construction sites
        PopulationParameters.get().buildingDistribution.populationToConstructionSitesRatio = 100000;
        int population = 10000;
        int nInfections = 100;

        Model m = new Model()
                .setPopulationSize(population)
                .setnInitialInfections(nInfections)
                .setExternalInfectionDays(0)
                .setIters(1)
                .setnDays(20)
                .setRNGSeed(42)
                .setNoOutput();

        List<List<DailyStats>> stats = m.run(0);

        for (DailyStats s : stats.get(0)) {
            assertEquals("Unexpected construction site infections", 0, s.constructionSiteInfectionsWorker.get());
        }
    }

    @Test
    public void testConstructionSiteWorkers() throws ImpossibleWorkerDistributionException {
        int populationSize = 10000;
        
        Population p = PopulationGenerator.genValidPopulation(populationSize);
        p.allocatePeople();
        
        p.setPostHourHook((pop, time) -> {
            int totStaff = 0;
            for (ConstructionSite place : pop.getPlaces().getConstructionSites()) {
                List<Person> staff = place.getStaff(time);
                totStaff += staff.size();
            }

            if (time.getDay() < 5) {
                int startTime = Shifts.nineFiveFiveDays().getShift(time.getDay()).getStart();
                int endTime = Shifts.nineFiveFiveDays().getShift(time.getDay()).getEnd();

                //Staff should be at construction sites during working hours only
                if (time.getHour() < startTime || time.getHour() >= endTime) {
                    assertEquals("Unexpected staff at construction site", 0, totStaff);
                } else {
                    assertTrue("Unexpectedly no staff at construction site", totStaff > 0);
                }
            } else {
                //Staff should not be at construction sites on weekends
                assertEquals("Unexpected staff at construction site", 0, totStaff);
            }
        });
        
        p.simulate(7);
    }

    @Test
    public void testSendHome() {
        ConstructionSite cs = new ConstructionSite(CommunalPlace.Size.MED);
        cs.addPerson(new Adult(30, FEMALE));
        cs.addPerson(new Adult(35, MALE));
        int time = cs.getOpeningTimes().getClose() - 1;
        cs.determineMovement(new Time(time), new DailyStats(new Time(time)),  null);
        cs.commitMovement();
        int expPeople = 0;
        assertEquals("Unexpected people left on construction site", expPeople, cs.getNumPeople());
    }

}
