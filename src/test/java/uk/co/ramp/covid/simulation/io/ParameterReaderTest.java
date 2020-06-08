package uk.co.ramp.covid.simulation.io;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.parameters.CovidParameters;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.util.Probability;

import java.io.IOException;

import static org.junit.Assert.*;

public class ParameterReaderTest {

    private final static double EPSILON = 0.000001;

    @Test
    public void readParametersFromFile() throws IOException {
        // Note the parameters in the test file are sufficiently different from the defaults to allow testing
        // and do not necessarily represent real world parameters
        ParameterReader.readParametersFromFile("src/test/resources/test_params.json");

        // Disease Parameters (Tests CovidParameters as a side effect)
        assertEquals(123.12, CovidParameters.get().diseaseParameters.meanLatentPeriod, EPSILON);
        assertEquals(192.0, CovidParameters.get().diseaseParameters.meanAsymptomaticPeriod, EPSILON);
        assertEquals(100.0, CovidParameters.get().diseaseParameters.mortalityRate, EPSILON);
        assertEquals(0.0, CovidParameters.get().diseaseParameters.childProgressionPhase2, EPSILON);
        assertEquals(0.15, CovidParameters.get().diseaseParameters.adultProgressionPhase2, EPSILON);
        assertEquals(1.0, CovidParameters.get().diseaseParameters.pensionerProgressionPhase2, EPSILON);

        // Test Parameters
        assertEqualsP(0.9, CovidParameters.get().testParameters.diagnosticTestSensitivity, EPSILON);

        // Population Parameters
        assertEquals(42, PopulationParameters.get().population.size());

        assertEquals(10, PopulationParameters.get().households.householdTypeDistribution().toList().size());

        assertEquals(1000, (int) PopulationParameters.get().buildingDistribution.hospitals);
        assertEquals(500, (int) PopulationParameters.get().buildingDistribution.schools);
        assertEquals(200, (int) PopulationParameters.get().buildingDistribution.shops);
        assertEquals(2000, (int) PopulationParameters.get().buildingDistribution.constructionSites);
        assertEquals(100, (int) PopulationParameters.get().buildingDistribution.offices);
        assertEquals(3000, (int) PopulationParameters.get().buildingDistribution.nurseries);
        assertEquals(800, (int) PopulationParameters.get().buildingDistribution.restaurants);

        assertEqualsP(0.1, PopulationParameters.get().workerAllocation.pOffice, EPSILON);
        assertEqualsP(0.2, PopulationParameters.get().workerAllocation.pShop, EPSILON);
        assertEqualsP(0.2, PopulationParameters.get().workerAllocation.pHospital, EPSILON);
        assertEqualsP(0.1, PopulationParameters.get().workerAllocation.pConstruction, EPSILON);
        assertEqualsP(0.2, PopulationParameters.get().workerAllocation.pRestaurant, EPSILON);
        assertEqualsP(0.1, PopulationParameters.get().workerAllocation.pUnemployed, EPSILON);

        assertEquals(0.8, PopulationParameters.get().buildingProperties.pBaseTrans, EPSILON);
        assertEquals(0.2, PopulationParameters.get().buildingProperties.pHospitalTrans, EPSILON);
        assertEquals(0.3, PopulationParameters.get().buildingProperties.pConstructionSiteTrans, EPSILON);
        assertEquals(0.001, PopulationParameters.get().buildingProperties.pNurseryTrans, EPSILON);
        assertEquals(0.6, PopulationParameters.get().buildingProperties.pOfficeTrans, EPSILON);
        assertEquals(0.1, PopulationParameters.get().buildingProperties.pRestaurantTrans, EPSILON);
        assertEquals(0.05, PopulationParameters.get().buildingProperties.pSchoolTrans, EPSILON);
        assertEquals(0.2, PopulationParameters.get().buildingProperties.pShopTrans, EPSILON);

        assertEqualsP(0.1, PopulationParameters.get().buildingProperties.pHospitalKey, EPSILON);
        assertEqualsP(0.6, PopulationParameters.get().buildingProperties.pConstructionSiteKey, EPSILON);
        assertEqualsP(0.2, PopulationParameters.get().buildingProperties.pShopKey, EPSILON);
        assertEqualsP(0.9, PopulationParameters.get().buildingProperties.pOfficeKey, EPSILON);

        assertEqualsP(0.8, PopulationParameters.get().infantAllocation.pAttendsNursery, EPSILON);

        assertEquals(0.05, PopulationParameters.get().householdProperties.neighbourVisitFreq, EPSILON);
        assertEquals(2, (int) PopulationParameters.get().householdProperties.expectedNeighbours);

        assertEqualsP(0.9, PopulationParameters.get().personProperties.pTransmission, EPSILON);
        assertEqualsP(0.7, PopulationParameters.get().personProperties.pQuarantine, EPSILON);

        assertEquals(0.8, PopulationParameters.get().householdProperties.visitorLeaveRate, EPSILON);

        assertEqualsP(0.25, PopulationParameters.get().buildingDistribution.officeSizes.pSmall, EPSILON);
        assertEqualsP(0.4, PopulationParameters.get().buildingDistribution.officeSizes.pMed, EPSILON);
        assertEqualsP(0.35, PopulationParameters.get().buildingDistribution.officeSizes.pLarge, EPSILON);

        assertEqualsP(0.4, PopulationParameters.get().buildingProperties.pLeaveRestaurant, EPSILON);
        assertEqualsP(0.5, PopulationParameters.get().buildingProperties.pLeaveShop, EPSILON);
        assertEqualsP(0.01786, PopulationParameters.get().householdProperties.pGoShopping, EPSILON);
        assertEqualsP(0.01190, PopulationParameters.get().householdProperties.pGoRestaurant, EPSILON);
    }

    private void assertEqualsP(double v, Probability p, double epsilon) {
        assertEquals(v, p.asDouble(), epsilon);
    }

    @After
    public void clearParams() {
        CovidParameters.clearParameters();
        PopulationParameters.clearParameters();
    }
}
