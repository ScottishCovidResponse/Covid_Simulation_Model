package uk.co.ramp.covid.simulation.output;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.parameters.PopulationDistribution.SexAge;
import uk.co.ramp.covid.simulation.population.*;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** DailyStats accumulates statistics, e.g. healthy/dead, for a particular day */
public class DailyStats2 {
    private static final Logger LOGGER = LogManager.getLogger(DailyStats2.class);

    private List<Value> all = new ArrayList<>(); // in csv column order
    private List<IntValue> totalPopulation = new ArrayList<>();
    private List<IntValue> totalInfected = new ArrayList<>();
    private List<IntValue> totalDailyInfections = new ArrayList<>();
    private List<IntValue> logged = new ArrayList<>();
    private List<IntValue> deaths = new ArrayList<>();
    
    public IntValue iter = add("iter");
    public IntValue day = add("day");

    // Daily cumulative statistics
    public IntValue healthy = add("H").log("Healthy").addTo(totalPopulation);
    public IntValue exposed = add("L").log("Latent").addTo(totalPopulation).addTo(totalInfected);

    // ...TODO        
            
    // Infection rate stats
    public DoubleValue secInfections = new DoubleValue("SecInfections");
    private DoubleValue generationTime = new DoubleValue("GenerationTime");

    public class Value {
        private final String csvHeader;
        protected String nameForLog;

        protected Value(String csvHeader) {
            all.add(this);
            this.csvHeader = csvHeader;            
        }

        public String header() { return csvHeader; } 

        public String logString() {
            return nameForLog + " " + toString();
        }
    }
    
    public class IntValue extends Value {
        private int value = 0;

        public IntValue(String csvHeader) {
            super(csvHeader);
        }
        
        public int get() { return value; }
        public void set(int value) { this.value = value; }
        public void increment() { value++; }        
        
        public IntValue log(String name) {
            logged.add(this);
            this.nameForLog = name;
            return this;
        }
        
        public IntValue addTo(List<IntValue> list) {
            list.add(this);
            return this;
        }
        
        @Override
        public String toString() { return Integer.toString(value); }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntValue other = (IntValue) o;
            return value == other.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }
    
    public class DoubleValue extends Value {
        private double realValue;
        
        public DoubleValue(String csvHeader) {
            super(csvHeader);
        }
        
        public void set(double value) { this.realValue = value; }

        @Override
        public String toString() { return Double.toString(realValue); }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DoubleValue other = (DoubleValue) o;
            return realValue == other.realValue;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(realValue);
        }        
    }
    
    private IntValue add(String csvHeader) { return new IntValue(csvHeader); }
    
    public DailyStats2(Time t) {
        day.set(t.getAbsDay());
    }
    
    public void processPerson(Person p) {
        switch (p.cStatus()) {
            case HEALTHY: healthy.increment(); break;
            case LATENT: exposed.increment(); break;
            case ASYMPTOMATIC: asymptomatic.increment(); break;
            case PHASE1: phase1.increment(); break;
            case PHASE2: phase2.increment(); break;
            case RECOVERED: recovered.increment(); break;
            case DEAD: dead.increment(); break;
        }
        
        if (p.isHospitalised()) {
            inHospital.increment();
        }
    }
    
    private int sum(List<IntValue> stats) {
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
   
    public String logString() {
        return String.join(" ", logged.stream().map(s -> s.logString()).toArray(String[]::new));
    }
    
    public void log(){
        LOGGER.info(logString());
        //LOGGER.info("Day = {} Healthy = {} Latent = {} Asymptomatic = {} Phase 1 = {} " +
        //                "Phase 2 = {} Hospitalised = {} Dead = {} Recovered = {}",
        //        day, healthy, exposed, asymptomatic,phase1, phase2, inHospital, dead, recovered);
    }

    // TODO: We should probably either move the outputCSV method in here,
    // or alternatively return the list of values (and not pass in the CSVPrinter here)
    public void appendCSV(CSVPrinter csv, int iter) throws IOException {
        this.iter.set(iter);
        csv.printRecord(all);
    }

    public Stream<String> csvHeaders() {
        return all.stream().map(value -> value.header());
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
        return all.equals(that.all);
    }

    public void determineRValues(Population p) {
        RStats rs = new RStats(p);
        secInfections.set(rs.getSecInfections(day.get()));
        generationTime.set(rs.getMeanGenerationTime(day.get()));
    }
}
