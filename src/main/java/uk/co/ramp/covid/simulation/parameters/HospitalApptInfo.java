package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

public class HospitalApptInfo {
    /** Daily probability that a person has an appt of a particular type */
    public Probability pInPatientDaily = null;
    public Probability pOutPatientDaily = null;
    public Probability pDayCaseDaily = null;

    /** Expected length of inPatient appt (based on age) */
    public Double inPatientLengthDays = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HospitalApptInfo that = (HospitalApptInfo) o;
        return Objects.equals(pInPatientDaily, that.pInPatientDaily) &&
                Objects.equals(pOutPatientDaily, that.pOutPatientDaily) &&
                Objects.equals(pDayCaseDaily, that.pDayCaseDaily) &&
                Objects.equals(inPatientLengthDays, that.inPatientLengthDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pInPatientDaily, pOutPatientDaily, pDayCaseDaily, inPatientLengthDays);
    }
}
