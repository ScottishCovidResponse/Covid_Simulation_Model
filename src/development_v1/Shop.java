package development_v1;

public class Shop extends CommunalPlace{
	public Shop(int cindex) {
		super(cindex);
		this.transProb = super.transProb * 5 / (7500 / 400);
	}
}
