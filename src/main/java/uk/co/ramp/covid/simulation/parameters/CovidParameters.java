package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.InvalidParametersException;

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

    public CovidTestParameters testParameters = null;
    public DiseaseParameters diseaseParameters = null;
    public InfectionSeedingProperties infectionSeedProperties = null;
    public HospitalisationParameters hospitalisationParameters = null;
    public CareHomeParameters careHomeParameters = null;

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
                && checker.isValid(hospitalisationParameters);
    }

}
