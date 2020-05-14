package uk.co.ramp.covid.simulation;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.place.*;
import uk.co.ramp.covid.simulation.population.Person;

/** DailyStatis accumluates statistics, e.g. healthy/dead, for a particular day */
import java.io.IOException;

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
            default: LOGGER.info("Invalid Status"); break;
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
                homeInfections);
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

    public void infectedPlace(CommunalPlace p) {
        if (p instanceof ConstructionSite) {
           constructionSiteInfections++;
        }
        else if (p instanceof Hospital) {
            hospitalInfections++;
        }
        else if (p instanceof Nursery) {
            nurseryInfections++;
        }
        else if (p instanceof Office) {
            officeInfections++;
        }
        else if (p instanceof Restaurant) {
            restaurantInfections++;
        }
        else if (p instanceof School) {
            schoolInfections++;
        }
        else if (p instanceof Shop) {
            shopInfections++;
        }
    }

    public void infectedHome() {
        homeInfections++;
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
}
