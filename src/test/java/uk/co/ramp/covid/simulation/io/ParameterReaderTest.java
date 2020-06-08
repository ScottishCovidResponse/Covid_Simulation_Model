package uk.co.ramp.covid.simulation.io;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.covid.CovidParameters;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
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
        assertEquals(123.12, CovidParameters.get().getMeanLatentPeriod(), EPSILON);
        assertEquals(192.0, CovidParameters.get().getMeanAsymptomaticPeriod(), EPSILON);
        assertEquals(100.0, CovidParameters.get().getMortalityRate(), EPSILON);
        assertEquals(0.0, CovidParameters.get().getChildProgressionPhase2(), EPSILON);
        assertEquals(0.15, CovidParameters.get().getAdultProgressionPhase2(), EPSILON);
        assertEquals(1.0, CovidParameters.get().getPensionerProgressionPhase2(), EPSILON);

        // Test Parameters
        assertEquals(0.9, CovidParameters.get().getDiagnosticTestSensitivity(), EPSILON);

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

        assertEquals(0.1, PopulationParameters.get().workerAllocation.pOffice, EPSILON);
        assertEquals(0.2, PopulationParameters.get().workerAllocation.pShop, EPSILON);
        assertEquals(0.2, PopulationParameters.get().workerAllocation.pHospital, EPSILON);
        assertEquals(0.1, PopulationParameters.get().workerAllocation.pConstruction, EPSILON);
        assertEquals(0.2, PopulationParameters.get().workerAllocation.pRestaurant, EPSILON);
        assertEquals(0.1, PopulationParameters.get().workerAllocation.pUnemployed, EPSILON);

        assertEquals(0.8, PopulationParameters.get().buildingProperties.pBaseTrans, EPSILON);
        assertEquals(0.2, PopulationParameters.get().buildingProperties.pHospitalTrans, EPSILON);
        assertEquals(0.3, PopulationParameters.get().buildingProperties.pConstructionSiteTrans, EPSILON);
        assertEquals(0.001, PopulationParameters.get().buildingProperties.pNurseryTrans, EPSILON);
        assertEquals(0.6, PopulationParameters.get().buildingProperties.pOfficeTrans, EPSILON);
        assertEquals(0.1, PopulationParameters.get().buildingProperties.pRestaurantTrans, EPSILON);
        assertEquals(0.05, PopulationParameters.get().buildingProperties.pSchoolTrans, EPSILON);
        assertEquals(0.2, PopulationParameters.get().buildingProperties.pShopTrans, EPSILON);
        assertEquals(0.1, PopulationParameters.get().buildingProperties.pHospitalKey, EPSILON);
        assertEquals(0.6, PopulationParameters.get().buildingProperties.pConstructionSiteKey, EPSILON);
        assertEquals(0.2, PopulationParameters.get().buildingProperties.pShopKey, EPSILON);
        assertEquals(0.9, PopulationParameters.get().buildingProperties.pOfficeKey, EPSILON);

        assertEquals(0.8, PopulationParameters.get().infantAllocation.pAttendsNursery, EPSILON);

        assertEquals(0.05, PopulationParameters.get().householdProperties.neighbourVisitFreq, EPSILON);
        assertEquals(2, (int) PopulationParameters.get().householdProperties.expectedNeighbours);

        assertEquals(0.9, PopulationParameters.get().personProperties.pTransmission, EPSILON);
        assertEquals(0.7, PopulationParameters.get().personProperties.pQuarantine, EPSILON);

        assertEquals(0.8, PopulationParameters.get().householdProperties.visitorLeaveRate, EPSILON);

        assertEquals(0.25, PopulationParameters.get().buildingDistribution.officeSizes.pSmall, EPSILON);
        assertEquals(0.4, PopulationParameters.get().buildingDistribution.officeSizes.pMed, EPSILON);
        assertEquals(0.35, PopulationParameters.get().buildingDistribution.officeSizes.pLarge, EPSILON);

        assertEquals(0.4, PopulationParameters.get().buildingProperties.pLeaveRestaurant, EPSILON);
        assertEquals(0.5, PopulationParameters.get().buildingProperties.pLeaveShop, EPSILON);
        assertEquals(0.01786, PopulationParameters.get().householdProperties.pGoShopping, EPSILON);
        assertEquals(0.01190, PopulationParameters.get().householdProperties.pGoRestaurant, EPSILON);
    }

    @After
    public void clearParams() {
        CovidParameters.clearParameters();
        PopulationParameters.clearParameters();
    }
}
