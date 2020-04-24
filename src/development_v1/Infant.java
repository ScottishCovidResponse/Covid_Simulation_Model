package development_v1;

public class Infant extends Person{
public Infant() {
	this.setNursery();
}
private void setNursery() {
	if(Math.random() < 0.5) super.nursery = true;
}
}
