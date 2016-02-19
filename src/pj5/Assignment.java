package pj5;

import java.util.ArrayList;
import java.util.List;

public class Assignment {
	private char bagName;
	private List<Character> itemNames;
	private int itemNum;
	private int totalWeight;
	private int wasted;
	public Assignment(char bagName){
		this.bagName = bagName;
		this.itemNames = new ArrayList<>();
		this.itemNum = 0;
		this.totalWeight = 0;
		this.wasted = 0;
	}
	public void addItem(Item i){
		this.itemNames.add(i.getName());
	}
}
