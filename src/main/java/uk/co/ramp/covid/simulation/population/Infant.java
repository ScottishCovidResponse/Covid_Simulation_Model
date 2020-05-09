package uk.co.ramp.covid.simulation.population;

public class Infant extends Person {
    public Infant() {
        this.setNursery();
    }

    private void setNursery() {
        if (Math.random() < 0.5) super.setNursery(true);
    }
}
