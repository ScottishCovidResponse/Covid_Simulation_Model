package uk.co.ramp.covid.simulation.output;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.*;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;

/** DailyStats accumulates statistics, e.g. healthy/dead, for a particular day */
public class DailyStats2 {
    private static final Logger LOGGER = LogManager.getLogger(DailyStats2.class);

    private List<Value<?>> all = new ArrayList<>(); // in csv column order
    private List<IntValue> population = new ArrayList<>();
    private List<IntValue> infected = new ArrayList<>();
    private List<IntValue> dailyInfections = new ArrayList<>();
    private List<IntValue> logged = new ArrayList<>();
    private List<IntValue> deaths = new ArrayList<>();

    public IntValue iter = add("iter");
    public IntValue day = add("day").log("Day");

    // Daily cumulative statistics
    public IntValue healthy = add("H").log("Healthy").addTo(population);
    public IntValue exposed = add("L").log("Latent").addTo(population).addTo(infected);
    private IntValue asymptomatic = add("A").log("Asymptomatic").addTo(population).addTo(infected);
    private IntValue phase1 = add("P1").log("Phase 1").addTo(population).addTo(infected);
    private IntValue phase2 = add("P2").log("Phase 2").addTo(population).addTo(infected);
    private IntValue dead = add("D").log("Dead").addTo(population);
    private IntValue recovered = add("R").log("Recovered").addTo(population);

    // Daily only statistics
    private IntValue seedInfections = add("ISeed").addTo(dailyInfections);
    private IntValue constructionSiteInfectionsWorker = add("ICs_W").addTo(dailyInfections);
    private IntValue hospitalInfectionsWorker = add("IHos_W").addTo(dailyInfections);
    private IntValue nurseryInfectionsWorker = add("INur_W").addTo(dailyInfections);
    private IntValue officeInfectionsWorker = add("IOff_W").addTo(dailyInfections);
    private IntValue restaurantInfectionsWorker = add("IRes_W").addTo(dailyInfections);
    private IntValue schoolInfectionsWorker = add("ISch_W").addTo(dailyInfections);
    private IntValue shopInfectionsWorker = add("ISho_W").addTo(dailyInfections);
    private IntValue careHomeInfectionsWorker = add("ICHome_W").addTo(dailyInfections);
    private IntValue homeInfectionsInhabitant = add("IHome_I").addTo(dailyInfections);
    private IntValue careHomeInfectionsResident = add("ICHome_R").addTo(dailyInfections);
    private IntValue constructionSiteInfectionsVisitor = add("ICs_V").addTo(dailyInfections);
    private IntValue hospitalInfectionsVisitor = add("IHos_V").addTo(dailyInfections);
    private IntValue nurseryInfectionsVisitor = add("INur_V").addTo(dailyInfections);
    private IntValue officeInfectionsVisitor = add("IOff_V").addTo(dailyInfections);
    private IntValue restaurantInfectionsVisitor  = add("IRes_V").addTo(dailyInfections);
    private IntValue schoolInfectionsVisitor = add("ISch_V").addTo(dailyInfections);
    private IntValue shopInfectionsVisitor = add("ISho_V").addTo(dailyInfections);
    private IntValue homeInfectionsVisitor = add("IHome_V").addTo(dailyInfections);

    // Age Statistics
    private IntValue adultInfected = add("IAdu");
    private IntValue pensionerInfected = add("IPen");
    private IntValue childInfected  = add("IChi");
    private IntValue infantInfected = add("IInf");

    // Fatality Statistics
    private IntValue adultDeaths = add("DAdul").addTo(deaths);
    private IntValue pensionerDeaths = add("DPen").addTo(deaths);
    private IntValue childDeaths = add("DChi").addTo(deaths);
    private IntValue infantDeaths = add("DInf").addTo(deaths);
    private IntValue homeDeaths = add("DHome");
    private IntValue hospitalDeaths = add("DHospital");
    private IntValue careHomeDeaths = add("DCareHome");
    private IntValue additionalDeaths = add("DAdditional"); // Deaths in a workplace/school/etc

    // Hospitalisation Stats
    private IntValue inHospital = add("NumHospital").log("Hospitalised");
    private IntValue newlyHospitalised = add("HospitalisedToday");
            
    // Infection rate stats
    public Value<Double> secInfections = new Value<Double>("SecInfections");
    private Value<Double> generationTime = new Value<Double>("GenerationTime");

    public class Value<T> {
        private final String csvHeader;
        protected String nameForLog;
        protected T value;

        protected Value(String csvHeader) {
            all.add(this);
            this.csvHeader = csvHeader;            
        }

        public T get() { return value; }
        public void set(T value) { this.value = value; }
        public String header() { return csvHeader; } 

        public String logString() {
            return nameForLog + " " + toString();
        }

        @Override
        public String toString() { return value.toString(); }

        @Override
        public int hashCode() { return value.hashCode(); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Value<?> other = (Value<?>) o;
            return value.equals(other.value);
        }
    }

    public class IntValue extends Value<Integer> {

        public IntValue(String csvHeader) {
            super(csvHeader);
        }
        
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
        return sum(population);
    }

    public int getTotalInfected() {
        return sum(infected);
    }
    
    public int getTotalDailyInfections() {
        return sum(dailyInfections);
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
