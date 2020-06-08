package uk.co.ramp.covid.simulation.parameters;

public class BuildingProperties {
    public Double pBaseTrans = null;
    public Double pHospitalTrans = null;
    public Double pConstructionSiteTrans = null;
    public Double pNurseryTrans = null;
    public Double pOfficeTrans = null;
    public Double pRestaurantTrans = null;
    public Double pSchoolTrans = null;
    public Double pShopTrans = null;

    public Double pHospitalKey = null;
    public Double pConstructionSiteKey = null;
    public Double pOfficeKey = null;
    public Double pShopKey = null;

    public Double pLeaveShop = null;
    public Double pLeaveRestaurant = null;

    public boolean isValid() {
        /*
        return PopulationParameters.isValidProbability(pBaseTrans, "pBaseTrans")
                && PopulationParameters.isValidProbability(pHospitalKey, "pHospitalKey")
                && PopulationParameters.isValidProbability(pConstructionSiteKey, "pConstructionSiteKey")
                && PopulationParameters.isValidProbability(pOfficeKey, "pOfficeKey")
                && PopulationParameters.isValidProbability(pShopKey, "pShopKey")
                && PopulationParameters.isValidProbability(pLeaveShop, "pLeaveShop")
                && PopulationParameters.isValidProbability(pLeaveRestaurant, "pLeaveRestaurant");
    }
    */
        return true;
    }
}
