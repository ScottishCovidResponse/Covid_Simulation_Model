package uk.co.ramp.covid.simulation.population;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * PopulationParameters is a singleton class for reading and storing the population parameters
 * used to perform allocation (to household etc).
 *
 * Note: This use of the singleton pattern is not thread safe
 */
public class PopulationParameters {
    private static final Logger LOGGER = LogManager.getLogger(PopulationParameters.class);
    private static PopulationParameters pp = null;
    private static final double EPSILON = 0.001;

    // Household populations
    // These values define the probability of a household being an adult only, adult and child household etc
    private static class Households {
        public Double householdRatio = null;

        public Double pAdultOnly = null;
        public Double pPensionerOnly = null;
        public Double pPensionerAdult = null;
        public Double pAdultChildren = null;
        public Double pPensionerChildren = null;
        public Double pAdultPensionerChildren = null;

        @Override
        public String toString() {
            return "Households{" +
                    "householdRatio=" + householdRatio +
                    ", pAdultOnly=" + pAdultOnly +
                    ", pPensionerOnly=" + pPensionerOnly +
                    ", pPensionerAdult=" + pPensionerAdult +
                    ", pAdultChildren=" + pAdultChildren +
                    ", pPensionerChildren=" + pPensionerChildren +
                    ", pAdultPensionerChildren=" + pAdultPensionerChildren +
                    '}';
        }

        public boolean isValid() {
            boolean probabilitiesValid = isValidProbability(pAdultOnly, "pAdultOnly")
                    && isValidProbability(pPensionerOnly, "pPensionerOnly")
                    && isValidProbability(pAdultChildren, "pAdultChildren")
                    && isValidProbability(pAdultPensionerChildren, "pAdultPensionerChildren")
                    && isValidProbability(pPensionerChildren, "pPensionerChildren")
                    && isValidProbability(pPensionerAdult, "pPensionerAdult");

            probabilitiesValid = probabilitiesValid && (householdRatio >= 1);

            double totalP = pAdultOnly + pPensionerAdult + pPensionerOnly + pAdultChildren
                    + pPensionerChildren + pAdultPensionerChildren;
            if(!(totalP <= 1 + EPSILON && totalP >= 1 - EPSILON)) {
                LOGGER.error("Household parameter probabilities do not total one");
                return false;
            }
            return probabilitiesValid;
        }

    }

    // Household allocation probabilities based on household size and type
    private static class AdditionalMembersDistributions {
        public Map<Integer, Double> adultAllocationPMap = null;
        public Map<Integer, Double> pensionerAllocationPMap = null;
        public Map<Integer, Double> childAllocationPMap = null;
        public Map<Integer, Double> infantAllocationPMap = null;

        public AdditionalMembersDistributions() {}

        @Override
        public String toString() {
            return "AdditionalMembersDistributions{" +
                    "adultAllocationPMap=" + adultAllocationPMap +
                    ", pensionerAllocationPMap=" + pensionerAllocationPMap +
                    ", childAllocationPMap=" + childAllocationPMap +
                    ", infantAllocationPMap=" + infantAllocationPMap +
                    '}';
        }

    }

    private static class Size {
        public Double pSmall = null;
        public Double pMed = null;
        public Double pLarge = null;

        public boolean isValid(String name) {
            double totalP = pSmall + pMed + pLarge;
            if (!(totalP <= 1 + EPSILON && totalP >= 1 - EPSILON)) {
                LOGGER.error("Building size parameters for " + name + " do not total one");
                return false;
            }
            return true;
        }
    }


    // Defines the number of types of building per N people
    private static class BuildingDistribution {
        public Integer hospitals = null;
        public Size hospitalSizes = null;

        public Integer schools = null;
        public Size schoolSizes = null;

        public Integer shops = null;
        public Size shopSizes = null;

        public Integer offices = null;
        public Size officeSizes = null;

        public Integer constructionSites = null;
        public Size constructionSiteSizes = null;

        public Integer nurseries = null;
        public Size nurserySizes = null;

        public Integer restaurants = null;
        public Size restaurantSizes = null;

        @Override
        public String toString() {
            return "BuildingDistribution{" +
                    "hospitals=" + hospitals +
                    ", schools=" + schools +
                    ", shops=" + shops +
                    ", offices=" + offices +
                    ", constructionSites=" + constructionSites +
                    ", nurseries=" + nurseries +
                    ", restaurants=" + restaurants +
                    '}';
        }

