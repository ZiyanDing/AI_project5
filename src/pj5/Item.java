package pj5;

import java.util.ArrayList;
import java.util.List;

public class Item {
	char name;
	int weight;
	char stored;
	List<Character> allowed;
	List<Character> forbidden;
	List<Item> friends;
	List<Item> enemies;
	List<Item> mutualFriends;
	List<Character> mutualA;
	List<Character> mutualB;
	public Item(char name, int weight){
		this.name = name;
		this.weight = weight;
		this.stored = 'N';
		this.allowed = new ArrayList<>();
		this.forbidden = new ArrayList<>();
		this.friends = new ArrayList<>();
		this.enemies = new ArrayList<>();
		this.mutualFriends = new ArrayList<>();
		this.mutualA = new ArrayList<>();
		this.mutualB = new ArrayList<>();
	}
}
