package pj5;

public class Bag {
	char name;
	int capacity;
	int max;
	int stored;
	
	public Bag(char name, int capacity){
		this.name = name;
		this.capacity = capacity;
		this.max = capacity;
		this.stored = 0;	
	}
}
