package development_v1;

public class ConstructionSite extends CommunalPlace {

	public ConstructionSite(int cindex) {
		super(cindex);
		this.transProb = super.transProb * 10 / (5000/40);
		System.out.println("Construction site = " + super.transProb);
	}
}
