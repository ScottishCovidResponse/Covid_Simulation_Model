package development_v1;

public class School extends CommunalPlace{
	public School(int cindex) {
		super(cindex);
		int startTime = 9;
		int endTime = 15;
		this.transProb = super.transProb * 30 / (34000 / 50); // These transmission probabilities are long winded, but they do make sense
	//	this.keyPremises = false;
	}
}
