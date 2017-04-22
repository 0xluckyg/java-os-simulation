public class Resource {
	private int max;
	private int has;
	
	//Resources to keep track of (has type and number of units)
	public Resource(int has) {
		this.setHas(has);
	}

	public int getHas() {
		return has;
	}

	public void setHas(int has) {
		this.max = has;
		this.has = has;
	}
	
	public void decrementHas(int amount) {
		this.has -= amount;
	}
	
	public void incrementHas(int amount) {
		this.has += amount;
	}

	public int getMax() {
		return max;
	}	
}