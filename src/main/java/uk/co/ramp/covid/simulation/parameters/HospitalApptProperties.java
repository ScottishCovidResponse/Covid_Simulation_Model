package uk.co.ramp.covid.simulation.parameters;

public class HospitalApptProperties {
    public Integer dayCaseStartTime = null;
    public Double meanDayCaseTime = null;
    public Double SDDayCaseTime = null;
    public Integer inPatientFirstStartTime = null;
    public Integer inPatientLastStartTime = null;
    public Integer outPatientFirstStartTime = null;
    public Integer outPatientLastStartTime = null;
    public Double meanOutPatientTime = null;
    public Double lockdownApptDecreasePercentage = null;

    public boolean isValid() {
        return lockdownApptDecreasePercentage >= 0.0 && lockdownApptDecreasePercentage <= 1.0;
    }
}



