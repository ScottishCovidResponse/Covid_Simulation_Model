package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class BuildingProperties {
    // TODO: These are now rates, not probabilities
    public Double pBaseTrans = null;
    public Double pHospitalTrans = null;
    public Double pConstructionSiteTrans = null;
    public Double pNurseryTrans = null;
    public Double pOfficeTrans = null;
    public Double pRestaurantTrans = null;
    public Double pSchoolTrans = null;
    public Double pShopTrans = null;

    public Probability pHospitalKey = null;
    public Probability pConstructionSiteKey = null;
    public Probability pOfficeKey = null;
    public Probability pShopKey = null;

    public Probability pLeaveShop = null;
    public Probability pLeaveRestaurant = null;

    public boolean isValid() {
        return true;
    }
}
