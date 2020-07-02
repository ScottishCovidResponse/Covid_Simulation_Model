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
    private List<Value> totalPopulation = new ArrayList<>();
    private List<Value> totalInfected = new ArrayList<>();
    private List<Value> totalDailyInfections = new ArrayList<>();
    private List<Value> logged = new ArrayList<>();
    private List<Value> deaths = new ArrayList<>();
    
    public Value iter = add("iter");
    public Value day = add("day");

    // Daily cumulative statistics
    public Value healthy = add("H").log("Healthy").addTo(totalPopulation);
    public Value exposed = add("L").log("Latent").addTo(totalPopulation).addTo(totalInfected);

    // ...TODO        
            
    // Infection rate stats
    public RealValue secInfections = new RealValue("SecInfections");
    private RealValue generationTime = new RealValue("GenerationTime");

    public class Value {
        private final String csvHeader;
        protected String nameForLog;
        private int value = 0;

        public Value(String csvHeader) {
            all.add(this);
            this.csvHeader = csvHeader;
        }
        
        public String header() { return csvHeader; } 
        public int get() { return value; }
        public void set(int value) { this.value = value; }
        public void increment() { value++; }        
        
        public Value log(String name) {
            logged.add(this);
            this.nameForLog = name;
            return this;
        }
        
        public Value addTo(List<Value> list) {
            list.add(this);
            return this;
        }
        
        public String logString() {
            return nameForLog + " " + value;
        }
        
        @Override
        public String toString() { return Integer.toString(value); }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Value other = (Value) o;
            return value == other.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }
    
    public class RealValue extends Value {
        private double realValue;
        
        public RealValue(String csvHeader) {
            super(csvHeader);
        }
        
        public void set(double value) { this.realValue = value; }

        @Override
        public String logString() {
            return nameForLog + " " + realValue;
        }
        
        @Override
        public String toString() { return Double.toString(realValue); }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RealValue other = (RealValue) o;
            return realValue == other.realValue;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(realValue);
        }        
    }
    
    private Value add(String csvHeader) { return new Value(csvHeader); }
    
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
    
    private int sum(List<Value> stats) {
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