        public boolean isValid() {
            return hospitalSizes.isValid("hospital")
                    && schoolSizes.isValid("school")
                    && shopSizes.isValid("shop")
                    && officeSizes.isValid("office")
                    && constructionSiteSizes.isValid("construction site")
                    && nurserySizes.isValid("nurseries")
                    && restaurantSizes.isValid("restaurant");
        }
    }

    // Probability an Adult works in a particular job
    private static class WorkerAllocation {
        public Double pOffice = null;
        public Double pShop = null;
        public Double pHospital = null;
        public Double pConstruction = null;
        public Double pTeacher = null;
        public Double pRestaurant = null;
        public Double pUnemployed = null;
        public Double pNursery = null;

        public Size sizeAllocation = null;

        @Override
        public String toString() {
            return "WorkerAllocation{" +
                    "pOffice=" + pOffice +
                    ", pShop=" + pShop +
                    ", pHospital=" + pHospital +
                    ", pConstruction=" + pConstruction +
                    ", pTeacher=" + pTeacher +
                    ", pRestaurant=" + pRestaurant +
                    ", pUnemployed=" + pUnemployed +
                    ", pNursery=" + pNursery +
                    ", sizeAllocation=" + sizeAllocation +
                    '}';
        }

        public boolean isValid() {
            boolean probabilitiesValid = isValidProbability(pOffice, "pOffice")
                    && isValidProbability(pShop, "pShop")
                    && isValidProbability(pHospital, "pHospital")
                    && isValidProbability(pConstruction, "pConstruction")
                    && isValidProbability(pTeacher, "pTeacher")
                    && isValidProbability(pRestaurant, "pRestaurant")
                    && isValidProbability(pNursery, "pNursery")
                    && isValidProbability(pUnemployed, "pUnemployed");

            double totalP = pOffice + pShop + pHospital + pConstruction + pTeacher + pRestaurant + pNursery + pUnemployed;
            if(!(totalP <= 1 + EPSILON && totalP >= 1 - EPSILON)) {
                LOGGER.error("Worker allocation parameter probabilities do not total one");
                return false;
            }

            return probabilitiesValid;
        }
    }

    private static class BuildingProperties {
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

        @Override
        public String toString() {
            return "BuildingProperties{" +
                    "pBaseTrans =" + pBaseTrans +
                    ", pHospitalTrans=" + pHospitalTrans +
                    ", pConstructionSiteTrans=" + pConstructionSiteTrans +
                    ", pNurseryTrans=" + pNurseryTrans +
                    ", pOfficeTrans=" + pOfficeTrans +
                    ", pRestaurantTrans=" + pRestaurantTrans +
                    ", pSchoolTrans=" + pSchoolTrans +
                    ", pShopTrans=" + pShopTrans +
                    ", pHospitalKey=" + pHospitalKey +
                    ", pConstructionSiteKey=" + pConstructionSiteKey +
                    ", pOfficeKey=" + pOfficeKey +
                    ", pShopKey=" + pShopKey +
                    ", pLeaveShop =" + pLeaveShop +
                    ", pLeaveRestaurant =" + pLeaveRestaurant +
                    '}';
        }

        public boolean isValid() {
            return isValidProbability(pBaseTrans, "pBaseTrans")
                    && isValidProbability(pHospitalTrans, "pHospitalTrans")
                    && isValidProbability(pConstructionSiteTrans, "pConstructionSiteTrans")
                    && isValidProbability(pNurseryTrans, "pNurseryTrans")
                    && isValidProbability(pOfficeTrans, "pOfficeTrans")
                    && isValidProbability(pRestaurantTrans, "pRestaurantTrans")
                    && isValidProbability(pSchoolTrans, "pSchoolTrans")
                    && isValidProbability(pShopTrans, "pShopTrans")
                    && isValidProbability(pHospitalKey, "pHospitalKey")
                    && isValidProbability(pConstructionSiteKey, "pConstructionSiteKey")
                    && isValidProbability(pOfficeKey, "pOfficeKey")
                    && isValidProbability(pShopKey, "pShopKey")
                    && isValidProbability(pLeaveShop, "pLeaveShop")
                    && isValidProbability(pLeaveRestaurant, "pLeaveRestaurant");
        }
    }

