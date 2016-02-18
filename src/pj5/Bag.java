package pj5;

public class Bag {
	private char name;
	private int capacity;
	private int max;
	private int stored;
	
	public Bag(char name, int capacity){
		this.name = name;
		this.capacity = capacity;
		this.max = capacity;
		this.stored = 0;	
	}
	
	public void setCapacity(int i){
		this.capacity = i;
	}
	
	public char getName(){return name;}
	public int getCapacity(){return capacity;}
	public int getMax(){return max;}
	public int getStored(){return stored;}
	
}
