package uk.co.ramp.covid.simulation.population;

import org.junit.Before;
import org.junit.Test;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PlacesTest extends SimulationTest {

    private Places p;
    private int population = 20000;
    private int iters = 2000; // samples to take for random testing
    private int n = 200;
    private double DELTA = 0.05;
    
    @Before
    public void createPlaces() {
        // Force it so we expect a fixed number of places of each type
        PopulationParameters.get().buildingDistribution.populationToHospitalsRatio = population / n;
        PopulationParameters.get().buildingDistribution.populationToShopsRatio = population / n;
        PopulationParameters.get().buildingDistribution.populationToSchoolsRatio = population / n;
        PopulationParameters.get().buildingDistribution.populationToRestaurantsRatio = population / n;
        PopulationParameters.get().buildingDistribution.populationToOfficesRatio = population / n;
        PopulationParameters.get().buildingDistribution.populationToConstructionSitesRatio = population / n;
        PopulationParameters.get().buildingDistribution.populationToCareHomesRatio = population / n;
        PopulationParameters.get().buildingDistribution.populationToNurseriesRatio = population / n;
        
        p = new Places(population, 0);
    }

    @Test
    public void getRandomOffice() {
        List<Office> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomOffice());
        }

        int s = 0, m = 0, l = 0;
        for (Office o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We should sample each office size at least once", s > 0 && m > 0 && l > 0);
        assertTrue("We should sample large offices more often than small", l > s);
        assertTrue("We should sample large offices more often than medium", l > m);
        assertTrue("We should sample medium offices more often than small", m > s);

        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pLarge.asDouble(),  (double) l/ (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pMed.asDouble(), (double) m / (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pSmall.asDouble(), (double) s / (double) iters, DELTA);
    }

    @Test
    public void getRandomConstructionSite() {
        List<ConstructionSite> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomConstructionSite());
        }

        int s = 0, m = 0, l = 0;
        for (ConstructionSite o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We should sample each constructionSite size at least once", s > 0 && m > 0 && l > 0);
        assertTrue("We should sample large constructionSites more often than small", l > s);
        assertTrue("We should sample large constructionSites more often than medium", l > m);
        assertTrue("We should sample medium constructionSites more often than small", m > s);

       
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pLarge.asDouble(),  (double) l/ (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pMed.asDouble(), (double) m / (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pSmall.asDouble(), (double) s / (double) iters, DELTA);
    }

    @Test
    public void getRandomHospital() {
        List<Hospital> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomHospital());
        }

        int s = 0, m = 0, l = 0;
        for (Hospital o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We only have medium hospitals", s == 0 && m > 0 && l == 0);
        assertEquals(iters, m);
    }

    @Test
    public void getRandomNursery() {
        List<Nursery> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomNursery());
        }

        int s = 0, m = 0, l = 0;
        for (Nursery o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We only have medium nurseries", s == 0 && m > 0 && l == 0);
        assertEquals(iters, m);
    }

    @Test
    public void getRandomRestaurant() {
        List<Restaurant> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomRestaurant());
        }

        int s = 0, m = 0, l = 0;
        for (Restaurant o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We should sample each restaurant size at least once", s > 0 && m > 0 && l > 0);
        assertTrue("We should sample large restaurants more often than small", l > s);
        assertTrue("We should sample large restaurants more often than medium", l > m);
        assertTrue("We should sample medium restaurants more often than small", m > s);

        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pLarge.asDouble(),  (double) l/ (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pMed.asDouble(), (double) m / (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pSmall.asDouble(), (double) s / (double) iters, DELTA);
    }

    @Test
    public void getRandomSchool() {
        List<School> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomSchool());
        }

        int s = 0, m = 0, l = 0;
        for (School o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We only have medium school size", s == 0 && m > 0 && l == 0);
        assertEquals(iters, m);
    }

    @Test
    public void getRandomShop() {
        List<Shop> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomShop());
        }

        int s = 0, m = 0, l = 0;
        for (Shop o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We should sample each shop size at least once", s > 0 && m > 0 && l > 0);
        assertTrue("We should sample large shops more often than small", l > s);
        assertTrue("We should sample large shops more often than medium", l > m);
        assertTrue("We should sample medium shops more often than small", m > s);

        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pLarge.asDouble(),  (double) l/ (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pMed.asDouble(), (double) m / (double) iters, DELTA);
        assertEquals(PopulationParameters.get().workerDistribution.sizeAllocation.pSmall.asDouble(), (double) s / (double) iters, DELTA);
    }

    @Test
    public void getRandomCareHome() {
        List<CareHome> samples = new ArrayList<>();
        for (int i = 0; i < iters ; i++) {
            samples.add(p.getRandomCareHome());
        }

        int s = 0, m = 0, l = 0;
        for (CareHome o : samples) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertTrue("We only have medium careHomes", s == 0 && m > 0 && l == 0);
        assertEquals(iters, m);
    }

    @Test
    public void createNOffices() {
        int s = 0, m = 0, l = 0;
        for (Office o : p.getOffices()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        // Test params = pSmall: 0.2, pMed: 0.3, pLarge:0.5
        assertEquals(n, s + m + l);
        assertEquals(n * PopulationParameters.get().buildingDistribution.officeSizeDistribution.pSmall.asDouble(), s, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.officeSizeDistribution.pMed.asDouble(), m, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.officeSizeDistribution.pLarge.asDouble(), l, n*0.05);
    }

    @Test
    public void createNHospitals() {
        int s = 0, m = 0, l = 0;
        for (Hospital o : p.getAllHospitals()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        // Test params = pSmall: 0.2, pMed: 0.3, pLarge:0.5
        assertEquals(n, s + m + l);
        assertEquals(n * PopulationParameters.get().buildingDistribution.hospitalSizeDistribution.pSmall.asDouble(), s, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.hospitalSizeDistribution.pMed.asDouble(), m, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.hospitalSizeDistribution.pLarge.asDouble(), l, n*0.05);
    }

    @Test
    public void createNSchools() {
        int s = 0, m = 0, l = 0;
        for (School o : p.getSchools()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        // Test params = pSmall: 0.2, pMed: 0.3, pLarge:0.5
        assertEquals(n, s + m + l);
        assertEquals(n * PopulationParameters.get().buildingDistribution.schoolSizeDistribution.pSmall.asDouble(), s, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.schoolSizeDistribution.pMed.asDouble(), m, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.schoolSizeDistribution.pLarge.asDouble(), l, n*0.05);
    }

    @Test
    public void createNNurseries() {
        int s = 0, m = 0, l = 0;
        for (Nursery o : p.getNurseries()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        // Test params = pSmall: 0.2, pMed: 0.3, pLarge:0.5
        assertEquals(n, s + m + l);
        assertEquals(n * PopulationParameters.get().buildingDistribution.nurserySizeDistribution.pSmall.asDouble(), s, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.nurserySizeDistribution.pMed.asDouble(), m, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.nurserySizeDistribution.pLarge.asDouble(), l, n*0.05);
    }

    @Test
    public void createNRestaurants() {
        int s = 0, m = 0, l = 0;
        for (Restaurant o : p.getRestaurants()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        assertEquals(n, s + m + l);
        assertEquals(n * PopulationParameters.get().buildingDistribution.restaurantSizeDistribution.pSmall.asDouble(), s, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.restaurantSizeDistribution.pMed.asDouble(), m, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.restaurantSizeDistribution.pLarge.asDouble(), l, n*0.05);
    }

    @Test
    public void createNShops() {
        int s = 0, m = 0, l = 0;
        for (Shop o : p.getShops()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        // Test params = pSmall: 0.2, pMed: 0.3, pLarge:0.5
        assertEquals(n, s + m + l);
        assertEquals(n * PopulationParameters.get().buildingDistribution.shopSizeDistribution.pSmall.asDouble(), s, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.shopSizeDistribution.pMed.asDouble(), m, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.shopSizeDistribution.pLarge.asDouble(), l, n*0.05);
    }

    @Test
    public void createNConstructionSites() {
        int s = 0, m = 0, l = 0;
        for (ConstructionSite o : p.getConstructionSites()) {
            switch (o.getSize()) {
                case SMALL: s++; break;
                case MED: m++; break;
                case LARGE: l++; break;
            }
        }

        // Test params = pSmall: 0.2, pMed: 0.3, pLarge:0.5
        assertEquals(n, s + m + l);
        assertEquals(n * PopulationParameters.get().buildingDistribution.constructionSiteSizeDistribution.pSmall.asDouble(), s, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.constructionSiteSizeDistribution.pMed.asDouble(), m, n*0.05);
        assertEquals(n * PopulationParameters.get().buildingDistribution.constructionSiteSizeDistribution.pLarge.asDouble(), l, n*0.05);
    }

    @Test
    public void getAllPlaces() {
        assertEquals(n*8, p.getCommunalPlaces().size());
        assertTrue(p.getCommunalPlaces().containsAll(p.getOffices()));
        assertTrue(p.getCommunalPlaces().containsAll(p.getConstructionSites()));
        assertTrue(p.getCommunalPlaces().containsAll(p.getAllHospitals()));
        assertTrue(p.getCommunalPlaces().containsAll(p.getNurseries()));
        assertTrue(p.getCommunalPlaces().containsAll(p.getRestaurants()));
        assertTrue(p.getCommunalPlaces().containsAll(p.getSchools()));
        assertTrue(p.getCommunalPlaces().containsAll(p.getShops()));
        assertTrue(p.getCommunalPlaces().containsAll(p.getCareHomes()));
    }

    @Test
    public void getOffices() {
        List<Office> offices = p.getOffices();
        assertEquals(n, offices.size());
        assertTrue(p.getCommunalPlaces().containsAll(offices));
    }

    @Test
    public void getConstructionSites() {
        List<ConstructionSite> cs = p.getConstructionSites();
        assertEquals(n, cs.size());
        assertTrue(p.getCommunalPlaces().containsAll(cs));
    }

    @Test
    public void getHospitals() {
        List<Hospital> hs = p.getAllHospitals();
        assertEquals(n, hs.size());
        assertTrue(p.getCommunalPlaces().containsAll(hs));
    }

    @Test
    public void getNurseries() {
        List<Nursery> ns = p.getNurseries();
        assertEquals(n, ns.size());
        assertTrue(p.getCommunalPlaces().containsAll(ns));
    }

    @Test
    public void getRestaurants() {
        List<Restaurant> rs = p.getRestaurants();
        assertEquals(n, rs.size());
        assertTrue(p.getCommunalPlaces().containsAll(rs));
    }

    @Test
    public void getSchools() {
        List<School> ss = p.getSchools();
        assertEquals(n, ss.size());
        assertTrue(p.getCommunalPlaces().containsAll(ss));
    }

    @Test
    public void getShops() {
        List<Shop> ss = p.getShops();
        assertEquals(n, ss.size());
        assertTrue(p.getCommunalPlaces().containsAll(ss));
    }

    @Test
    public void getCareHomes() {
        List<CareHome> ss = p.getCareHomes();
        assertEquals(n, ss.size());
        assertTrue(p.getCommunalPlaces().containsAll(ss));
    }
}
