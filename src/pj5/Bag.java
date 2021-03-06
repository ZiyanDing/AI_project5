package pj5;

public class Bag {
	private char name;
	private int capacity;
	private int max;
	private int stored;
	
	public Bag(char name, int capacity){
		this.name = name;
		this.capacity = capacity; //rest capacity that can be used to store
		this.max = capacity; //maximum capacity
		this.stored = 0; //number of items stored in bag
	}
	//returns capacity after putting item i in bag
	public void reduceCapacity(Item i){
		this.capacity = this.capacity - i.getWeight();
	}
	
	public void addCapacity(Item i){
		this.capacity = this.capacity + i.getWeight();
	}
	
	public void addStored(){
		this.stored ++;
	}
	public void reduceStored(){
		this.stored--;
	}
	public char getName(){return name;}
	public int getCapacity(){return capacity;}
	public int getMax(){return max;}
	public int getStored(){return stored;}
	
}
