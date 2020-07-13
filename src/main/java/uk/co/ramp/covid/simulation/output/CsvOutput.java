package uk.co.ramp.covid.simulation.output;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CsvOutput {
    private static final Logger LOGGER = LogManager.getLogger(CsvOutput.class);

    public static void writeDailyStats(Appendable out, int startIterID, List<List<DailyStats>> stats) throws IOException {
    final String[] headers = {"iter", "day", "H", "L", "A", "P1", "P2", "D", "R", "ISeed",
                              "ICs_W","IHos_W","INur_W","IOff_W","IRes_W","ISch_W","ISho_W","ICHome_W",
                              "IHome_I", "ICHome_R",
                              "ICs_V","IHos_V","INur_V","IOff_V","IRes_V","ISch_V","ISho_V","IHome_V", "ITransport",
                              "IAdu","IPen","IChi","IInf",
                              "DAdul","DPen","DChi","DInf","DHome", "DHospital", "DCareHome", "DAdditional",
                              "NumHospital", "HospitalisedToday",
                              "SecInfections", "GenerationTime" };
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
        for (int i = 0; i < stats.size(); i++) {
            for (DailyStats s : stats.get(i)) {
                printer.printRecord(s.csvRecords(startIterID + i));
            }
        }
        printer.close();
    }
    
    public static void writeDailyStats(Path outF, int startIterID, List<List<DailyStats>> stats) {
        try {
            FileWriter out = new FileWriter(outF.toFile());
            writeDailyStats(out, startIterID, stats);
            out.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static void extraOutputsForThibaud(Path outputDir, List<List<DailyStats>> stats) {
        try {
            CSVPrinter dailyInfectionsCSV = new CSVPrinter(new FileWriter(outputDir.resolve("dailyInfections.csv").toFile()), CSVFormat.DEFAULT);
            CSVPrinter deathsCSV = new CSVPrinter(new FileWriter(outputDir.resolve("deaths.csv").toFile()), CSVFormat.DEFAULT);
            for (int i = 0; i < stats.size(); i++) {
                for (DailyStats s : stats.get(i)) {
                    dailyInfectionsCSV.print(s.getTotalDailyInfections());
                    deathsCSV.print(s.getTotalDeaths());
                }
                dailyInfectionsCSV.println();
                deathsCSV.println();
            }
            dailyInfectionsCSV.close(true);
            deathsCSV.close(true);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
