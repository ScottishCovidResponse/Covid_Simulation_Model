package development_v1;

public class Office extends CommunalPlace{
	public Office(int cindex) {
		super(cindex);
		this.transProb = super.transProb * (10 / (10000 / 400));
	}

}
