package development_v1;

public class Hospital extends CommunalPlace{

	public Hospital(int cindex) {
		super(cindex);
		this.transProb = super.transProb * (15 / (5000 / 10));
		this.startDay = 3; //Bodge set start day to a different day of the week to help syncing
		this.endDay = 7;
	}
	
}
