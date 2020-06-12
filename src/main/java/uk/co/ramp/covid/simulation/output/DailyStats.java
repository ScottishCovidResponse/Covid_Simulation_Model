package uk.co.ramp.covid.simulation.output;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.*;

import java.io.IOException;
import java.util.Objects;

/** DailyStats accumulates statistics, e.g. healthy/dead, for a particular day */
public class DailyStats {
    private static final Logger LOGGER = LogManager.getLogger(DailyStats.class);

    private final int day;

    // Daily cumulative statistics
    private int healthy = 0;
    private int exposed = 0;
    private int asymptomatic = 0;
    private int phase1 = 0;
    private int phase2 = 0;
    private int dead = 0;
    private int recovered = 0;

    // Daily only statistics
    private int homeInfectionsInhabitant = 0;
    private int homeInfectionsVisitor = 0;
    private int constructionSiteInfectionsWorker = 0;
    private int constructionSiteInfectionsVisitor = 0;
    private int hospitalInfectionsWorker = 0;
    private int hospitalInfectionsVisitor = 0;
    private int nurseryInfectionsWorker = 0;
    private int nurseryInfectionsVisitor = 0;
    private int officeInfectionsWorker = 0;
    private int officeInfectionsVisitor = 0;
    private int restaurantInfectionsWorker = 0;
    private int restaurantInfectionsVisitor  = 0;
    private int schoolInfectionsWorker = 0;
    private int schoolInfectionsVisitor = 0;
    private int shopInfectionsWorker = 0;
    private int shopInfectionsVisitor = 0;
    private int careHomeInfectionsWorker = 0;
    private int careHomeInfectionsResident = 0;
    private int seedInfections = 0;

    // Age Statistics
    private int adultInfected = 0;
    private int pensionerInfected = 0;
    private int childInfected  = 0;
    private int infantInfected = 0;

    // Fatality Statistics
    private int adultDeaths = 0;
    private int pensionerDeaths = 0;
    private int childDeaths = 0;
    private int infantDeaths = 0;

    // Infection rate stats
    private Double secInfections = null;
    private Double generationTime = null;


    public DailyStats(Time t) {
        this.day = t.getAbsDay();
    }

    public void processPerson(Person p) {
        switch (p.cStatus()) {
            case HEALTHY: healthy++; break;
            case LATENT: exposed++; break;
            case ASYMPTOMATIC: asymptomatic++; break;
            case PHASE1: phase1++; break;
            case PHASE2: phase2++; break;
            case RECOVERED: recovered++; break;
            case DEAD: dead++; break;
        }
    }

    public void incrementDeaths(int deaths) {
       dead += deaths;
    }

    public int getTotalPopulation() {
        return healthy + exposed + asymptomatic + phase1 + phase2 + dead + recovered;
    }

    public int getTotalInfected() {
        return exposed + asymptomatic + phase1 + phase2;
    }

    public int getConstructionSiteInfectionsWorker() { return constructionSiteInfectionsWorker; }

    public int getHomeInfectionsInhabitant() { return homeInfectionsInhabitant; }

    public int getHomeInfectionsVisitor() { return homeInfectionsVisitor; }

    public int getConstructionSiteInfectionsVisitor() { return constructionSiteInfectionsVisitor; }

    public int getHospitalInfectionsWorker() { return hospitalInfectionsWorker; }

    public int getHospitalInfectionsVisitor() { return hospitalInfectionsVisitor; }

    public int getNurseryInfectionsWorker() { return nurseryInfectionsWorker; }

    public int getNurseryInfectionsVisitor() { return nurseryInfectionsVisitor; }

    public int getOfficeInfectionsWorker() { return officeInfectionsWorker; }

    public int getOfficeInfectionsVisitor() { return officeInfectionsVisitor; }

    public int getRestaurantInfectionsWorker() { return restaurantInfectionsWorker; }

    public int getRestaurantInfectionsVisitor() { return restaurantInfectionsVisitor; }

    public int getSchoolInfectionsWorker() { return schoolInfectionsWorker; }

    public int getSchoolInfectionsVisitor() { return schoolInfectionsVisitor; }

    public int getShopInfectionsWorker() { return shopInfectionsWorker; }

