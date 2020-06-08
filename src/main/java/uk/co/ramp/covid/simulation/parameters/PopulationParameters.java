package uk.co.ramp.covid.simulation.parameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.place.householdtypes.*;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

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
        public Double householdRatio = null;

        public Double pSingleAdult = null;
        public Double pSmallAdult = null;
        public Double pSingleParent = null;
        public Double pSmallFamily = null;
        public Double pLargeTwoAdultFamily = null;
        public Double pLargeManyAdultFamily = null;
        public Double pLargeAdult = null;
        public Double pAdultPensioner = null;
        public Double pDoubleOlder = null;
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
            boolean probabilitiesValid = isValidProbability(pSingleAdult, "pSingleAdult")
                    && isValidProbability(pSmallAdult, "pSmallAdult")
                    && isValidProbability(pSingleParent, "pSingleParent")
                    && isValidProbability(pSmallFamily, "pSmallFamily")
                    && isValidProbability(pLargeTwoAdultFamily, "pLargeTwoAdultFamily")
                    && isValidProbability(pLargeManyAdultFamily, "pLargeManyAdultFamily")
                    && isValidProbability(pLargeAdult, "pLargeAdult")
                    && isValidProbability(pAdultPensioner, "pAdultPensioner")
                    && isValidProbability(pDoubleOlder, "pDoubleOlder")
                    && isValidProbability(pSingleOlder, "pSingleOlder");

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
        public Double pOffice = null;
        public Double pShop = null;
        public Double pHospital = null;
        public Double pConstruction = null;
        public Double pTeacher = null;
        public Double pRestaurant = null;
        public Double pUnemployed = null;
        public Double pNursery = null;

        public Size sizeAllocation = null;

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

    public static class BuildingProperties {
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
            return isValidProbability(pBaseTrans, "pBaseTrans")
                    && isValidProbability(pHospitalKey, "pHospitalKey")
                    && isValidProbability(pConstructionSiteKey, "pConstructionSiteKey")
                    && isValidProbability(pOfficeKey, "pOfficeKey")
                    && isValidProbability(pShopKey, "pShopKey")
                    && isValidProbability(pLeaveShop, "pLeaveShop")
                    && isValidProbability(pLeaveRestaurant, "pLeaveRestaurant");
        }
    }

    public static class InfantAllocation {
        public Double pAttendsNursery = null;

        public boolean isValid() {
            return isValidProbability(pAttendsNursery, "pAttendsNursery");
        }
    }

    public static class PersonProperties {
        public Double pQuarantine = null;
        public Double pTransmission = null;

        public boolean isValid() {
            return isValidProbability(pQuarantine, "pQuarantine")
                    && isValidProbability(pTransmission, "pTransmission");
        }
    }
    
    public static class HouseholdProperties {
        public Double visitorLeaveRate = null;
        public Double neighbourVisitFreq = null;
        public Integer expectedNeighbours = null;

        public Double pGoShopping = null;
        public Double pGoRestaurant = null;
        public Integer householdIsolationPeriod = null;
        public Double pWillIsolate = null;
        public Double pLockCompliance = null;

        public boolean isValid() {
            return isValidProbability(pGoShopping, "pGoShopping")
                    && isValidProbability(pGoRestaurant, "pGoRestaurant")
                    && isValidProbability(pWillIsolate, "pWillIsolate")
                    && isValidProbability(pLockCompliance, "pLockCompliance");
        }
    }

    public final Map<String,Double> population;
    public final Households households;
    public BuildingDistribution buildingDistribution;
    public final WorkerAllocation workerAllocation;
    public final BuildingProperties buildingProperties;
    public InfantAllocation infantAllocation;
    public final PersonProperties personProperties;
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
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        boolean valid = true;
        // We don't do this in a single statement to ensure that all the "uninitalised" parameter warnings are printed
        // in one go instead of being short circuited
        valid = valid && checker.isValid(population);
        valid = valid && checker.isValid(households) && households.isValid();
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
    
    private static boolean isValidProbability(Double val, String name) {
        if(val < 0 || val > 1) {
            LOGGER.error(name + " is not a valid probability");
            return false;
        }
        return true;
    }

}
