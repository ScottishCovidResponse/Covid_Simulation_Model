package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.DateRange;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingProperties that = (BuildingProperties) o;
        return Objects.equals(baseTransmissionConstant, that.baseTransmissionConstant) &&
                Objects.equals(hospitalExpectedInteractionsPerHour, that.hospitalExpectedInteractionsPerHour) &&
                Objects.equals(constructionSiteExpectedInteractionsPerHour, that.constructionSiteExpectedInteractionsPerHour) &&
                Objects.equals(nurseryExpectedInteractionsPerHour, that.nurseryExpectedInteractionsPerHour) &&
                Objects.equals(officeExpectedInteractionsPerHour, that.officeExpectedInteractionsPerHour) &&
                Objects.equals(restaurantExpectedInteractionsPerHour, that.restaurantExpectedInteractionsPerHour) &&
                Objects.equals(schoolExpectedInteractionsPerHour, that.schoolExpectedInteractionsPerHour) &&
                Objects.equals(shopExpectedInteractionsPerHour, that.shopExpectedInteractionsPerHour) &&
                Objects.equals(careHomeExpectedInteractionsPerHour, that.careHomeExpectedInteractionsPerHour) &&
                Objects.equals(pHospitalKey, that.pHospitalKey) &&
                Objects.equals(pConstructionSiteKey, that.pConstructionSiteKey) &&
                Objects.equals(pOfficeKey, that.pOfficeKey) &&
                Objects.equals(pShopKey, that.pShopKey) &&
                Objects.equals(pLeaveShopHour, that.pLeaveShopHour) &&
                Objects.equals(pLeaveRestaurantHour, that.pLeaveRestaurantHour) &&
                Objects.equals(pHospitalStaffWillFurlough, that.pHospitalStaffWillFurlough) &&
                Objects.equals(schoolHolidays, that.schoolHolidays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseTransmissionConstant, hospitalExpectedInteractionsPerHour, constructionSiteExpectedInteractionsPerHour, nurseryExpectedInteractionsPerHour, officeExpectedInteractionsPerHour, restaurantExpectedInteractionsPerHour, schoolExpectedInteractionsPerHour, shopExpectedInteractionsPerHour, careHomeExpectedInteractionsPerHour, pHospitalKey, pConstructionSiteKey, pOfficeKey, pShopKey, pLeaveShopHour, pLeaveRestaurantHour, pHospitalStaffWillFurlough, schoolHolidays);
    }
}