    private static class InfantAllocation {
        public Double pAttendsNursery = null;

        @Override
        public String toString() {
            return "InfantAllocation{" +
                    "pAttendsNursery=" + pAttendsNursery +
                    '}';
        }

        public boolean isValid() {
            return isValidProbability(pAttendsNursery, "pAttendsNursery");
        }


    }

    private static class PersonProperties {
        public Double pQuarantine = null;
        public Double pTransmission = null;

        @Override
        public String toString() {
            return "PersonProperties{" +
                    "pQuarantine=" + pQuarantine +
                    ", pTransmission=" + pTransmission +
                    '}';
        }

        public boolean isValid() {
            return isValidProbability(pQuarantine, "pQuarantine")
                    && isValidProbability(pTransmission, "pTransmission");
        }
    }
    
    private static class HouseholdProperties {
        public Double visitorLeaveRate = null;
        public Double neighbourVisitFreq = null;
        public Integer expectedNeighbours = null;

        public Double pGoShopping = null;
        public Double pGoRestaurant = null;
        public Integer householdIsolationPeriod = null;
        public Double pWillIsolate = null;

        @Override
        public String toString() {
            return "HouseholdProperties{" +
                    "visitorLeaveRate=" + visitorLeaveRate +
                    ", neighbourVisitFreq=" + neighbourVisitFreq +
                    ", expectedNeighbours=" + expectedNeighbours +
                    ", pGoShopping=" + pGoShopping +
                    ", pGoRestaurant=" + pGoRestaurant +
                    ", householdIsolationPeriod=" + householdIsolationPeriod +
                    ", pWillIsolate=" + pWillIsolate +
                    '}';
        }

        public boolean isValid() {
            return isValidProbability(pGoShopping, "pGoShopping")
                    && isValidProbability(pGoRestaurant, "pGoRestaurant")
                    && isValidProbability(pWillIsolate, "pWillIsolate");
        }
    }

    private final Map<String,Double> population;
    private final Households households;
    private final AdditionalMembersDistributions additionalMembersDistributions;
    private final BuildingDistribution buildingDistribution;
    private final WorkerAllocation workerAllocation;
    private final BuildingProperties buildingProperties;
    private final InfantAllocation infantAllocation;
    private final PersonProperties personProperties;
    private final HouseholdProperties householdProperties;

    private PopulationParameters() {
        population = new HashMap<>();
        households = new Households();
        additionalMembersDistributions = new AdditionalMembersDistributions();
        buildingDistribution = new BuildingDistribution();
        workerAllocation = new WorkerAllocation();
        buildingProperties = new BuildingProperties();
        infantAllocation = new InfantAllocation();
        personProperties = new PersonProperties();
        householdProperties = new HouseholdProperties();
    }

    public boolean isValid() {
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        boolean valid = true;
        // We don't do this in a single statement to ensure that all the "uninitalised" parameter warnings are printed
        // in one go instead of being short circuited
        valid = valid && checker.isValid(population);
        valid = valid && checker.isValid(households) && households.isValid();
        valid = valid && checker.isValid(additionalMembersDistributions);
        valid = valid && checker.isValid(buildingDistribution) && buildingDistribution.isValid();
        valid = valid && checker.isValid(workerAllocation) && workerAllocation.isValid();
        valid = valid && checker.isValid(buildingProperties) && buildingProperties.isValid();
        valid = valid && checker.isValid(infantAllocation) && infantAllocation.isValid();
        valid = valid && checker.isValid(personProperties) && personProperties.isValid();
        valid = valid && checker.isValid(householdProperties) && householdProperties.isValid();
        return valid;
    }

    public static PopulationParameters get() {
        if (pp == null) {
            throw new InvalidParametersException("Invalid population parameters");
        }
        return pp;
    }

    public static void setParameters(PopulationParameters p) {
        pp = p;
    }
    public static void clearParameters() {
        pp = null;
    }
    
    public Map<String, Double> getPopulation() { return population; }

    // Household allocation parameters

    public double getHouseholdRatio() { return households.householdRatio; }

    public void setHouseholdRatio(double r) { households.householdRatio = r; }

    public double getpAdultOnly() {
        return households.pAdultOnly;
    }

