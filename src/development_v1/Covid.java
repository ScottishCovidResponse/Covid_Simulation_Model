package development_v1;
import org.apache.commons.math3.distribution.*;
public class Covid {
	private int meanLatentPeriod;
	private int meanAsymptomaticPeriod;
	private int meanP1;
	private int meanP2;
	private double transmissionProb;
	
	private int latentPeriod;
	private double asymptomaticPeriod;
	private double p1;
	private double p2;
	
	private double mortalityRate;
	private int infCounter;
	
	private boolean latent;
	private boolean asymptomatic;
	private boolean phase1;
	private boolean phase2;
	private boolean recovered;
	private boolean dead;

	
	private Person ccase;
	public Covid(Person ccase) {
		this.meanLatentPeriod = 7;
		this.meanAsymptomaticPeriod = 1;
		this.meanP1 = 5;
		this.meanP2 = 10;
		
		this.ccase = ccase;
		this.mortalityRate = 0.02;
		
		this.infCounter = 0;
		this.setPeriods();

	}
	
	private void setPeriods() {
		this.latentPeriod = new PoissonDistribution(this.meanLatentPeriod).sample();
		this.asymptomaticPeriod = new PoissonDistribution(this.meanAsymptomaticPeriod).sample();
		this.p1 = new PoissonDistribution(this.meanP1).sample();
		this.p2 = new PoissonDistribution(this.meanP2).sample();
		if((this.ccase instanceof Infant || this.ccase instanceof Child) & Math.random() > 0.02) this.p2 = 0;
		if((this.ccase instanceof Adult) & Math.random() > 0.15) this.p2 = 0; 
		if((this.ccase instanceof Pensioner) & Math.random() > 0.8) this.p2 = 0; 

	}
	
	public String stepInfection() {
		this.infCounter ++;
	//	System.out.println("Inf counter"  + this.infCounter + " Latent period = " + this.latentPeriod);
		String status = "Latent";
		if((this.latentPeriod * 24) > this.infCounter) {
			this.latent = true;
		//	System.out.println("Latent");
		}
		else if(((this.latentPeriod + this.asymptomaticPeriod) * 24) > this.infCounter) {
			this.asymptomatic = true;
			status = "Asymptomatic";
		//	System.out.println("Asymptomatic");

		}
		else if((this.latentPeriod + this.asymptomaticPeriod + this.p1) * 24 > this.infCounter) {
			this.phase1 = true;
			status = "Phase 1";
		}
		else if((this.latentPeriod + this.asymptomaticPeriod + this.p1 + this.p2) * 24 > this.infCounter) {
			this.phase2 = true;
			double rVal = Math.random();
			if(rVal < this.mortalityRate / 24) {
				this.dead = true;
				status = "Dead";
				System.out.println("DEAD");
			}
			if(rVal >= this.mortalityRate / 24) {
				status = "Phase 2";
			//	System.out.println("Phase 2");
			}
		}
		else if((this.latentPeriod + this.asymptomaticPeriod + this.p1 + this.p2) * 24 <= this.infCounter){
			this.recovered = true;
			status = "Recovered";
			System.out.println("RECOVERED");

		}
		return status;
		
	}
	
	
	
}
