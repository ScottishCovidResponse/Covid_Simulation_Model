package development_v1;

public class ConstructionSite extends CommunalPlace {

	public ConstructionSite(int cindex) {
		super(cindex);
		this.transProb = super.transProb * 10 / (5000/100);
		System.out.println("Construction site = " + this.transProb);
		this.keyProb = 0.5;
		if(Math.random() > this.keyProb) this.keyPremises = false;

	}
}
