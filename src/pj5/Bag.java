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
	//returns capacity after putting item i in bag
	public void reduceCapacity(Item i){
		this.capacity = this.capacity - i.getWeight();
	}
	
	public void addCapacity(Item i){
		this.capacity = this.capacity + i.getWeight();
	}
	
	public char getName(){return name;}
	public int getCapacity(){return capacity;}
	public int getMax(){return max;}
	public int getStored(){return stored;}
	
}
