package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public PublicTransportParameters publicTransportParameters;
    public HospitalApptProperties hospitalApptProperties;

    // For easier parsing we have 2 structures for appt info. One we parse, and one we query
    private final Map<String, HospitalApptInfo> hospitalAppts;

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
        publicTransportParameters = new PublicTransportParameters();
        hospitalApptProperties = new HospitalApptProperties();
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
        valid = valid && checker.isValid(buildingProperties) && buildingProperties.isValid();
        valid = valid && checker.isValid(infantProperties);
        valid = valid && checker.isValid(pensionerProperties) && pensionerProperties.isValid();
        valid = valid && checker.isValid(personProperties) && personProperties.isValid();
        valid = valid && checker.isValid(householdProperties) && householdProperties.isValid();
        valid = valid && checker.isValid(publicTransportParameters) && publicTransportParameters.isValid();
        valid = valid && checker.isValid(hospitalApptProperties) && hospitalApptProperties.isValid();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopulationParameters that = (PopulationParameters) o;
        return Objects.equals(populationDistribution, that.populationDistribution) &&
                Objects.equals(householdDistribution, that.householdDistribution) &&
                Objects.equals(buildingDistribution, that.buildingDistribution) &&
                Objects.equals(workerDistribution, that.workerDistribution) &&
                Objects.equals(buildingProperties, that.buildingProperties) &&
                Objects.equals(infantProperties, that.infantProperties) &&
                Objects.equals(pensionerProperties, that.pensionerProperties) &&
                Objects.equals(personProperties, that.personProperties) &&
                Objects.equals(householdProperties, that.householdProperties) &&
                Objects.equals(publicTransportParameters, that.publicTransportParameters) &&
                Objects.equals(hospitalApptProperties, that.hospitalApptProperties) &&
                Objects.equals(hospitalAppts, that.hospitalAppts) &&
                Objects.equals(hospitalAppsParams, that.hospitalAppsParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(populationDistribution, householdDistribution, buildingDistribution, workerDistribution, buildingProperties, infantProperties, pensionerProperties, personProperties, householdProperties, publicTransportParameters, hospitalApptProperties, hospitalAppts, hospitalAppsParams);
    }
}
