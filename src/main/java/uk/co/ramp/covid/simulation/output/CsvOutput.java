package uk.co.ramp.covid.simulation.output;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CsvOutput {
    private static final Logger LOGGER = LogManager.getLogger(CsvOutput.class);

    private static final String STATS_FNAME = "out.csv";
    private static final String INFECTIONS_OVER_TIME_FNAME = "dailyInfections.csv";
    private static final String DEATHS_OVER_TIME_FNAME = "deaths.csv";
    private static final String DEATHS_BY_AGE_FNAME = "deathsByAge.csv";
    
    private Path outputDir;
    private List<List<DailyStats>> stats;
    private int startIteration;

    public CsvOutput(Path outputDir, int startIteration, List<List<DailyStats>> modelStats) {
        this.outputDir = outputDir;
        this.startIteration = startIteration;
        this.stats = modelStats;
    }
    
    public void writeOutput() {
        writeDailyStats(outputDir, startIteration, stats);
        extraOutputsForThibaud(outputDir, stats);
        writeDeathsByAge(outputDir, startIteration, stats);
    }

    public static void writeDailyStats(Appendable out, int startIterID, List<List<DailyStats>> stats) throws IOException {
        if (stats.isEmpty() || stats.get(0).isEmpty()) {
            return;
        }

        String[] headers = stats.get(0).get(0).csvHeaders().toArray(String[]::new);
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
            FileWriter out = new FileWriter(outF.resolve(STATS_FNAME).toFile());
            writeDailyStats(out, startIterID, stats);
            out.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static void extraOutputsForThibaud(Path outputDir, List<List<DailyStats>> stats) {
        try {
            CSVPrinter dailyInfectionsCSV = new CSVPrinter(
                    new FileWriter(outputDir.resolve(INFECTIONS_OVER_TIME_FNAME).toFile()), CSVFormat.DEFAULT);
            CSVPrinter deathsCSV = new CSVPrinter(
                    new FileWriter(outputDir.resolve(DEATHS_OVER_TIME_FNAME).toFile()), CSVFormat.DEFAULT);
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

    public static void writeDeathsByAge(Path outputDir, int startIter, List<List<DailyStats>> stats)  {
        final String[] headers = {"iter", "day", "age", "deaths"};
        try {
            FileWriter file = new FileWriter(outputDir.resolve(DEATHS_BY_AGE_FNAME).toFile());
            CSVPrinter printer = new CSVPrinter(file, CSVFormat.DEFAULT.withHeader(headers));

            for (int i = 0; i < stats.size(); i++) {
                for (DailyStats s : stats.get(i)) {
                    for (Map.Entry<Integer, Integer> entry : s.deathsByAge.entrySet()) {
                        printer.printRecord(startIter + i, s.day.get(), entry.getKey(), entry.getValue());
                    }
                }
            }

            printer.close(true);
        } catch (IOException e) {
            LOGGER.error("Could not output deathsByAge");
        }
    }
}