    public int getShopInfectionsVisitor() { return shopInfectionsVisitor; }

    public void log(){
        LOGGER.info("Day = {} Healthy = {} Latent = {} Asymptomatic = {} Phase 1 = {} Phase 2 = {} Dead = {} Recovered = {}",
                day, healthy, exposed, asymptomatic,phase1, phase2, dead, recovered);
    }

    public void appendCSV(CSVPrinter csv, int iter) throws IOException {
        csv.printRecord(iter, day, healthy, exposed, asymptomatic,
                phase1, phase2, dead, recovered, seedInfections,
                constructionSiteInfectionsWorker, hospitalInfectionsWorker, 
                nurseryInfectionsWorker, officeInfectionsWorker, restaurantInfectionsWorker, schoolInfectionsWorker, 
                shopInfectionsWorker, careHomeInfectionsWorker, homeInfectionsInhabitant, careHomeInfectionsResident,
                constructionSiteInfectionsVisitor, hospitalInfectionsVisitor, nurseryInfectionsVisitor, 
                officeInfectionsVisitor, restaurantInfectionsVisitor, schoolInfectionsVisitor, shopInfectionsVisitor,
                homeInfectionsVisitor, adultInfected, pensionerInfected, childInfected, infantInfected, adultDeaths,
                pensionerDeaths, childDeaths, infantDeaths, secInfections, generationTime);
    }

    public int getTotalDailyInfections () {
        return constructionSiteInfectionsVisitor +
                constructionSiteInfectionsWorker +
                hospitalInfectionsVisitor +
                hospitalInfectionsWorker +
                restaurantInfectionsVisitor +
                restaurantInfectionsWorker +
                shopInfectionsVisitor +
                shopInfectionsWorker +
                schoolInfectionsVisitor +
                schoolInfectionsWorker +
                officeInfectionsVisitor +
                officeInfectionsWorker +
                nurseryInfectionsVisitor +
                nurseryInfectionsWorker +
                careHomeInfectionsResident +
                careHomeInfectionsWorker +
                homeInfectionsInhabitant +
                homeInfectionsVisitor +
                seedInfections;
    }

    public int getHealthy() {
        return healthy;
    }

    public int getExposed() {
        return exposed;
    }

    public int getAsymptomatic() {
        return asymptomatic;
    }

    public int getPhase1() {
        return phase1;
    }

    public int getPhase2() {
        return phase2;
    }

    public int getDead() {
        return dead;
    }

    public int getRecovered() {
        return recovered;
    }

    public int getDay() {
        return day;
    }

    public int getAdultInfected() { return adultInfected; }

    public int getPensionerInfected() { return pensionerInfected; }

    public int getChildInfected() { return childInfected; }

    public int getInfantInfected() { return infantInfected; }

    public int getAdultDeaths() { return adultDeaths; }

    public int getPensionerDeaths() { return pensionerDeaths; }

    public int getChildDeaths() { return childDeaths; }

    public int getInfantDeaths() { return infantDeaths; }

    public int getTotalDeaths() { return adultDeaths + pensionerDeaths + childDeaths + infantDeaths; }

