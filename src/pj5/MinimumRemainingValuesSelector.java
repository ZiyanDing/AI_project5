package pj5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinimumRemainingValuesSelector implements Selector {
	
	public MinimumRemainingValuesSelector(){} //tricking java into doing what we want

	@Override
	public Item select(Map<Character, List<Character>> assignment, List<Item> itemList) {
		List<Character> assignedValues = new ArrayList<>();
		if (assignment.size() > 0){
			for (Map.Entry<Character, List<Character>> entry : assignment.entrySet()){
				assignedValues.addAll(entry.getValue());
			}
		}
		int minvalues = Integer.MAX_VALUE;
		Item output = null;
		for (Item i: itemList){
			if (!assignedValues.contains(i.getName()) && i.getPossibleBags().size() < minvalues){
				minvalues = i.getPossibleBags().size();
				output = i;
			}
		}
		return output;
	}

}
