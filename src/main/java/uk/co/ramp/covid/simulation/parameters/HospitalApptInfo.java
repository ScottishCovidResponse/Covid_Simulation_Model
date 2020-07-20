package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class HospitalApptInfo {
    /** Daily probability that a person has an appt of a particular type */
    public Probability pInPatientDaily = null;
    public Probability pOutPatientDaily = null;
    public Probability pDayCaseDaily = null;

    /** Expected length of inPatient appt (based on age) */
    public Double inPatientLengthDays = null;
}
