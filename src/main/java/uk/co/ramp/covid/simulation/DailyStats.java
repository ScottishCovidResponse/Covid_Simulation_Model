package uk.co.ramp.covid.simulation;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.population.*;

/** DailyStatis accumluates statistics, e.g. healthy/dead, for a particular day */
import java.io.IOException;
import java.util.Objects;

public class DailyStats {
    private static final Logger LOGGER = LogManager.getLogger(DailyStats.class);

    private int day;

    // Daily cummulative statistics
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
    private Double rnaught = null;
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

    public int getConstructionSiteInfectionsWorker() {
        return constructionSiteInfectionsWorker;
    }

    public void log(){
        LOGGER.info("Day = {} Healthy = {} Latent = {} Asymptomatic = {} Phase 1 = {} Phase 2 = {} Dead = {} Recovered = {}",
                day, healthy, exposed, asymptomatic,phase1, phase2, dead, recovered);
    }

    public void appendCSV(CSVPrinter csv, int iter) throws IOException {
        csv.printRecord(iter, day, healthy, exposed, asymptomatic,
                phase1, phase2, dead, recovered,
                constructionSiteInfectionsWorker, hospitalInfectionsWorker, 
                nurseryInfectionsWorker, officeInfectionsWorker, restaurantInfectionsWorker, schoolInfectionsWorker, 
                shopInfectionsWorker, homeInfectionsInhabitant,
                constructionSiteInfectionsVisitor, hospitalInfectionsVisitor, nurseryInfectionsVisitor, 
                officeInfectionsVisitor, restaurantInfectionsVisitor, schoolInfectionsVisitor, shopInfectionsVisitor,
                homeInfectionsVisitor, adultInfected, pensionerInfected, childInfected, infantInfected, adultDeaths,
                pensionerDeaths, childDeaths, infantDeaths, rnaught, generationTime);
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
                homeInfectionsInhabitant +
                homeInfectionsVisitor;
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

    public void incDeathsAdult() {
        adultDeaths++;
    }

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
        rnaught = rs.getMeanR(day);
        generationTime = rs.getMeanGenerationTime(day);
    }
}
