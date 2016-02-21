package pj5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Item {
	private char name;
	private int weight;
	private char stored; //which bag it is stored
	private HashSet<Character> allowed; //unary inclusive constraint
	private HashSet<Character> forbidden; //unary exclusive constraint
	private HashSet<Item> friends; //equal binary constraint
	private HashSet<Item> enemies; //not equal binary constraint
	private List<Item> mutualFriends; //mutual Inclusive binary constraints
	private List<Character> mutualA;
	private List<Character> mutualB;
	
	private HashSet<Character> possibleBags;
	
	public Item(char name, int weight){
		this.name = name;
		this.weight = weight;
		this.stored = ' ';
		this.allowed = new HashSet<>();
		this.forbidden = new HashSet<>();
		this.friends = new HashSet<>();
		this.enemies = new HashSet<>();
		this.mutualFriends = new ArrayList<>();
		this.mutualA = new ArrayList<>();
		this.mutualB = new ArrayList<>();
		this.possibleBags = new HashSet<>();
	}
	
	public void addAllowed(char c){
		this.allowed.add(c);
	}
	public void addForbidden(char c){
		this.forbidden.add(c);
	}
	public void addFriends(Item i){
		this.friends.add(i);
	}
	public void addEnemies(Item i){
		this.enemies.add(i);
	}
	public void addMutualFriends(Item i){
		this.mutualFriends.add(i);
	}
	public void addMutualA(char c){
		this.mutualA.add(c);
	}
	public void addMutualB(char c){
		this.mutualB.add(c);
	}
	
	public void addStored(Bag b){
		this.stored = b.getName();
	}
	
	public void removeStored(Bag b){
		this.stored = ' ';
	}
	
	public void addPossibleBag(char b){
		this.possibleBags.add(b);
	}
	
	public char getName(){return name;}
	public int getWeight(){return weight;}
	public char getStored(){return stored;}
	public HashSet<Character> getAllowed(){return allowed;}
	public HashSet<Character> getForbidden(){return forbidden;}
	public HashSet<Item> getFriends(){return friends;}
	public HashSet<Item> getEnemies(){return enemies;}
	public List<Item> getMutualFriends(){return mutualFriends;}
	public List<Character> getMutualA(){return mutualA;}
	public List<Character> getMutualB(){return mutualB;}
	public HashSet<Character> getPossibleBags(){return possibleBags;}
	
}