    public double getpPensionerOnly() {
        return households.pPensionerOnly;
    }

    public double getpPensionerAdult() {
        return households.pPensionerAdult;
    }

    public double getpAdultChildren() {
        return households.pAdultChildren;
    }

    public double getpPensionerChildren() {
        return households.pPensionerChildren;
    }

    public double getpAdultPensionerChildren() {
        return households.pAdultPensionerChildren;
    }

    public Map<Integer, Double> getAdultAllocationPMap() {
        return additionalMembersDistributions.adultAllocationPMap;
    }

    public Map<Integer, Double> getPensionerAllocationPMap() {
        return additionalMembersDistributions.pensionerAllocationPMap;
    }

    public Map<Integer, Double> getChildAllocationPMap() {
        return additionalMembersDistributions.childAllocationPMap;
    }

    public Map<Integer, Double> getInfantAllocationPMap() {
        return additionalMembersDistributions.infantAllocationPMap;
    }

    // Number of buildings of a particular type
    public int getHospitalRatio() {
        return buildingDistribution.hospitals;
    }

    public double getpHospitalSmall() {
        return buildingDistribution.hospitalSizes.pSmall;
    }

    public double getpHospitalMed() {
        return buildingDistribution.hospitalSizes.pMed;
    }

    public double getpHospitalLarge() {
        return buildingDistribution.hospitalSizes.pLarge;
    }

    public int getSchoolsRatio() {
        return buildingDistribution.schools;
    }

    public double getpSchoolSmall() {
        return buildingDistribution.schoolSizes.pSmall;
    }

    public double getpSchoolMed() {
        return buildingDistribution.schoolSizes.pMed;
    }

    public double getpSchoolLarge() {
        return buildingDistribution.schoolSizes.pLarge;
    }

    public int getShopsRatio() {
        return buildingDistribution.shops;
    }

    public double getpShopSmall() {
        return buildingDistribution.shopSizes.pSmall;
    }

    public double getpShopMed() {
        return buildingDistribution.shopSizes.pMed;
    }

    public double getpShopLarge() {
        return buildingDistribution.shopSizes.pLarge;
    }

    public int getOfficesRatio() {
        return buildingDistribution.offices;
    }

    public double getpOfficeSmall() {
        return buildingDistribution.officeSizes.pSmall;
    }

    public double getpOfficeMed() {
        return buildingDistribution.officeSizes.pMed;
    }

    public double getpOfficeLarge() {
        return buildingDistribution.officeSizes.pLarge;
    }

    public int getConstructionSiteRatio() {
        return buildingDistribution.constructionSites;
    }

    public double getpConstructionSiteSmall() {
        return buildingDistribution.constructionSiteSizes.pSmall;
    }

    public double getpConstructionSiteMed() {
        return buildingDistribution.constructionSiteSizes.pMed;
    }

    public double getpConstructionSiteLarge() {
        return buildingDistribution.constructionSiteSizes.pLarge;
    }

    public int getNurseriesRatio() {
        return buildingDistribution.nurseries;
    }

    public double getpNurserySmall() {
        return buildingDistribution.nurserySizes.pSmall;
    }

    public double getpNurseryMed() {
        return buildingDistribution.nurserySizes.pMed;
    }

    public double getpNurseryLarge() {
        return buildingDistribution.nurserySizes.pLarge;
    }

    public int getRestaurantRatio() {
        return buildingDistribution.restaurants;
    }

    public double getpRestaurantSmall() {
        return buildingDistribution.restaurantSizes.pSmall;
    }

    public double getpRestaurantMed() {
        return buildingDistribution.restaurantSizes.pMed;
    }

    public double getpRestaurantLarge() {
        return buildingDistribution.restaurantSizes.pLarge;
    }

    // Worker job assignment probabilities
    public double getpOfficeWorker() {
        return workerAllocation.pOffice;
    }

    public double getpAllocateSmall() {
        return workerAllocation.sizeAllocation.pSmall;
    }
    public double getpAllocateMed() {
        return workerAllocation.sizeAllocation.pMed;
    }
    public double getpAllocateLarge() {
        return workerAllocation.sizeAllocation.pLarge;
    }

    public double getpShopWorker() {
        return workerAllocation.pShop;
    }

    public double getpHospitalWorker() {
        return workerAllocation.pHospital;
    }

