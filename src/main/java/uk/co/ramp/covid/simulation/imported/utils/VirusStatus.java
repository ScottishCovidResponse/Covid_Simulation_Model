package uk.co.ramp.covid.simulation.imported.utils;

public enum VirusStatus {

    SUSCEPTIBLE(0), EXPOSED(2), INFECTED(3), RECOVERED(1);

    private final int val;

    VirusStatus(int i) {
        this.val = i;
    }

    public int getVal() {
        return val;
    }
}
