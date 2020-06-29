package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.InvalidParametersException;

import java.util.HashMap;
import java.util.Map;

public class HospitalApptParameters {
    
    // For simplicity we keep n copies of the apptInfo, one for each sex/age in the range
    private Map<PopulationDistribution.SexAge, HospitalApptInfo> apptInfo;
    
    public HospitalApptParameters(Map<String, HospitalApptInfo> m) {
        apptInfo = new HashMap<>();
        readFromMap(m);
    }

    // Similar to PopulationDistribution
    private void readFromMap(Map<String, HospitalApptInfo> m) {
        for (Map.Entry<String, HospitalApptInfo> elem : m.entrySet()) {
            String[] k = elem.getKey().split("_");
            if (k.length != 3) {
                throw new InvalidParametersException("HospitalAppt data format is invalid");
            }

            Person.Sex s;
            if (k[0].equals("m")) {
                s = Person.Sex.MALE;
            } else if (k[0].equals("f")) {
                s = Person.Sex.FEMALE;
            } else {
                throw new InvalidParametersException("HospitalAppt data format is invalid. Expected sex = m/f");
            }

            int age_l = Integer.parseInt(k[1]);
            int age_r = Integer.parseInt(k[2]);

            if (age_l > age_r) {
                throw new InvalidParametersException("HospitalAppt data format is invalid. age range start > end");
            }

            for (int i = age_l; i <= age_r; i++) {
                apptInfo.put(new PopulationDistribution.SexAge(s, i), elem.getValue());
            }
        }
    }
    
    public boolean isValid() {
        ParameterInitialisedChecker checker = new ParameterInitialisedChecker();
        boolean valid = true;
        for (Map.Entry<PopulationDistribution.SexAge, HospitalApptInfo> elem : apptInfo.entrySet()) {
            valid = valid && checker.isValid(elem.getValue());
        }
        return valid;
    }

    public HospitalApptInfo getParams(Person.Sex sex, int age) {
        return apptInfo.get(new PopulationDistribution.SexAge(sex, age));
    }
}