    @Override
    public int hashCode() {
        return Objects.hash(day, healthy, exposed, asymptomatic, phase1, phase2, dead, recovered,
                homeInfectionsInhabitant, homeInfectionsVisitor, constructionSiteInfectionsWorker,
                constructionSiteInfectionsVisitor, hospitalInfectionsWorker, hospitalInfectionsVisitor,
                nurseryInfectionsWorker, nurseryInfectionsVisitor, officeInfectionsWorker, officeInfectionsVisitor,
                restaurantInfectionsWorker, restaurantInfectionsVisitor, schoolInfectionsWorker, schoolInfectionsVisitor,
                shopInfectionsWorker, shopInfectionsVisitor, adultInfected, pensionerInfected, childInfected, infantInfected,
                adultDeaths, pensionerDeaths, childDeaths, infantDeaths);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyStats that = (DailyStats) o;
        return day == that.day &&
                healthy == that.healthy &&
                exposed == that.exposed &&
                asymptomatic == that.asymptomatic &&
                phase1 == that.phase1 &&
                phase2 == that.phase2 &&
                dead == that.dead &&
                recovered == that.recovered &&
                homeInfectionsInhabitant == that.homeInfectionsInhabitant &&
                homeInfectionsVisitor == that.homeInfectionsVisitor &&
                constructionSiteInfectionsWorker == that.constructionSiteInfectionsWorker &&
                constructionSiteInfectionsVisitor == that.constructionSiteInfectionsVisitor &&
                hospitalInfectionsWorker == that.hospitalInfectionsWorker &&
                hospitalInfectionsVisitor == that.hospitalInfectionsVisitor &&
                nurseryInfectionsWorker == that.nurseryInfectionsWorker &&
                nurseryInfectionsVisitor == that.nurseryInfectionsVisitor &&
                officeInfectionsWorker == that.officeInfectionsWorker &&
                officeInfectionsVisitor == that.officeInfectionsVisitor &&
                restaurantInfectionsWorker == that.restaurantInfectionsWorker &&
                restaurantInfectionsVisitor == that.restaurantInfectionsVisitor &&
                schoolInfectionsWorker == that.schoolInfectionsWorker &&
                schoolInfectionsVisitor == that.schoolInfectionsVisitor &&
                shopInfectionsWorker == that.shopInfectionsWorker &&
                shopInfectionsVisitor == that.shopInfectionsVisitor &&
                adultInfected == that.adultInfected &&
                pensionerInfected == that.pensionerInfected &&
                childInfected == that.childInfected &&
                infantInfected == that.infantInfected &&
                adultDeaths == that.adultDeaths &&
                pensionerDeaths == that.pensionerDeaths &&
                childDeaths == that.childDeaths &&
                infantDeaths == that.infantDeaths;
    }
    
    public int getSeedInfections() { return seedInfections; }

    public void incSeedInfections() { seedInfections++; }

    public void incDeathsAdult() { adultDeaths++; }

    public void incDeathsChild() {
        childDeaths++;
    }

    public void incDeathsInfant() {
        infantDeaths++;
    }

    public void incDeathsPensioner() {
        pensionerDeaths++;
    }

    public void incInfectionsAdult() {
        adultInfected++;
    }

    public void incInfectionsChild() {
        childInfected++;
    }

    public void incInfectionsPensioner() {
        pensionerInfected++;
    }

    public void incInfectionsInfant() {
        infantInfected++;
    }

    public void incInfectionsConstructionSiteVisitor() {
        constructionSiteInfectionsVisitor++;
    }

    public void incInfectionsOfficeVisitor() {
        officeInfectionsVisitor++;
    }

    public void incInfectionsHospitalVisitor() {
        hospitalInfectionsVisitor++;
    }

    public void incInfectionsSchoolVisitor() {
        schoolInfectionsVisitor++;
    }
    
    public void incInfectionsRestaurantVisitor() {
        restaurantInfectionsVisitor++;
    }
    
    public void incInfectionsShopVisitor() {
        shopInfectionsVisitor++;
    }

    public void incInfectionsNurseryVisitor() {
        nurseryInfectionsVisitor++;
    }

    public void incInfectionsHomeVisitor() {
        homeInfectionsVisitor++;
    }

    public void incInfectionConstructionSiteWorker() {
        constructionSiteInfectionsWorker++;
    }

    public void incInfectionOfficeWorker() {
        officeInfectionsWorker++;
    }

    public void incInfectionHospitalWorker() {
        hospitalInfectionsWorker++;
    }

    public void incInfectionsSchoolWorker() {
        schoolInfectionsWorker++;
    }

    public void incInfectionsRestaurantWorker() {
        restaurantInfectionsWorker++;
    }

    public void incInfectionsShopWorker() {
        shopInfectionsWorker++;
    }

    public void incInfectionsNurseryWorker() {
        nurseryInfectionsWorker++;
    }

    public void incInfectionsHomeInhabitant() {
        homeInfectionsInhabitant++;
    }

    public void determineRValues(Population p) {
        RStats rs = new RStats(p);
        secInfections = rs.getSecInfections(day);
        generationTime = rs.getMeanGenerationTime(day);
    }

    public void incInfectionCareHomeWorker() {
        careHomeInfectionsWorker++;
    }

    public void incInfectionCareHomeResident() {
        careHomeInfectionsResident++;
    }
}
