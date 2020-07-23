package uk.co.ramp.covid.simulation.parameters;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HospitalApptProperties that = (HospitalApptProperties) o;
        return Objects.equals(dayCaseStartTime, that.dayCaseStartTime) &&
                Objects.equals(meanDayCaseTime, that.meanDayCaseTime) &&
                Objects.equals(SDDayCaseTime, that.SDDayCaseTime) &&
                Objects.equals(inPatientFirstStartTime, that.inPatientFirstStartTime) &&
                Objects.equals(inPatientLastStartTime, that.inPatientLastStartTime) &&
                Objects.equals(outPatientFirstStartTime, that.outPatientFirstStartTime) &&
                Objects.equals(outPatientLastStartTime, that.outPatientLastStartTime) &&
                Objects.equals(meanOutPatientTime, that.meanOutPatientTime) &&
                Objects.equals(lockdownApptDecreasePercentage, that.lockdownApptDecreasePercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayCaseStartTime, meanDayCaseTime, SDDayCaseTime, inPatientFirstStartTime, inPatientLastStartTime, outPatientFirstStartTime, outPatientLastStartTime, meanOutPatientTime, lockdownApptDecreasePercentage);
    }
}



