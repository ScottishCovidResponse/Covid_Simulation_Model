package uk.co.ramp.covid.simulation.population;

public class Infant extends Person {
    public Infant() {
        this.setNursery();
    }

    private void setNursery() {
        if (Math.random() < PopulationParameters.get().getpAttendsNursery()) {
            super.setNursery(true);
        }
    }
}
