package uk.co.ramp.covid.simulation.io;

import org.junit.After;
import org.junit.Test;
import uk.co.ramp.covid.simulation.CovidParameters;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ParameterReaderTest {

    private final static double EPSILON = 0.000001;

    @Test
    public void readParametersFromFile() throws IOException {
        // Note the parameters in the test file are sufficiently different from the defaults to allow testing
        // and do not necessarily represent real world parameters
        ParameterReader.readParametersFromFile("src/test/resources/test_params.json");

        // Disease Parameters (Tests CovidParameters as a side effect)
        assertEquals(10, CovidParameters.get().getMeanLatentPeriod());
        assertEquals(5, CovidParameters.get().getMeanAsymptomaticPeriod());
        assertEquals(2, CovidParameters.get().getMeanPhase1DurationMild());
        assertEquals(5, CovidParameters.get().getMeanPhase1DurationSevere());
        assertEquals(100.0, CovidParameters.get().getMortalityRate(), EPSILON);
        assertEquals(0.03, CovidParameters.get().getChildProgressionPhase2(), EPSILON);
        assertEquals(0.20, CovidParameters.get().getAdultProgressionPhase2(), EPSILON);
        assertEquals(0.5, CovidParameters.get().getPensionerProgressionPhase2(), EPSILON);

        // Population Parameters
        assertEquals(0.1, PopulationParameters.get().getpInfants(), EPSILON);
        assertEquals(0.3, PopulationParameters.get().getpChildren(), EPSILON);
        assertEquals(0.4, PopulationParameters.get().getpAdults(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpPensioners(), EPSILON);

        assertEquals(0.2, PopulationParameters.get().getpAdultOnly(), EPSILON);
        assertEquals(0.1, PopulationParameters.get().getpPensionerOnly(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpPensionerAdult(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpAdultChildren(), EPSILON);
        assertEquals(0.15, PopulationParameters.get().getpPensionerChildren(), EPSILON);
        assertEquals(0.15, PopulationParameters.get().getpAdultPensionerChildren(), EPSILON);

        // Using size as a proxy to make sure it picks up the right parameter.
        // As map parsing is built into GSON we don't test this again
        assertEquals(3, PopulationParameters.get().getAdultAllocationPMap().size());
        assertEquals(4, PopulationParameters.get().getPensionerAllocationPMap().size());
        assertEquals(5, PopulationParameters.get().getChildAllocationPMap().size());
        assertEquals(2, PopulationParameters.get().getInfantAllocationPMap().size());

        assertEquals(1000, PopulationParameters.get().getHospitalRatio());
        assertEquals(500, PopulationParameters.get().getSchoolsRatio());
        assertEquals(200, PopulationParameters.get().getShopsRatio());
        assertEquals(2000, PopulationParameters.get().getConstructionSiteRatio());
        assertEquals(100, PopulationParameters.get().getOfficesRatio());
        assertEquals(3000, PopulationParameters.get().getNurseriesRatio());
        assertEquals(800, PopulationParameters.get().getRestaurantRatio());

        assertEquals(0.1, PopulationParameters.get().getpOfficeWorker(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpShopWorker(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpHospitalWorker(), EPSILON);
        assertEquals(0.1, PopulationParameters.get().getpConstructionWorker(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpRestaurantWorker(), EPSILON);
        assertEquals(0.1, PopulationParameters.get().getpUnemployed(), EPSILON);

        assertEquals(0.8, PopulationParameters.get().getpBaseTrans(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpHospitalTrans(), EPSILON);
        assertEquals(0.3, PopulationParameters.get().getpConstructionSiteTrans(), EPSILON);
        assertEquals(0.001, PopulationParameters.get().getpNurseryTrans(), EPSILON);
        assertEquals(0.6, PopulationParameters.get().getpOfficeTrans(), EPSILON);
        assertEquals(0.1, PopulationParameters.get().getpRestaurantTrans(), EPSILON);
        assertEquals(0.05, PopulationParameters.get().getpSchoolTrans(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpShopTrans(), EPSILON);
        assertEquals(0.1, PopulationParameters.get().getpHospitalKey(), EPSILON);
        assertEquals(0.6, PopulationParameters.get().getpConstructionSiteKey(), EPSILON);
        assertEquals(0.2, PopulationParameters.get().getpShopKey(), EPSILON);
        assertEquals(0.9, PopulationParameters.get().getpOfficeKey(), EPSILON);

        assertEquals(0.8, PopulationParameters.get().getpAttendsNursery(), EPSILON);

        assertEquals(0.05, PopulationParameters.get().getNeighbourVisitFreq(), EPSILON);
        assertEquals(2, PopulationParameters.get().getExpectedNeighbours());

        assertEquals(0.9, PopulationParameters.get().getpTransmission(), EPSILON);
        assertEquals(0.7, PopulationParameters.get().getpQuarantine(), EPSILON);

        assertEquals(0.8, PopulationParameters.get().getHouseholdVisitorLeaveRate(), EPSILON);

        assertEquals(0.25, PopulationParameters.get().getpOfficeSmall(), EPSILON);
        assertEquals(0.4, PopulationParameters.get().getpOfficeMed(), EPSILON);
        assertEquals(0.35, PopulationParameters.get().getpOfficeLarge(), EPSILON);
    }

    @After
    public void clearParams() {
        CovidParameters.clearParameters();
        PopulationParameters.clearParameters();
    }
}
