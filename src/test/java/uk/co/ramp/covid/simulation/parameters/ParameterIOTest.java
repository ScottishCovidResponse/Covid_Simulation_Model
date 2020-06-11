package uk.co.ramp.covid.simulation.parameters;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.util.Probability;

import java.io.IOException;

import static org.junit.Assert.*;

public class ParameterIOTest {

    private final static double EPSILON = 0.000001;

    @Test
    public void readParametersFromFile() throws IOException {
        // Note the parameters in the test file are sufficiently different from the defaults to allow testing
        // and do not necessarily represent real world parameters
        ParameterIO.readParametersFromFile("src/test/resources/test_params.json");

        // Disease Parameters (Tests CovidParameters as a side effect)
        assertEquals(123.12, CovidParameters.get().diseaseParameters.meanLatentPeriod, EPSILON);
        assertEquals(192.0, CovidParameters.get().diseaseParameters.meanAsymptomaticPeriod, EPSILON);
        assertEquals(100.0, CovidParameters.get().diseaseParameters.mortalityRate, EPSILON);
        assertEquals(0.0, CovidParameters.get().diseaseParameters.childProgressionPhase2, EPSILON);
        assertEquals(0.15, CovidParameters.get().diseaseParameters.adultProgressionPhase2, EPSILON);
        assertEquals(1.0, CovidParameters.get().diseaseParameters.pensionerProgressionPhase2, EPSILON);

        // Test Parameters
        assertEqualsP(0.9, CovidParameters.get().testParameters.pDiagnosticTestDetectsSuccessfully);

        // Population Parameters
        assertEquals(42, PopulationParameters.get().populationDistribution.size());

        assertEquals(10, PopulationParameters.get().householdDistribution.householdTypeDistribution().toList().size());

        assertEquals(1000, (int) PopulationParameters.get().buildingDistribution.populationToHospitalsRatio);
        assertEquals(500, (int) PopulationParameters.get().buildingDistribution.populationToSchoolsRatio);
        assertEquals(200, (int) PopulationParameters.get().buildingDistribution.populationToShopsRatio);
        assertEquals(2000, (int) PopulationParameters.get().buildingDistribution.populationToConstructionSitesRatio);
        assertEquals(100, (int) PopulationParameters.get().buildingDistribution.populationToOfficesRatio);
        assertEquals(3000, (int) PopulationParameters.get().buildingDistribution.populationToNurseriesRatio);
        assertEquals(800, (int) PopulationParameters.get().buildingDistribution.populationToRestaurantsRatio);

        assertEqualsP(0.1, PopulationParameters.get().workerDistribution.pOffice);
        assertEqualsP(0.2, PopulationParameters.get().workerDistribution.pShop);
        assertEqualsP(0.2, PopulationParameters.get().workerDistribution.pHospital);
        assertEqualsP(0.1, PopulationParameters.get().workerDistribution.pConstruction);
        assertEqualsP(0.2, PopulationParameters.get().workerDistribution.pRestaurant);
        assertEqualsP(0.1, PopulationParameters.get().workerDistribution.pUnemployed);

        assertEquals(0.8, PopulationParameters.get().buildingProperties.baseTransmissionConstant, EPSILON);
        assertEquals(0.2, PopulationParameters.get().buildingProperties.hospitalTransmissionConstant, EPSILON);
        assertEquals(0.3, PopulationParameters.get().buildingProperties.constructionSiteTransmissionConstant, EPSILON);
        assertEquals(0.001, PopulationParameters.get().buildingProperties.nurseryTransmissionConstant, EPSILON);
        assertEquals(0.6, PopulationParameters.get().buildingProperties.officeTransmissionConstant, EPSILON);
        assertEquals(0.1, PopulationParameters.get().buildingProperties.restaurantTransmissionConstant, EPSILON);
        assertEquals(0.05, PopulationParameters.get().buildingProperties.schoolTransmissionConstant, EPSILON);
        assertEquals(0.2, PopulationParameters.get().buildingProperties.shopTransmissionConstant, EPSILON);

        assertEqualsP(0.1, PopulationParameters.get().buildingProperties.pHospitalKey);
        assertEqualsP(0.6, PopulationParameters.get().buildingProperties.pConstructionSiteKey);
        assertEqualsP(0.2, PopulationParameters.get().buildingProperties.pShopKey);
        assertEqualsP(0.9, PopulationParameters.get().buildingProperties.pOfficeKey);

        assertEqualsP(0.8, PopulationParameters.get().infantProperties.pAttendsNursery);

        assertEqualsP(0.144, PopulationParameters.get().householdProperties.pHouseholdVisitsNeighbourDaily);
        assertEquals(2, (int) PopulationParameters.get().householdProperties.expectedNeighbours);

        assertEqualsP(0.9, PopulationParameters.get().personProperties.pTransmission);
        assertEqualsP(0.7, PopulationParameters.get().personProperties.pQuarantinesIfSymptomatic);

        assertEqualsP(0.8, PopulationParameters.get().householdProperties.pVisitorsLeaveHousehold);

        assertEqualsP(0.25, PopulationParameters.get().buildingDistribution.officeSizeDistribution.pSmall);
        assertEqualsP(0.4, PopulationParameters.get().buildingDistribution.officeSizeDistribution.pMed);
        assertEqualsP(0.35, PopulationParameters.get().buildingDistribution.officeSizeDistribution.pLarge);

        assertEqualsP(0.4, PopulationParameters.get().buildingProperties.pLeaveRestaurant);
        assertEqualsP(0.5, PopulationParameters.get().buildingProperties.pLeaveShop);
        assertEqualsP(0.01786, PopulationParameters.get().householdProperties.pGoShopping);
        assertEqualsP(0.01190, PopulationParameters.get().householdProperties.pGoRestaurant);
    }

    private void assertEqualsP(double v, Probability p) {
        assertEquals(v, p.asDouble(), EPSILON);
    }

    @After
    public void clearParams() {
        CovidParameters.clearParameters();
        PopulationParameters.clearParameters();
    }
}
