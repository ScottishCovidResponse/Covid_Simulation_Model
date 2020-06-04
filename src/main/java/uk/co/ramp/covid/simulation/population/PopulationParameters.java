package uk.co.ramp.covid.simulation.population;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
    public static class Households {
        @ParamDoc("Ratio of population size to households, e.g. 2 implies 1 household per 2 people ")
        public Double householdRatio = null;

        @ParamDoc("Probability of a single adult household")
        public Double pSingleAdult = null;
        @ParamDoc("Probability of a small adult household")
        public Double pSmallAdult = null;
        @ParamDoc("Probability of a single parent household")
        public Double pSingleParent = null;
        @ParamDoc("Probability of a small family household")
        public Double pSmallFamily = null;
        @ParamDoc("Probability of a two adult family household")
        public Double pLargeTwoAdultFamily = null;
        @ParamDoc("Probability of a >= 3 adult family household")
        public Double pLargeManyAdultFamily = null;
        @ParamDoc("Probability of large adult only household")
        public Double pLargeAdult = null;
        @ParamDoc("Probability of an adult pensioner household")
        public Double pAdultPensioner = null;
        @ParamDoc("Probability of a two pensioenr household")
        public Double pDoubleOlder = null;
        @ParamDoc("Probability of a single pensioenr household")
        public Double pSingleOlder = null;

        public ProbabilityDistribution<Function<Places, Household>> householdTypeDistribution() {
            ProbabilityDistribution<Function<Places, Household>> p = new ProbabilityDistribution<>();
            p.add(pSingleAdult, SingleAdult::new);
            p.add(pSmallAdult, SmallAdult::new);
            p.add(pSingleParent, SingleParent::new);
            p.add(pSmallFamily, SmallFamily::new);
            p.add(pLargeTwoAdultFamily, LargeTwoAdultFamiy::new);
            p.add(pLargeManyAdultFamily, LargeManyAdultFamily::new);
            p.add(pLargeAdult, LargeAdult::new);
            p.add(pAdultPensioner, AdultPensioner::new);
            p.add(pDoubleOlder, DoubleOlder::new);
            p.add(pSingleOlder, SingleOlder::new);
            return p;
        }

        public boolean isValid() {
            boolean probabilitiesValid = ParameterChecker.isValidProbability(pSingleAdult, "pSingleAdult")
                    && ParameterChecker.isValidProbability(pSmallAdult, "pSmallAdult")
                    && ParameterChecker.isValidProbability(pSingleParent, "pSingleParent")
                    && ParameterChecker.isValidProbability(pSmallFamily, "pSmallFamily")
                    && ParameterChecker.isValidProbability(pLargeTwoAdultFamily, "pLargeTwoAdultFamily")
                    && ParameterChecker.isValidProbability(pLargeManyAdultFamily, "pLargeManyAdultFamily")
                    && ParameterChecker.isValidProbability(pLargeAdult, "pLargeAdult")
                    && ParameterChecker.isValidProbability(pAdultPensioner, "pAdultPensioner")
                    && ParameterChecker.isValidProbability(pDoubleOlder, "pDoubleOlder")
                    && ParameterChecker.isValidProbability(pSingleOlder, "pSingleOlder");

            probabilitiesValid = probabilitiesValid && (householdRatio >= 1);

            double totalP = pSingleAdult + pSmallAdult + pSingleParent + pSmallFamily + pLargeManyAdultFamily
                    + pLargeTwoAdultFamily + pLargeAdult + pAdultPensioner + pDoubleOlder + pSingleOlder;
            if(!(totalP <= 1 + EPSILON && totalP >= 1 - EPSILON)) {
                LOGGER.error("Household parameter probabilities do not total one");
                return false;
            }
            return probabilitiesValid;
        }

    }

    public static class Size {
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
    public static class BuildingDistribution {
        @ParamDoc("Hospitals per N people")
        public Integer hospitals = null;
        @ParamDoc("Distribution of hosptial sizes")
        public Size hospitalSizes = null;

        @ParamDoc("Schools per N people")
        public Integer schools = null;
        @ParamDoc("Distribution of school sizes")
        public Size schoolSizes = null;

        @ParamDoc("Shops per N people")
        public Integer shops = null;
        @ParamDoc("Distribution of shop sizes")
        public Size shopSizes = null;

        @ParamDoc("Offices per N people")
        public Integer offices = null;
        @ParamDoc("Distribution of office sizes")
        public Size officeSizes = null;

        @ParamDoc("Construction Sites per N people")
        public Integer constructionSites = null;
        @ParamDoc("Distribution of construction site sizes")
        public Size constructionSiteSizes = null;

        @ParamDoc("Nurseries per N people")
        public Integer nurseries = null;
        @ParamDoc("Distribution of nursery sizes")
        public Size nurserySizes = null;

        @ParamDoc("Restaurants per N people")
        public Integer restaurants = null;
        @ParamDoc("Distribution of restaurant sizes")
        public Size restaurantSizes = null;

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
    public static class WorkerAllocation {
        @ParamDoc("Probability of working in an office")
        public Double pOffice = null;
        @ParamDoc("Probability of working in a shop")
        public Double pShop = null;
        @ParamDoc("Probability of working in a hospital")
        public Double pHospital = null;
        @ParamDoc("Probability of working on a construction site")
        public Double pConstruction = null;
        @ParamDoc("Probability of working in a schol")
        public Double pTeacher = null;
        @ParamDoc("Probability of working in a restaurant")
        public Double pRestaurant = null;
        @ParamDoc("Probability of being unemployed")
        public Double pUnemployed = null;
        @ParamDoc("Probability of working in a nursery unemployed")
        public Double pNursery = null;

        @ParamDoc("Probability of being assigned a workplace of a particular size")
        public Size sizeAllocation = null;

        public boolean isValid() {
            boolean probabilitiesValid = ParameterChecker.isValidProbability(pOffice, "pOffice")
                    && ParameterChecker.isValidProbability(pShop, "pShop")
                    && ParameterChecker.isValidProbability(pHospital, "pHospital")
                    && ParameterChecker.isValidProbability(pConstruction, "pConstruction")
                    && ParameterChecker.isValidProbability(pTeacher, "pTeacher")
                    && ParameterChecker.isValidProbability(pRestaurant, "pRestaurant")
                    && ParameterChecker.isValidProbability(pNursery, "pNursery")
                    && ParameterChecker.isValidProbability(pUnemployed, "pUnemployed");

            double totalP = pOffice + pShop + pHospital + pConstruction + pTeacher + pRestaurant + pNursery + pUnemployed;
            if(!(totalP <= 1 + EPSILON && totalP >= 1 - EPSILON)) {
                LOGGER.error("Worker allocation parameter probabilities do not total one");
                return false;
            }

            return probabilitiesValid;
        }
    }

    public static class BuildingProperties {
        @ParamDoc("Base disease transmission probability for all places")
        public Double pBaseTrans = null;
        @ParamDoc("Transmission probability for hospitals")
        public Double pHospitalTrans = null;
        @ParamDoc("Transmission probability for construction sites")
        public Double pConstructionSiteTrans = null;
        @ParamDoc("Transmission probability for nurseries")
        public Double pNurseryTrans = null;
        @ParamDoc("Transmission probability for offices")
        public Double pOfficeTrans = null;
        @ParamDoc("Transmission probability for restaurants")
        public Double pRestaurantTrans = null;
        @ParamDoc("Transmission probability for schols")
        public Double pSchoolTrans = null;
        @ParamDoc("Transmission probability for shops")
        public Double pShopTrans = null;

        @ParamDoc("Probability a hospital remains open during lockdown")
        public Double pHospitalKey = null;
        @ParamDoc("Probability a construction site remains open during lockdown")
        public Double pConstructionSiteKey = null;
        @ParamDoc("Probability an office remains open during lockdown")
        public Double pOfficeKey = null;
        @ParamDoc("Probability a shop remains open during lockdown")
        public Double pShopKey = null;

        @ParamDoc("Probability (per hour) a shooper (and family) leave a shop")
        public Double pLeaveShop = null;
        @ParamDoc("Probability (per hour) a restaurant visitor (and family) leave a restaurant")
        public Double pLeaveRestaurant = null;

        public boolean isValid() {
            return ParameterChecker.isValidProbability(pBaseTrans, "pBaseTrans")
                    && ParameterChecker.isValidProbability(pHospitalKey, "pHospitalKey")
                    && ParameterChecker.isValidProbability(pConstructionSiteKey, "pConstructionSiteKey")
                    && ParameterChecker.isValidProbability(pOfficeKey, "pOfficeKey")
                    && ParameterChecker.isValidProbability(pShopKey, "pShopKey")
                    && ParameterChecker.isValidProbability(pLeaveShop, "pLeaveShop")
                    && ParameterChecker.isValidProbability(pLeaveRestaurant, "pLeaveRestaurant");
        }
    }

    public static class InfantAllocation {
        @ParamDoc("Probability an infant attends nursery")
        public Double pAttendsNursery = null;

        public boolean isValid() {
            return ParameterChecker.isValidProbability(pAttendsNursery, "pAttendsNursery");
        }
    }

    public static class PersonProperties {
        @ParamDoc("Probability a person respects lockdown")
        public Double pQuarantine = null;
        @ParamDoc("Base transimission rate for a person")
        public Double pTransmission = null;

        public boolean isValid() {
            return ParameterChecker.isValidProbability(pQuarantine, "pQuarantine")
                    && ParameterChecker.isValidProbability(pTransmission, "pTransmission");
        }
    }
    
    public static class HouseholdProperties {
        @ParamDoc("Chance a visitor (and family) leave a neighbour each hour")
        public Double visitorLeaveRate = null;
        @ParamDoc("How often people try to visit neighbours")
        public Double neighbourVisitFreq = null;
        @ParamDoc("Expected (Poisson) number of neighbours")
        public Integer expectedNeighbours = null;

        @ParamDoc("Probability a household goes shopping")
        public Double pGoShopping = null;
        @ParamDoc("Probability a household goes to eat")
        public Double pGoRestaurant = null;
        @ParamDoc("Number of days a household isolates for when a member becomes symptomatic")
        public Integer householdIsolationPeriod = null;
        @ParamDoc("Probability the household respects isolation when a member becomes symptomatic")
        public Double pWillIsolate = null;

        public boolean isValid() {
            return ParameterChecker.isValidProbability(pGoShopping, "pGoShopping")
                    && ParameterChecker.isValidProbability(pGoRestaurant, "pGoRestaurant")
                    && ParameterChecker.isValidProbability(pWillIsolate, "pWillIsolate");
        }
    }

    @ParamDoc("Map specifying population. E.g. \"m_0_5:0.02\" implies a 0.02 chance a person is male aged between 0-5")
    public Map<String,Double> population;
    @ParamDoc("Controls the distribution of households")
    public Households households;
    @ParamDoc("Controls the distribution of places")
    public BuildingDistribution buildingDistribution;
    @ParamDoc("Controls the distribution of workers")
    public WorkerAllocation workerAllocation;
    @ParamDoc("Controls the properties of place types")
    public BuildingProperties buildingProperties;
    @ParamDoc("Determines infant nursery allocation")
    public InfantAllocation infantAllocation;
    @ParamDoc("Person Properties")
    public PersonProperties personProperties;
    @ParamDoc("Household Properties")
    public HouseholdProperties householdProperties;

    private PopulationParameters() {
        population = new HashMap<>();
        households = new Households();
        buildingDistribution = new BuildingDistribution();
        workerAllocation = new WorkerAllocation();
        buildingProperties = new BuildingProperties();
        infantAllocation = new InfantAllocation();
        personProperties = new PersonProperties();
        householdProperties = new HouseholdProperties();
    }

    public boolean isValid() {
        boolean valid = true;
        // We don't do this in a single statement to ensure that all the "uninitalised" parameter warnings are printed
        // in one go instead of being short circuited
        valid = valid && ParameterChecker.isValid(population);
        valid = valid && ParameterChecker.isValid(households) && households.isValid();
        valid = valid && ParameterChecker.isValid(buildingDistribution) && buildingDistribution.isValid();
        valid = valid && ParameterChecker.isValid(workerAllocation) && workerAllocation.isValid();
        valid = valid && ParameterChecker.isValid(buildingProperties) && buildingProperties.isValid();
        valid = valid && ParameterChecker.isValid(infantAllocation) && infantAllocation.isValid();
        valid = valid && ParameterChecker.isValid(personProperties) && personProperties.isValid();
        valid = valid && ParameterChecker.isValid(householdProperties) && householdProperties.isValid();
        return valid;
    }
    
    public static void printDoc() {
        StringBuilder b = new StringBuilder();
        // We use new objects here to allow us to print the doc-strings even if the user-specified parameters are wrong.
        b.append("---Population Parameters---\n");
        ParameterPrinter.appendDoc(b, new PopulationParameters());
        b.append("---household---\n");
        ParameterPrinter.appendDoc(b, new Households());
        b.append("---buildingDistribution---\n");
        ParameterPrinter.appendDoc(b, new BuildingDistribution());
        b.append("---workerAllocation---\n");
        ParameterPrinter.appendDoc(b, new WorkerAllocation());
        b.append("---buildingProperties---\n");
        ParameterPrinter.appendDoc(b, new BuildingProperties());
        b.append("---infantAllocation---\n");
        ParameterPrinter.appendDoc(b, new InfantAllocation());
        b.append("---personProperties---\n");
        ParameterPrinter.appendDoc(b, new PersonProperties());
        b.append("---householdProperties---\n");
        ParameterPrinter.appendDoc(b, new HouseholdProperties());
        LOGGER.info(b.toString());
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


    public ProbabilityDistribution<Function<Places, Household>> getHouseholdDistribution() {
        return households.householdTypeDistribution();
    }

    public double getHouseholdRatio() { return households.householdRatio; }

    public void setHouseholdRatio(double r) { households.householdRatio = r; }

    public double getpSingleAdult() {
        return households.pSingleAdult;
    }

    public double getpSmallAdult() {
        return households.pSmallAdult;
    }

    public double getpSingleParent() {
        return households.pSingleParent;
    }

    public double getpSmallFamily() {
        return households.pSmallFamily;
    }

    public double getpLargeTwoAdultFamily() {
        return households.pLargeTwoAdultFamily;
    }

    public double getpLargeManyFamily() {
        return households.pLargeManyAdultFamily;
    }

    public double getpLargeAdult() {
        return households.pLargeAdult;
    }

    public double getpAdultPensioner() {
        return households.pAdultPensioner;
    }

    public double getpDobuleOlder() {
        return households.pDoubleOlder;
    }

    public double getpSingleOlder() {
        return households.pSingleOlder;
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

    public void setConstructionSiteRatio(Integer ratio) {
        buildingDistribution.constructionSites = ratio;
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
    public void setAttendsNursery(double pAttendsNursery) {
        infantAllocation.pAttendsNursery = pAttendsNursery;
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

    public void setPQuarantine(double pQuarantine) {
        personProperties.pQuarantine = pQuarantine;
    }
    public double getpTransmission() {
        return personProperties.pTransmission;
    }

    public void setPTransmission(double pTransmission) {
        personProperties.pTransmission = pTransmission;
    }
    


}
