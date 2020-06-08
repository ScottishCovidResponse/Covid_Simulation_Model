package uk.co.ramp.covid.simulation.parameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

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

    public final Map<String,Double> population;
    public final HouseholdDistribution households;
    public BuildingDistribution buildingDistribution;
    public final WorkerDistribution workerAllocation;
    public final BuildingProperties buildingProperties;
    public InfantProperties infantAllocation;
    public final PersonProperties personProperties;
    public HouseholdProperties householdProperties;

    private PopulationParameters() {
        population = new HashMap<>();
        households = new HouseholdDistribution();
        buildingDistribution = new BuildingDistribution();
        workerAllocation = new WorkerDistribution();
        buildingProperties = new BuildingProperties();
        infantAllocation = new InfantProperties();
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
    
    private static boolean isValidProbability(Double val, String name) {
        if(val < 0 || val > 1) {
            LOGGER.error(name + " is not a valid probability");
            return false;
        }
        return true;
    }

}
