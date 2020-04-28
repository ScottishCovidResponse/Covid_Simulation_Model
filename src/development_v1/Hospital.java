package development_v1;

public class Hospital extends CommunalPlace{

	public Hospital(int cindex) {
		super(cindex);
		this.transProb = super.transProb * (15 / (7500 / 10));
		this.startDay = 1;
		this.endDay = 7;
	}
}
