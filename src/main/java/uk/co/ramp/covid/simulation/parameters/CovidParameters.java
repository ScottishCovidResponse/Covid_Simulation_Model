package uk.co.ramp.covid.simulation.parameters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

/**
 * PopulationParameters is a singleton class for reading and storing the covid disease parameters
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
    public DiseaseParameters diseaseParameters= null;

    public CovidParameters() {
        diseaseParameters = new DiseaseParameters();
        testParameters = new CovidTestParameters();
    }

    public static void setParameters(CovidParameters p) {
        cparams = p;
    }
    public static void clearParameters() {
        cparams = null;
    }

    public boolean isValid() {
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        return checker.isValid(diseaseParameters) && checker.isValid(testParameters);
    }

}
