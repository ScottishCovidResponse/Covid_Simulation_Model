package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.DateRange;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

public class BuildingProperties {
    public Double baseTransmissionConstant = null;
    public Double hospitalExpectedInteractionsPerHour = null;
    public Double constructionSiteExpectedInteractionsPerHour = null;
    public Double nurseryExpectedInteractionsPerHour = null;
    public Double officeExpectedInteractionsPerHour = null;
    public Double restaurantExpectedInteractionsPerHour = null;
    public Double schoolExpectedInteractionsPerHour = null;
    public Double shopExpectedInteractionsPerHour = null;
    public Double careHomeExpectedInteractionsPerHour = null;

    public Probability pHospitalKey = null;
    public Probability pConstructionSiteKey = null;
    public Probability pOfficeKey = null;
    public Probability pShopKey = null;

    public Probability pLeaveShop = null;
    public Probability pLeaveRestaurant = null;
    public Probability pHospitalStaffWillFurlough = null;
    public List<DateRange> schoolHolidays = null;
}
