package uk.co.ramp.covid.simulation.population;

import java.util.HashMap;
import java.util.Map;

/**
 * PopulationParameters is a singleton class for reading and storing the population parameters
 * used to perform allocation (to household etc).
 *
 * Note: This use of the singleton pattern is not thread safe
 */
public class PopulationParameters {
    private static PopulationParameters pp = null;

    // Proportions of each type of person in the population
    private static class Population {
        public double pInfants = 0.08;
        public double pChildren = 0.2;
        public double pAdults = 0.5;
        public double pPensioners = 0.22;

        public Population() {}

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
        public double pAdultOnly = 0.3;
        public double pPensionerOnly = 0.1;
        public double pPensionerAdult = 0.1;
        public double pAdultChildren = 0.3;
        public double pPensionerChildren = 0.1;
        public double pAdultPensionerChildren = 0.1;

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
        public Map<Integer, Double> adultAllocationPMap;
        public Map<Integer, Double> pensionerAllocationPMap;
        public Map<Integer, Double> childAllocationPMap;
        public Map<Integer, Double> infantAllocationPMap;

        public AdditionalMembersDistributions() {
            // Default parameters
            adultAllocationPMap = new HashMap<>();
            adultAllocationPMap.put(1, 0.8);
            adultAllocationPMap.put(2, 0.5);
            adultAllocationPMap.put(3, 0.3);
            adultAllocationPMap.put(4, 0.2);
            adultAllocationPMap.put(5, 0.1);

            pensionerAllocationPMap = new HashMap<>();
            pensionerAllocationPMap.put(1, 0.8);
            pensionerAllocationPMap.put(2, 0.5);
            pensionerAllocationPMap.put(3, 0.3);
            pensionerAllocationPMap.put(4, 0.2);
            pensionerAllocationPMap.put(5, 0.1);

            childAllocationPMap = new HashMap<>();
            childAllocationPMap.put(1, 0.8);
            childAllocationPMap.put(2, 0.5);
            childAllocationPMap.put(3, 0.3);
            childAllocationPMap.put(4, 0.2);
            childAllocationPMap.put(5, 0.1);

            infantAllocationPMap = new HashMap<>();
            infantAllocationPMap.put(1, 0.8);
            infantAllocationPMap.put(2, 0.5);
            infantAllocationPMap.put(3, 0.3);
            infantAllocationPMap.put(4, 0.2);
            infantAllocationPMap.put(5, 0.1);
        }

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
        public int hospitals = 10000;
        public int schools = 2000;
        public int shops = 500;
        public int offices = 250;
        public int constructionSites = 1000;
        public int nurseries = 2000;
        public int restaurants = 1000;

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
        public double pOffice = 0.2;
        public double pShop = 0.1;
        public double pHospital = 0.1;
        public double pConstruction = 0.1;
        public double pTeacher = 0.2;
        public double pRestaurant = 0.1;
        public double pUnemployed = 0.2;

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
        public double pBaseTrans = 0.45;
        public double pHospitalTrans = 0.03;
        public double pConstructionSiteTrans = 0.2;
        public double pNurseryTrans = 0.044118;
        public double pOfficeTrans = 0.4;
        public double pRestaurantTrans = 1;
        public double pSchoolTrans = 0.044118;
        public double pShopTrans = 0.2;

        public double pHospitalKey = 0;
        public double pConstructionSiteKey = 0.5;
        public double pOfficeKey = 0.5;
        public double pShopKey = 0.5;

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
        public double pAttendsNursery = 0.8;

        @Override
        public String toString() {
            return "InfantAllocation{" +
                    "pAttendsNursery=" + pAttendsNursery +
                    '}';
        }
    }

    private static class NeighbourProperties {
        public double neighbourVisitFreq = 1.0 / 7.0 / 24.0;
        public int expectedNeighbours = 3;

        @Override
        public String toString() {
            return "NeighbourProperties{" +
                    "neighbourVisitFreq=" + neighbourVisitFreq +
                    ", expectedNeighbours=" + expectedNeighbours +
                    '}';
        }
    }

    private static class PersonProperties {
        public double pQuarantine = 0.9;
        public double pTransmission = 0.45;

        @Override
        public String toString() {
            return "PersonProperties{" +
                    "pQuarantine=" + pQuarantine +
                    ", pTransmission=" + pTransmission +
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
    private final NeighbourProperties neighbourProperties;
    private final PersonProperties personProperties;

    private PopulationParameters() {
        population = new Population();
        households = new Households();
        additionalMembersDistributions = new AdditionalMembersDistributions();
        buildingDistribution = new BuildingDistribution();
        workerAllocation = new WorkerAllocation();
        buildingProperties = new BuildingProperties();
        infantAllocation = new InfantAllocation();
        neighbourProperties = new NeighbourProperties();
        personProperties = new PersonProperties();
    }

    public static PopulationParameters get() {
        if (pp == null) {
            pp = new PopulationParameters();
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

    // Neighbour properties
    public double getNeighbourVisitFreq() {
        return neighbourProperties.neighbourVisitFreq;
    }
    public int getExpectedNeighbours() {
        return neighbourProperties.expectedNeighbours;
    }

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
                neighbourProperties + "\n" +
                personProperties + "\n" +
                '}';
    }
}
