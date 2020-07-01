package uk.co.ramp.covid.simulation.output;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.*;

import java.io.IOException;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** DailyStats accumulates statistics, e.g. healthy/dead, for a particular day */
public class DailyStats2 {
    private static final Logger LOGGER = LogManager.getLogger(DailyStats2.class);

    private final int day;

    // Infection rate stats
    private Double secInfections = null;
    private Double generationTime = null;

    private static class Stat {
        public final String csvHeader;
        public int value = 0;

        public Stat(String csvHeader) { this.csvHeader = csvHeader; }
    }
    
    private Map<String, Stat> map = new HashMap<>();
    private List<Stat> all = new ArrayList<>(); // in csv column order
    private List<Stat> totalPopulation = new ArrayList<>();
    private List<Stat> totalInfected = new ArrayList<>();
    private List<Stat> totalDailyInfections = new ArrayList<>();
    private List<Stat> logged = new ArrayList<>();
    private List<Stat> deaths = new ArrayList<>();

    private static final int totalPopulationFlag = 1;
    private static final int totalInfectedFlag = 1 << 1;
    private static final int totalDailyInfectionsFlag = 1 << 2;
    private static final int loggedFlag = 1 << 3;
    private static final int deathsFlag = 1 << 4;
    
    private void addStat(String name, String csvHeader, int flags) {
        Stat stat = new Stat(csvHeader);
        all.add(stat);
        map.put(name, stat);
        
        if ((flags & totalPopulationFlag) != 0) {
            totalPopulation.add(stat);
        }
        if ((flags & totalInfectedFlag) != 0) {
            totalInfected.add(stat);
        }
        //...TODO
    }

    public DailyStats2(Time t) {
        this.day = t.getAbsDay();
        addStat("healthy", "H", totalPopulationFlag | loggedFlag);
        addStat("exposed", "L", totalPopulationFlag | totalInfectedFlag | loggedFlag);
        // ...TODO
    }
    
    public int get(String name) { return map.get(name).value; }
    
    public void increment(String name) { map.get(name).value++; }

    public void processPerson(Person p) {
        switch (p.cStatus()) {
            case HEALTHY: increment("healthy"); break;
            case LATENT: increment("exposed"); break;
            case ASYMPTOMATIC: increment("asymptomatic"); break;
            case PHASE1: increment("phase1"); break;
            case PHASE2: increment("phase2"); break;
            case RECOVERED: increment("recovered"); break;
            case DEAD: increment("dead"); break;
        }
        
        if (p.isHospitalised()) {
            increment("inHospital");
        }
    }
    
    private int sum(List<Stat> stats) {
        return stats.stream().mapToInt(s -> s.value).sum();
    }

    public int getTotalPopulation() {
        return sum(totalPopulation);
    }

    public int getTotalInfected() {
        return sum(totalInfected);
    }
    
    public int getTotalDailyInfections() {
        return sum(totalDailyInfections);
    }
   
    public void log(){
        // TODO
        //LOGGER.info("Day = {} Healthy = {} Latent = {} Asymptomatic = {} Phase 1 = {} " +
        //                "Phase 2 = {} Hospitalised = {} Dead = {} Recovered = {}",
        //        day, healthy, exposed, asymptomatic,phase1, phase2, inHospital, dead, recovered);
    }

    // TODO: We should probably either move the outputCSV method in here,
    // or alternatively return the list of values (and not use the CSVPrinter here)
    public void appendCSV(CSVPrinter csv, int iter) throws IOException {
        List<Object> values = new ArrayList<>();
        values.add(iter);
        values.add(day);
        all.forEach(s -> { values.add(s.value); });
        values.add(secInfections);
        values.add(generationTime);
        csv.printRecord(values);
    }

    public List<String> csvHeaders() {
        List<String> headers = new ArrayList<>();
        headers.add("iter");
        headers.add("day");
        all.forEach(s -> { headers.add(s.csvHeader); });
        headers.add("SecInfections");
        headers.add("GenerationTime");
        return headers;
    }

    public int getTotalDeaths() { return sum(deaths); }

    @Override
    public int hashCode() {
        return Objects.hash(all);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyStats2 that = (DailyStats2) o;
        if (day != that.day)
            return false;

        for (int i = 0; i < all.size(); i++) {
            if (!all.get(i).equals(that.all.get(i)))
                return false;            
        }
        return true;
    }

    public void determineRValues(Population p) {
        RStats rs = new RStats(p);
        secInfections = rs.getSecInfections(day);
        generationTime = rs.getMeanGenerationTime(day);
    }
}
