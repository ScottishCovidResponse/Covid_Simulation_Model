package uk.co.ramp.covid.simulation.population;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.lang.reflect.Field;
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

    // Proportions of each type of person in the population
    private static class Population {
        public Double pInfants = null;
        public Double pChildren = null;
        public Double pAdults = null;
        public Double pPensioners = null;

        @Override
        public String toString() {
            return "Population{" +
                    "pInfants=" + pInfants +
                    ", pChildren=" + pChildren +
                    ", pAdults=" + pAdults +
                    ", pPensioners=" + pPensioners +
                    '}';
        }
    }

    // Household populations
    // These values define the probability of a household being an adult only, adult and child household etc
    private static class Households {
        public Double pAdultOnly = null;
        public Double pPensionerOnly = null;
        public Double pPensionerAdult = null;
        public Double pAdultChildren = null;
        public Double pPensionerChildren = null;
        public Double pAdultPensionerChildren = null;

        @Override
        public String toString() {
            return "Households{" +
                    "pAdultOnly=" + pAdultOnly +
                    ", pPensionerOnly=" + pPensionerOnly +
                    ", pPensionerAdult=" + pPensionerAdult +
                    ", pAdultChildren=" + pAdultChildren +
                    ", pPensionerChildren=" + pPensionerChildren +
                    ", pAdultPensionerChildren=" + pAdultPensionerChildren +
                    '}';
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

    // Defines the number of types of building per N people
    private static class BuildingDistribution {
        public Integer hospitals = null;
        public Integer schools = null;
        public Integer shops = null;
        public Integer offices = null;
        public Integer constructionSites = null;
        public Integer nurseries = null;
        public Integer restaurants = null;

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
                    '}';
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
                    '}';
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
    }
    
    private static class HouseholdProperties {
        public Double visitorLeaveRate = null;
        public Double neighbourVisitFreq = null;
        public Integer expectedNeighbours = null;

        @Override
        public String toString() {
            return "HouseholdProperties{" +
                    "visitorLeaveRate=" + visitorLeaveRate +
                    ", neighbourVisitFreq=" + neighbourVisitFreq +
                    ", expectedNeighbours=" + expectedNeighbours +
                    '}';
        }
    }

    private final Population population;
    private final Households households;
    private final AdditionalMembersDistributions additionalMembersDistributions;
    private final BuildingDistribution buildingDistribution;
    private final WorkerAllocation workerAllocation;
    private final BuildingProperties buildingProperties;
    private final InfantAllocation infantAllocation;
    private final PersonProperties personProperties;
    private final HouseholdProperties householdProperties;

    private PopulationParameters() {
        population = new Population();
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
        valid = valid && checker.isValid(households);
        valid = valid && checker.isValid(additionalMembersDistributions);
        valid = valid && checker.isValid(buildingDistribution);
        valid = valid && checker.isValid(workerAllocation);
        valid = valid && checker.isValid(buildingProperties);
        valid = valid && checker.isValid(infantAllocation);
        valid = valid && checker.isValid(personProperties);
        valid = valid && checker.isValid(householdProperties);
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

    // Population distribution
    public double getpInfants() {
        return population.pInfants;
    }

    public double getpChildren() {
        return population.pChildren;
    }

    public double getpAdults() {
        return population.pAdults;
    }

    public double getpPensioners() {
        return population.pPensioners;
    }

    // Household allocation parameters
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

    public int getSchoolsRatio() {
        return buildingDistribution.schools;
    }

    public int getShopsRatio() {
        return buildingDistribution.shops;
    }

    public int getOfficesRatio() {
        return buildingDistribution.offices;
    }

    public int getConstructionSiteRatio() {
        return buildingDistribution.constructionSites;
    }

    public int getNurseriesRatio() {
        return buildingDistribution.nurseries;
    }

    public int getRestaurantRatio() {
        return buildingDistribution.restaurants;
    }

    // Worker job assignment probabilities
    public double getpOfficeWorker() {
        return workerAllocation.pOffice;
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
