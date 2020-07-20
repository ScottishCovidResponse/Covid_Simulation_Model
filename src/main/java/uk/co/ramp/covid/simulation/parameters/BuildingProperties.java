package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.DateRange;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

public class BuildingProperties {
    /** transmission constant for all places - adjusted based on number of expected interactions and number of people */
    public Double baseTransmissionConstant = null;
    public Double hospitalExpectedInteractionsPerHour = null;
    public Double constructionSiteExpectedInteractionsPerHour = null;
    public Double nurseryExpectedInteractionsPerHour = null;
    public Double officeExpectedInteractionsPerHour = null;
    public Double restaurantExpectedInteractionsPerHour = null;
    public Double schoolExpectedInteractionsPerHour = null;
    public Double shopExpectedInteractionsPerHour = null;
    public Double careHomeExpectedInteractionsPerHour = null;

    /** Probability a place is designated "key" and does not close during lockdown */
    public Probability pHospitalKey = null;
    public Probability pConstructionSiteKey = null;
    public Probability pOfficeKey = null;
    public Probability pShopKey = null;

    /** Hourly probability a family leaves a show/restaurant */
    public Probability pLeaveShopHour = null;
    public Probability pLeaveRestaurantHour = null;

    /** Probabilty a staff member of a non-COVID hospital stops going to work during lockdown */
    public Probability pHospitalStaffWillFurlough = null;

    /** Dates when schools are closed */
    public List<DateRange> schoolHolidays = null;
    
    public boolean isValid() {
        return hospitalExpectedInteractionsPerHour >= 0
            && constructionSiteExpectedInteractionsPerHour >= 0
            && nurseryExpectedInteractionsPerHour >= 0
            && officeExpectedInteractionsPerHour >= 0
            && restaurantExpectedInteractionsPerHour >= 0
            && schoolExpectedInteractionsPerHour >= 0
            && shopExpectedInteractionsPerHour >= 0
            && careHomeExpectedInteractionsPerHour >= 0;
    }
}
