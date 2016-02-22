package pj5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MRVAndDegreeSelector implements Selector {

	//This selector uses the MRV heuristic, but breaks ties using the degree heuristic. 
	
	public MRVAndDegreeSelector(){} //tricking java into doing what we want

	@Override
	public Item select(Map<Character, HashSet<Character>> assignment, List<Item> itemList) {
		List<Character> assignedValues = new ArrayList<>();
		if (assignment.size() > 0){
			for (Map.Entry<Character, HashSet<Character>> entry : assignment.entrySet()){
				assignedValues.addAll(entry.getValue());
			}
		}
		int minvalues = Integer.MAX_VALUE;
		Item output = null;
		for (Item item: itemList){
			if (!assignedValues.contains(item.getName()) && item.getPossibleBags().size() < minvalues){
				minvalues = item.getPossibleBags().size();
				output = item;
			}
			//use degree heuristic to break ties
			else if (!assignedValues.contains(item.getName()) && item.getPossibleBags().size() == minvalues && output != null){
				int numConstraintsItem = item.getFriends().size() + item.getEnemies().size() + item.getMutualFriends().size() + item.getMutualA().size() + item.getMutualB().size();
				int numConstraintsMax = output.getFriends().size() + output.getEnemies().size() + output.getMutualFriends().size() + output.getMutualA().size() + output.getMutualB().size();
				if (numConstraintsItem > numConstraintsMax) {
					minvalues = item.getPossibleBags().size();
					output = item;
				}
			}
		}
		return output;
	}

}
