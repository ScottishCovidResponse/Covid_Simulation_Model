package development_v1;

public class Nursery extends CommunalPlace {
	public Nursery(int cindex) {
		super(cindex);
		this.transProb = super.transProb * 30 / (34000 / 50);
	}
}
