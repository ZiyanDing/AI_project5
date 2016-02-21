package pj5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ForwardChecking implements Selecter2{
	
	public ForwardChecking(){}
	
	public boolean checkFurther(Map<Character, Bag> bagMap, Item item, char bagName, List<Character> unassignedVar){
		//mutual friends
		List<Item> mutualFriends = item.getMutualFriends();
		List<Character> mutualA = item.getMutualA();
		List<Character> mutualB = item.getMutualB();		
		List<Item> unassignedFriends = new ArrayList<>();
		List<Character> hisList = new ArrayList<>();	
		//get unassigned mutual friends
		//if the item is assigned to this bag, if it is possible for its mutual friends be assigned 
		//to required bag
		for (int index = 0; index < mutualFriends.size(); index++){
			char m = mutualFriends.get(index).getName();
			if (unassignedVar.contains(m) && mutualA.get(index) == bagName){
				unassignedFriends.add(mutualFriends.get(index));
				hisList.add(mutualB.get(index));
			}
		}
		for (int index = 0; index < hisList.size(); index++){
			Item tempI = unassignedFriends.get(index);
			Bag tempB = bagMap.get(hisList.get(index));
			int occurrences = Collections.frequency(unassignedFriends, tempI);
			if (occurrences > 1){
				return false;
			}
			//if there is no capacity to place mutual friend
			if (tempB.getCapacity() < tempI.getWeight()){
				return false;
			}	
		}
		return true;
	}
}


