package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

public class DiseaseParameters {
    public Double meanLatentPeriod = null;
    public Double sdLatentPeriod = null;
    public Double meanAsymptomaticPeriod = null;
    public Double sdAsymptomaticPeriod = null;
    public Probability pSymptomaticCaseChild = null;
    public Probability pSymptomaticCaseAdult = null;
    public Probability pSymptomaticCasePensioner = null;
    public Double meanSymptomDelay = null;
    public Double meanSymptomDelaySD = null;
    public Double meanInfectiousDuration = null;
    public Double sdInfectiousDuration = null;
    public Double phase1Betaa = null;
    public Double phase1Betab = null;
    public Double asymptomaticTransAdjustment = null;
    public Double symptomaticTransAdjustment = null;
    public Double caseMortalityBase = null;
    public Double caseMortalityRate = null;
    public Double childProgressionPhase2 = null;
    public Double adultProgressionPhase2 = null;
    public Double pensionerProgressionPhase2 = null;

    /** Probabilities that a case goes to hospital when they eventually survive/die */
    public Probability pSurvivorGoesToHospital = null;
    public Probability pFatalityGoesToHospital = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiseaseParameters that = (DiseaseParameters) o;
        return Objects.equals(meanLatentPeriod, that.meanLatentPeriod) &&
                Objects.equals(sdLatentPeriod, that.sdLatentPeriod) &&
                Objects.equals(meanAsymptomaticPeriod, that.meanAsymptomaticPeriod) &&
                Objects.equals(sdAsymptomaticPeriod, that.sdAsymptomaticPeriod) &&
                Objects.equals(pSymptomaticCaseChild, that.pSymptomaticCaseChild) &&
                Objects.equals(pSymptomaticCaseAdult, that.pSymptomaticCaseAdult) &&
                Objects.equals(pSymptomaticCasePensioner, that.pSymptomaticCasePensioner) &&
                Objects.equals(meanSymptomDelay, that.meanSymptomDelay) &&
                Objects.equals(meanSymptomDelaySD, that.meanSymptomDelaySD) &&
                Objects.equals(meanInfectiousDuration, that.meanInfectiousDuration) &&
                Objects.equals(sdInfectiousDuration, that.sdInfectiousDuration) &&
                Objects.equals(phase1Betaa, that.phase1Betaa) &&
                Objects.equals(phase1Betab, that.phase1Betab) &&
                Objects.equals(asymptomaticTransAdjustment, that.asymptomaticTransAdjustment) &&
                Objects.equals(symptomaticTransAdjustment, that.symptomaticTransAdjustment) &&
                Objects.equals(caseMortalityBase, that.caseMortalityBase) &&
                Objects.equals(caseMortalityRate, that.caseMortalityRate) &&
                Objects.equals(childProgressionPhase2, that.childProgressionPhase2) &&
                Objects.equals(adultProgressionPhase2, that.adultProgressionPhase2) &&
                Objects.equals(pensionerProgressionPhase2, that.pensionerProgressionPhase2) &&
                Objects.equals(pSurvivorGoesToHospital, that.pSurvivorGoesToHospital) &&
                Objects.equals(pFatalityGoesToHospital, that.pFatalityGoesToHospital);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meanLatentPeriod, sdLatentPeriod, meanAsymptomaticPeriod, sdAsymptomaticPeriod, pSymptomaticCaseChild, pSymptomaticCaseAdult, pSymptomaticCasePensioner, meanSymptomDelay, meanSymptomDelaySD, meanInfectiousDuration, sdInfectiousDuration, phase1Betaa, phase1Betab, asymptomaticTransAdjustment, symptomaticTransAdjustment, caseMortalityBase, caseMortalityRate, childProgressionPhase2, adultProgressionPhase2, pensionerProgressionPhase2, pSurvivorGoesToHospital, pFatalityGoesToHospital);
    }
}
