package pj5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class LCVOrderer implements Orderer {

	public LCVOrderer(){} //trick java into doing what we want
	
	//this is a class that mimics bag but has an additional parameter you can sort over.
	private class BagConstrain implements Comparable<BagConstrain>{
		public Bag bag;
		public int val;
		public BagConstrain(Bag base, int val){
			this.bag = base;
			this.val = val;
		}
		@Override
		public int compareTo(BagConstrain o) {
			return this.val - o.val;
		}
	}
	@Override
	public List<Bag> orderDomainValue(Item var, List<Item> itemList, Map<Character, HashSet<Character>> assignment, List<Bag> bagList, int bagMax) {
		HashSet<Bag> newBagList = new HashSet<Bag>();
		for (Bag b: bagList){
			if (CSP.consistant(b, var, assignment, bagMax)){
				newBagList.add(b);
			}
		}
		
		ArrayList<BagConstrain> list = new ArrayList<BagConstrain>(newBagList.size());
	
		for (Bag b: newBagList){
			CSP.addAssignmentHelper(b, var, assignment);
			list.add(new BagConstrain(b, domainSize(itemList, assignment, bagList, bagMax)));
			CSP.removeAssignmentHelper(b, var, assignment);
		}
		
		Collections.sort(list);
		
		ArrayList<Bag> output = new ArrayList<Bag>(newBagList.size());
		for (BagConstrain b: list){
			output.add(b.bag);
		}
		return output;
	}	
	//compute the sum of the cardinalities of the domains of all the varialbes under the given assignment.
	public int domainSize(List<Item> itemList, Map<Character, HashSet<Character>> assignment, List<Bag> bagList, int bagMax) {
		int total = 0;
		for (Item item: itemList){
			for (Bag bag: bagList){
				if (CSP.consistant(bag, item, assignment, bagMax)){
					total += 1;
				}
			}
		}
		return total;
	}

}
