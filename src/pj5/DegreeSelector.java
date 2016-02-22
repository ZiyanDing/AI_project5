package pj5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DegreeSelector implements Selector {

	public DegreeSelector(){}

	@Override
	public Item select(Map<Character, HashSet<Character>> assignment, List<Item> itemList) {
		List<Character> assignedValues = new ArrayList<>();
		if (assignment.size() > 0){
			for (Map.Entry<Character, HashSet<Character>> entry : assignment.entrySet()){
				assignedValues.addAll(entry.getValue());
			}
		}
		Item output = null;
		int max = Integer.MIN_VALUE; //the .size() function returns nonnegative results so this is guaranteed to be surpassed at some point. The function will not return null. 
		for (Item item: itemList){
			int numConstraints = item.getFriends().size() + item.getEnemies().size() + item.getMutualFriends().size() + item.getMutualA().size() + item.getMutualB().size();
			if (!assignedValues.contains(item.getName()) && numConstraints > max){
				max = numConstraints;
				output = item;
			}
		}
		return output;
	}

}
