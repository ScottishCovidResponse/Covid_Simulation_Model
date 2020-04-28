package development_v1;

public class ConstructionSite extends CommunalPlace {

	public ConstructionSite(int cindex) {
		super(cindex);
		this.transProb = 0.05;
		System.out.println("Construction site = " + super.transProb);
	}
}
