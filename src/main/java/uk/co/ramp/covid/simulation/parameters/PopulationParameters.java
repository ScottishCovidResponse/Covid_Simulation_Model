package uk.co.ramp.covid.simulation.parameters;

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
    private static PopulationParameters pp = null;

    public final Map<String,Double> populationDistribution;
    public final HouseholdDistribution householdDistribution;
    public BuildingDistribution buildingDistribution;
    public final WorkerDistribution workerDistribution;
    public final BuildingProperties buildingProperties;
    public InfantProperties infantProperties;
    public PensionerProperties pensionerProperties;
    public final PersonProperties personProperties;
    public HouseholdProperties householdProperties;

    private PopulationParameters() {
        populationDistribution = new HashMap<>();
        householdDistribution = new HouseholdDistribution();
        buildingDistribution = new BuildingDistribution();
        workerDistribution = new WorkerDistribution();
        buildingProperties = new BuildingProperties();
        infantProperties = new InfantProperties();
        pensionerProperties = new PensionerProperties();
        personProperties = new PersonProperties();
        householdProperties = new HouseholdProperties();
    }

    public boolean isValid() {
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        boolean valid = true;
        // We don't do this in a single statement to ensure that all the "uninitalised" parameter warnings are printed
        // in one go instead of being short circuited
        valid = checker.isValid(populationDistribution);
        valid = valid && checker.isValid(householdDistribution) && householdDistribution.isValid();
        valid = valid && checker.isValid(buildingDistribution) && buildingDistribution.isValid();
        valid = valid && checker.isValid(workerDistribution) && workerDistribution.isValid();
        valid = valid && checker.isValid(buildingProperties);
        valid = valid && checker.isValid(infantProperties);
        valid = valid && checker.isValid(pensionerProperties);
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
}
