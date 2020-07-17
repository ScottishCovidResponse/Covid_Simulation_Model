package uk.co.ramp.covid.simulation.output;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
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
    
    private final Path outputDir;
    private final List<List<DailyStats>> stats;
    private final int startIteration;

    public CsvOutput(Path outputDir, int startIteration, List<List<DailyStats>> modelStats) {
        this.outputDir = outputDir;
        this.startIteration = startIteration;
        this.stats = modelStats;
    }
    
    public CsvOutput(int startIteration, List<List<DailyStats>> modelStats) {
        this(null, startIteration, modelStats);
    }
    
    public void writeOutput() {
        if (outputDir != null) {
            writeDailyStats();
            extraOutputsForThibaud();
            writeDeathsByAge();
        } else {
            LOGGER.warn("Trying to output csv files, but no output directory was set");
        }
    }

    private void writeDailyStats(Appendable out) throws IOException {
        if (stats.isEmpty() || stats.get(0).isEmpty()) {
            return;
        }

        String[] headers = stats.get(0).get(0).csvHeaders().toArray(String[]::new);
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
        for (int i = 0; i < stats.size(); i++) {
            for (DailyStats s : stats.get(i)) {
                printer.printRecord(s.csvRecords(startIteration + i));
            }
        }
        printer.close();
    }
    
    private void writeDailyStats() {
        try {
            FileWriter out = new FileWriter(outputDir.resolve(STATS_FNAME).toFile());
            writeDailyStats(out);
            out.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
    
    public String dailyStatsAsCSVString() {
        StringWriter sw = new StringWriter();
        try {
            writeDailyStats(sw);
        } catch (IOException e) {
            LOGGER.warn("Could not convert DailyStats to CSV string");
            return "";
        }
        return sw.toString();
    }

    private void extraOutputsForThibaud() {
        try {
            CSVPrinter dailyInfectionsCSV = new CSVPrinter(
                    new FileWriter(outputDir.resolve(INFECTIONS_OVER_TIME_FNAME).toFile()), CSVFormat.DEFAULT);
            CSVPrinter deathsCSV = new CSVPrinter(
                    new FileWriter(outputDir.resolve(DEATHS_OVER_TIME_FNAME).toFile()), CSVFormat.DEFAULT);
            for (List<DailyStats> stat : stats) {
                for (DailyStats s : stat) {
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

    private void writeDeathsByAge()  {
        final String[] headers = {"iter", "day", "age", "deaths"};
        try {
            FileWriter file = new FileWriter(outputDir.resolve(DEATHS_BY_AGE_FNAME).toFile());
            CSVPrinter printer = new CSVPrinter(file, CSVFormat.DEFAULT.withHeader(headers));

            for (int i = 0; i < stats.size(); i++) {
                for (DailyStats s : stats.get(i)) {
                    for (Map.Entry<Integer, Integer> entry : s.deathsByAge.entrySet()) {
                        printer.printRecord(startIteration + i, s.day.get(), entry.getKey(), entry.getValue());
                    }
                }
            }

            printer.close(true);
        } catch (IOException e) {
            LOGGER.error("Could not output deathsByAge");
        }
    }
}
