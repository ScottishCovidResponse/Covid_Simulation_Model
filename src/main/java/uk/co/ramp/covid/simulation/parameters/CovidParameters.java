package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.util.Objects;

/**
 * CovidParameters is a singleton class for reading and storing the covid disease parameters
 *
 * Note: This use of the singleton pattern is not thread safe
 */
public class CovidParameters {
    public static CovidParameters cparams = null;

    public static CovidParameters get() {
        if (cparams == null) {
            throw new InvalidParametersException("Invalid COVID parameters");
        }
        return cparams;
    }

    public CovidTestParameters testParameters;
    public DiseaseParameters diseaseParameters;
    public InfectionSeedingProperties infectionSeedProperties;
    public HospitalisationParameters hospitalisationParameters;
    public CareHomeParameters careHomeParameters;

    public CovidParameters() {
        testParameters = new CovidTestParameters();
        diseaseParameters = new DiseaseParameters();
        infectionSeedProperties = new InfectionSeedingProperties();
        hospitalisationParameters = new HospitalisationParameters();
        careHomeParameters = new CareHomeParameters();
    }
    
    public static void setParameters(CovidParameters p) {
        cparams = p;
    }
    public static void clearParameters() {
        cparams = null;
    }

    public boolean isValid() {
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        return checker.isValid(diseaseParameters)
                && checker.isValid(testParameters)
                && checker.isValid(infectionSeedProperties)
                && checker.isValid(hospitalisationParameters) && hospitalisationParameters.isValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovidParameters that = (CovidParameters) o;
        return Objects.equals(testParameters, that.testParameters) &&
                Objects.equals(diseaseParameters, that.diseaseParameters) &&
                Objects.equals(infectionSeedProperties, that.infectionSeedProperties) &&
                Objects.equals(hospitalisationParameters, that.hospitalisationParameters) &&
                Objects.equals(careHomeParameters, that.careHomeParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testParameters, diseaseParameters, infectionSeedProperties, hospitalisationParameters, careHomeParameters);
    }
}
