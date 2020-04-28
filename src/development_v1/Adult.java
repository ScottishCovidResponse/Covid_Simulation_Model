package development_v1;

public class Adult extends Person{

	public Adult() {
		this.setProfession();
	}
	// Allocates adults to different professions
	public void setProfession() {
		double cVal = Math.random();
		//This needs repopulating with the real proportions in different groups
		if(cVal < 0.2) super.officeWorker = true;
		else if(cVal - 0.2 < 0.15) super.shopWorker = true;
		else if(cVal - 0.2 - 0.15 < 0.15) super.hospitalWorker = true;
		else if(cVal - 0.2 - 0.15 -0.15 < 0.1) super.constructionWorker = true;
		else if(cVal - 0.2 - 0.15 -0.15 - 0.1 < 0.2) super.teacher = true;
				
	}
}
