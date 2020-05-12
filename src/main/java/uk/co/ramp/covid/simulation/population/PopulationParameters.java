package uk.co.ramp.covid.simulation.population;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
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
    private class Population {
        public double pInfants;
        public double pChildren;
        public double pAdults;
        public double pPensioners;

        public Population() {
            // Default parameters
            pInfants = 0.08;
            pChildren = 0.2;
            pAdults = 0.5;
            pPensioners = 0.22;
        }

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
    private class Households {
        public double pAdultOnly;
        public double pPensionerOnly;
        public double pPensionerAdult;
        public double pAdultChildren;
        public double pPensionerChildren;
        public double pAdultPensionerChildren;

        public Households() {
            // Defeault paramerts
            pAdultOnly = 0.3;
            pPensionerOnly = 0.1;
            pPensionerAdult = 0.1;
            pAdultChildren = 0.3;
            pPensionerChildren = 0.1;
            pAdultPensionerChildren = 0.1;
        }

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
    private class AdditionalMembersDistributions {
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
    private class BuildingDistribution {
        public int hospitals;
        public int schools;
        public int shops;
        public int offices;
        public int constructionSites;
        public int nurseries;
        public int restaurants;

        public BuildingDistribution() {
            // Default parameters
            hospitals = 10000;
            schools = 2000;
            shops = 500;
            offices = 250;
            constructionSites = 1000;
            nurseries = 2000;
            restaurants = 1000;
        }

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
    private class WorkerAllocation {
        public double pOffice;
        public double pShop;
        public double pHospital;
        public double pConstruction;
        public double pTeacher;
        public double pRestaurant;
        public double pUnemployed;

        public WorkerAllocation() {
            pOffice = 0.2;
            pShop = 0.1;
            pHospital = 0.1;
            pConstruction = 0.1;
            pTeacher = 0.2;
            pRestaurant = 0.1;
            pUnemployed = 0.2;
        }

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

    private class BuildingProperties {
        public double pBaseTrans;
        public double pHospitalTrans;
        public double pConstructionSiteTrans;
        public double pNurseryTrans;
        public double pOfficeTrans;
        public double pRestaurantTrans;
        public double pSchoolTrans;
        public double pShopTrans;
        public double pHospitalKey;
        public double pConstructionSiteKey;
        public double pOfficeKey;
        public double pShopKey;

        BuildingProperties() {
            pBaseTrans = 0.45;
            pHospitalTrans = 0.03;
            pConstructionSiteTrans = 0.2;
            pNurseryTrans = 0.044118;
            pOfficeTrans = 0.4;
            pRestaurantTrans = 1;
            pSchoolTrans = 0.044118;
            pShopTrans = 0.2;

            pHospitalKey = 0;
            pConstructionSiteKey = 0.5;
            pOfficeKey = 0.5;
            pShopKey = 0.5;
        }

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

    private class InfantAllocation {
        public double pAttendsNursery;

        public InfantAllocation() {
            pAttendsNursery = 0.5;
        }

        @Override
        public String toString() {
            return "InfantAllocation{" +
                    "pAttendsNursery=" + pAttendsNursery +
                    '}';
        }
    }

    private class NeighbourProperties {
        public double neighbourVisitFreq;
        public int expectedNeighbours;

        public NeighbourProperties() {
            neighbourVisitFreq = 1.0 / 7.0 / 24.0;
            expectedNeighbours = 3;
        }

        @Override
        public String toString() {
            return "NeighbourProperties{" +
                    "neighbourVisitFreq=" + neighbourVisitFreq +
                    ", expectedNeighbours=" + expectedNeighbours +
                    '}';
        }
    }

    private class PersonProperties {
        public double pQuarantine;
        public double pTransmission;
        PersonProperties() {
            pQuarantine = 0.9;
            pTransmission = 0.45;
        }

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

    /** Read population data from JSON file */
    public static void readParametersFromFile(String path) throws IOException, JsonParseException {
        Reader file = new FileReader(path);
        Gson gson = new Gson();
        pp = gson.fromJson(file, PopulationParameters.class);
    }

    public static void printJSON() {
        Gson gson = new Gson();
        String j = gson.toJson(PopulationParameters.get());
        System.out.println(j);
    }

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
