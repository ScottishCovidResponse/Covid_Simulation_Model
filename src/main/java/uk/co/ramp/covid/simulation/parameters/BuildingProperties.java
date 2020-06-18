package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.DateRange;
import uk.co.ramp.covid.simulation.util.Probability;

import java.util.List;

public class BuildingProperties {
    public Double baseTransmissionConstant = null;
    public Double hospitalTransmissionConstant = null;
    public Double constructionSiteTransmissionConstant = null;
    public Double nurseryTransmissionConstant = null;
    public Double officeTransmissionConstant = null;
    public Double restaurantTransmissionConstant = null;
    public Double schoolTransmissionConstant = null;
    public Double shopTransmissionConstant = null;
    public Double careHomeTransmissionConstant = null;

    public Probability pHospitalKey = null;
    public Probability pConstructionSiteKey = null;
    public Probability pOfficeKey = null;
    public Probability pShopKey = null;

    public Probability pLeaveShop = null;
    public Probability pLeaveRestaurant = null;
    public Probability pHospitalStaffWillFurlough = null;
    public List<DateRange> schoolHolidays = null;
}
