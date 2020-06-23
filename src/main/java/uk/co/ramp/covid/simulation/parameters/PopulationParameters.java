package uk.co.ramp.covid.simulation.parameters;

import com.google.gson.annotations.Expose;
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

    // For easier parsing we have 2 structures for appt info. One we parse, and one we query
    private Map<String, HospitalApptInfo> hospitalAppts;

    // Marking this transient is the easiest way to avoid this being serialized since the map
    // above has all the same information
    private transient HospitalApptParameters hospitalAppsParams = null;

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
        hospitalAppts = new HashMap<>();
    }

    public boolean isValid() {
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        
        // We don't do this in a single statement to ensure that all the "uninitalised" parameter warnings are printed
        // in one go instead of being short circuited
        boolean valid = checker.isValid(populationDistribution);
        valid = valid && checker.isValid(householdDistribution) && householdDistribution.isValid();
        valid = valid && checker.isValid(buildingDistribution) && buildingDistribution.isValid();
        valid = valid && checker.isValid(workerDistribution) && workerDistribution.isValid();
        valid = valid && checker.isValid(buildingProperties);
        valid = valid && checker.isValid(infantProperties);
        valid = valid && checker.isValid(pensionerProperties);
        valid = valid && checker.isValid(personProperties);
        valid = valid && checker.isValid(householdProperties) && householdProperties.isValid();
        valid = valid && hospitalAppsParams().isValid();
        return valid;
    }

    public static PopulationParameters get() {
        if (pp == null) {
            throw new InvalidParametersException("Invalid population parameters");
        }
        return pp;
    }

    public HospitalApptParameters hospitalAppsParams() {
        if (hospitalAppsParams == null) {
            hospitalAppsParams = new HospitalApptParameters(hospitalAppts);
        }
        return hospitalAppsParams;
    }

    public static void setParameters(PopulationParameters p) {
        pp = p;
    }
    public static void clearParameters() {
        pp = null;
    }
}
