package pj5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/*This selector is the one that simply chooses the first variable which hasn't been assigned a value.
 * Useful mainly for comparing with smarter heuristics to see which is faster.*/

public class DefaultSelector implements Selector {

	public DefaultSelector(){} //tricking java into doing what we want

	@Override
	public Item select(Map<Character, HashSet<Character>> assignment, List<Item> itemList) {
		List<Character> assignedValues = new ArrayList<>();
		if (assignment.size() > 0){
			for (Map.Entry<Character, HashSet<Character>> entry : assignment.entrySet()){
				assignedValues.addAll(entry.getValue());
			}
		}
		for (Item i: itemList){
			if (!assignedValues.contains(i.getName())){
				return i;
			}
		}

		return null;
	}

}