    public double getpConstructionWorker() {
        return workerAllocation.pConstruction;
    }

    public double getpTeacher() {
        return workerAllocation.pTeacher;
    }

    public double getpRestaurantWorker() {
        return workerAllocation.pRestaurant;
    }

    public double getpNurseryWorker() { return workerAllocation.pNursery; }

    public double getpUnemployed() {
        return workerAllocation.pUnemployed;
    }

    // Building Properties
    public double getpBaseTrans() {
        return buildingProperties.pBaseTrans;
    }

    public double getpHospitalTrans () {
       return buildingProperties.pHospitalTrans;
    }

    public double getpConstructionSiteTrans () {
       return buildingProperties.pConstructionSiteTrans;
    }

    public double getpNurseryTrans () {
       return buildingProperties.pNurseryTrans;
    }

    public double getpOfficeTrans () {
       return buildingProperties.pOfficeTrans;
    }

    public double getpRestaurantTrans () {
       return buildingProperties.pRestaurantTrans;
    }

    public double getpSchoolTrans () {
       return buildingProperties.pSchoolTrans;
    }

    public double getpShopTrans () {
       return buildingProperties.pShopTrans;
    }

    public double getpHospitalKey () {
        return buildingProperties.pHospitalKey;
    }

    public double getpConstructionSiteKey () {
        return buildingProperties.pConstructionSiteKey;
    }

    public double getpOfficeKey () {
        return buildingProperties.pOfficeKey;
    }

    public double getpShopKey () {
        return buildingProperties.pShopKey;
    }

    public double getpLeaveShop () {
        return buildingProperties.pLeaveShop;
    }

    public void setpLeaveShop (double p) {
        buildingProperties.pLeaveShop = p;
    }

    public double getpLeaveRestaurant () {
        return buildingProperties.pLeaveRestaurant;
    }

    public void setpLeaveRestaurant (double p) {
        buildingProperties.pLeaveRestaurant = p;
    }

    // Infant allocation
    public double getpAttendsNursery() {
        return infantAllocation.pAttendsNursery;
    }

    // Household properties
    public double getNeighbourVisitFreq() {
        return householdProperties.neighbourVisitFreq;
    }
    public int getExpectedNeighbours() {
        return householdProperties.expectedNeighbours;
    }
    public double getHouseholdVisitorLeaveRate() { return householdProperties.visitorLeaveRate; }
    public void setHouseholdVisitorLeaveRate(double p) { householdProperties.visitorLeaveRate = p; }

    public double getpGoShopping() {
        return householdProperties.pGoShopping;
    }
    public double getpGoRestaurant() {
        return householdProperties.pGoRestaurant;
    }
    
    public int getHouseholdIsolationPeriod() { return householdProperties.householdIsolationPeriod; }
    public Double getpHouseholdWillIsolate() { return householdProperties.pWillIsolate; }
    public void setpHouseholdWillIsolate(Double p) { householdProperties.pWillIsolate = p; }



    // Person Properties
    public double getpQuarantine() {
        return personProperties.pQuarantine;
    }

    public double getpTransmission() {
        return personProperties.pTransmission;
    }

    @Override
    public String toString() {
        return "PopulationParameters{" + "\n" +
                population + "\n" +
                households + "\n" +
                additionalMembersDistributions + "\n" +
                buildingDistribution + "\n" +
                workerAllocation + "\n" +
                buildingProperties + "\n" +
                infantAllocation + "\n" +
                householdProperties + "\n" +
                personProperties + "\n" +
                '}';
    }
    
    private static boolean isValidProbability(Double val, String name) {
        if(val < 0 || val > 1) {
            LOGGER.error(name + " is not a valid probability");
            return false;
        }
        return true;
    }

    public class ParameterInitialisedChecker {

        public boolean isValid (Object o) {
            try {
                return fieldsValid(o);
            } catch (IllegalAccessException e) {
                LOGGER.warn(e);
            }
            return false;
        }

        private boolean fieldsValid (Object o) throws IllegalAccessException {
            boolean res = true;
            for (Field f : o.getClass().getFields()) {
                if (f.get(o) == null) {
                    LOGGER.warn("Uninitialised parameter: " + f.getName());
                    res = false;
                }
            }
            return res;
        }
    }
}
