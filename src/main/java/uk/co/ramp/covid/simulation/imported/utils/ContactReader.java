package uk.co.ramp.covid.simulation.imported.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactReader {

    private static final Logger LOGGER = LogManager.getLogger(ContactReader.class);

    private ContactReader() {
        // hidden constructor
    }

    public static Map<Integer, List<ContactRecord>> read(int personLimit, int dayLimit) {

        Iterable<CSVRecord> records;
        File file = new File("contacts.csv");
        try {
            Reader in = new FileReader(file);
            records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
        } catch (IOException e) {
            LOGGER.fatal("An error occurred parsing this file.");
            throw new RuntimeException("An error occured reading the file " + e.getMessage());
        }
        double maxWeight = 0d;
        Map<Integer, List<ContactRecord>> dailyRecord = new HashMap<>();
        for (CSVRecord record : records) {

            int day = Integer.parseInt(record.get(0)) - 1;
            int from = Integer.parseInt(record.get(1)) - 1;
            int to = Integer.parseInt(record.get(2)) - 1;
            double weight = Double.parseDouble(record.get(3));

            // TODO remove these when no longer developing
            if (day > dayLimit) break;
            if (from >= personLimit || to >= personLimit) continue;

            ContactRecord c = new ContactRecord(day, from, to, weight);
            dailyRecord.putIfAbsent(day, new ArrayList<>());
            dailyRecord.get(day).add(c);

            if (weight > maxWeight) {
                maxWeight = weight;
            }

        }

        LOGGER.info("maxWeight = {}", maxWeight);

        return dailyRecord;


    }


}
