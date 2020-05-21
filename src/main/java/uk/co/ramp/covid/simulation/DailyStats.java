package uk.co.ramp.covid.simulation;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.place.*;
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
    private int homeInfections = 0;
    private int constructionSiteInfections = 0;
    private int hospitalInfections = 0;
    private int nurseryInfections = 0;
    private int officeInfections = 0;
    private int restaurantInfections  = 0;
    private int schoolInfections = 0;
    private int shopInfections = 0;

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

    public DailyStats(int day) {
        this.day = day;
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

    public void log(){
        LOGGER.info("Day = {} Healthy = {} Latent = {} Asymptomatic = {} Phase 1 = {} Phase 2 = {} Dead = {} Recovered = {}",
                day, healthy, exposed, asymptomatic,phase1, phase2, dead, recovered);
    }

    public void appendCSV(CSVPrinter csv, int iter) throws IOException {
        csv.printRecord(iter, day, healthy, exposed, asymptomatic,
                phase1, phase2, dead, recovered, constructionSiteInfections,
                hospitalInfections, nurseryInfections, officeInfections,
                restaurantInfections, schoolInfections, shopInfections,
                homeInfections,
                adultInfected, pensionerInfected, childInfected, infantInfected,
                adultDeaths, pensionerDeaths, childDeaths, infantDeaths);
    }

    public int getTotalDailyInfections () {
        return constructionSiteInfections +
                hospitalInfections +
                nurseryInfections +
                officeInfections +
                restaurantInfections +
                schoolInfections +
                shopInfections +
                homeInfections;
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

    public int getHomeInfections() {
        return homeInfections;
    }

    public int getConstructionSiteInfections() {
        return constructionSiteInfections;
    }

    public int getHospitalInfections() {
        return hospitalInfections;
    }

    public int getNurseryInfections() {
        return nurseryInfections;
    }

    public int getOfficeInfections() {
        return officeInfections;
    }

    public int getRestaurantInfections() {
        return restaurantInfections;
    }

    public int getSchoolInfections() {
        return schoolInfections;
    }

    public int getShopInfections() {
        return shopInfections;
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
                homeInfections == that.homeInfections &&
                constructionSiteInfections == that.constructionSiteInfections &&
                hospitalInfections == that.hospitalInfections &&
                nurseryInfections == that.nurseryInfections &&
                officeInfections == that.officeInfections &&
                restaurantInfections == that.restaurantInfections &&
                schoolInfections == that.schoolInfections &&
                shopInfections == that.shopInfections &&
                adultInfected == that.adultInfected &&
                pensionerInfected == that.pensionerInfected &&
                childInfected == that.childInfected &&
                infantInfected == that.infantInfected &&
                adultDeaths == that.adultDeaths &&
                pensionerDeaths == that.pensionerDeaths &&
                childDeaths == that.childDeaths &&
                infantDeaths == that.infantDeaths;
    }

    public void incInfectionConstructionSite() {
        constructionSiteInfections++;
    }

    public void incInfectionOffice() {
        officeInfections++;
    }

    public void incInfectionHospital() {
        hospitalInfections++;
    }

    public void incInfectionsSchool() {
        schoolInfections++;
    }
    
    public void incInfectionsRestaurant() {
        restaurantInfections++;
    }
    
    public void incInfectionsShop() {
        shopInfections++;
    }

    public void incInfectionsNursery() {
        nurseryInfections++;
    }

    public void incInfectionsAdult() {
        adultInfected++;
    }

    public void incInfectionsChild() {
        childInfected++;
    }

    public void incInfectionsInfant() {
        infantInfected++;
    }

    public void incInfectionsPensioner() {
        pensionerInfected++;
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

    public void incInfectionsHome() {
        homeInfections++;
    }

}
