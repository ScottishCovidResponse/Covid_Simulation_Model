package uk.co.ramp.covid.simulation.output;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.co.ramp.covid.simulation.Model;
import uk.co.ramp.covid.simulation.testutil.SimulationTest;

import static org.junit.Assert.*;

public class CsvOutputTest extends SimulationTest {

    @Test
    public void testCsvOutput() throws IOException {
        
        Model m = new Model()
                .setPopulationSize(10000)
                .setnInitialInfections(200)
                .setExternalInfectionDays(0)
                .setIters(1)
                .setnDays(20)
                .setRNGSeed(0)
                .setNoOutput();

        int startIterID = 2;
        List<List<DailyStats>> stats = m.run(startIterID);        
        
        StringWriter sw = new StringWriter();
        CsvOutput.writeDailyStats(sw, startIterID, stats);
        StringReader sr = new StringReader(sw.toString());
        
        try (BufferedReader br = new BufferedReader(sr)) {
            String line = br.readLine();
            String expectedHeader = "iter,day,H,L,A,P1,P2,D,R,ISeed,"
                    + "ICs_W,IHos_W,INur_W,IOff_W,IRes_W,ISch_W,ISho_W,ICHome_W,"
                    + "IHome_I,ICHome_R,"
                    + "ICs_V,IHos_V,INur_V,IOff_V,IRes_V,ISch_V,ISho_V,IHome_V,ITransport,"
                    + "IAdu,IPen,IChi,IInf,"
                    + "DAdul,DPen,DChi,DInf,DHome,DHospital,DCareHome,DAdditional,"
                    + "NumHospital,HospitalisedToday,SecInfections,GenerationTime";
            assertEquals("Wrong csv header", line, expectedHeader);

            int day = 0;
            while ((line = br.readLine()) != null) {
                DailyStats s = stats.get(0).get(day);
                Object[] values = new Object[] {
                        startIterID, day,
                        s.getHealthy(), s.getExposed(), s.getAsymptomatic(),
                        s.getPhase1(), s.getPhase2(), s.getDead(),
                        s.getRecovered(), s.getSeedInfections(),
                        s.getConstructionSiteInfectionsWorker(),
                        s.getHospitalInfectionsWorker(), 
                        s.getNurseryInfectionsWorker(),
                        s.getOfficeInfectionsWorker(),
                        s.getRestaurantInfectionsWorker(),
                        s.getSchoolInfectionsWorker(),
                        s.getShopInfectionsWorker(),
                        s.getCareHomeInfectionsWorker(),
                        s.getHomeInfectionsInhabitant(),
                        s.getCareHomeInfectionsResident(),
                        s.getConstructionSiteInfectionsVisitor(),
                        s.getHospitalInfectionsVisitor(),
                        s.getNurseryInfectionsVisitor(),
                        s.getOfficeInfectionsVisitor(),
                        s.getRestaurantInfectionsVisitor(),
                        s.getSchoolInfectionsVisitor(),
                        s.getShopInfectionsVisitor(),
                        s.getHomeInfectionsVisitor(),
                        s.getTransportInfections(),
                        s.getAdultInfected(), s.getPensionerInfected(),
                        s.getChildInfected(), s.getInfantInfected(),
                        s.getAdultDeaths(), s.getPensionerDeaths(),
                        s.getChildDeaths(), s.getInfantDeaths(),
                        s.getHomeDeaths(), s.getHospitalDeaths(),
                        s.getCareHomeDeaths(), s.getAdditionalDeaths(),
                        s.getNumInHospital(), s.getNewlyHospitalised(),
                        doubleToString(s.getSecInfections()),
                        doubleToString(s.getGenerationTime())
                };
                String expected = Arrays.toString(values).replace(" ", "");
                assertEquals(expected, "[" + line + "]");
                day++;
            }
        }
    }
    
    private String doubleToString(Double d) {
        return d == null ? "" : d.toString();
    }
}
