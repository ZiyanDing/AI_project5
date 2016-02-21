package pj5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LCVOrderer implements Orderer {

	public LCVOrderer(){} //trick java into doing what we want
	
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
	public List<Bag> orderDomainValue(Item var, List<Item> itemList, Map<Character, List<Character>> assignment, List<Bag> bagList, int bagMax) {
		ArrayList<BagConstrain> list = new ArrayList<BagConstrain>(bagList.size());
	
		for (Bag b: bagList){
			CSP.addAssignmentHelper(b, var, assignment);
			list.add(new BagConstrain(b, orderDomainValueHelper(itemList, assignment, bagList, bagMax)));
			CSP.removeAssignmentHelper(b, var, assignment);
		}
		
		Collections.sort(list);
		
		ArrayList<Bag> output = new ArrayList<Bag>(bagList.size());
		for (BagConstrain b: list){
			output.add(b.bag);
		}
		return output;
	}		
	public int orderDomainValueHelper(List<Item> itemList, Map<Character, List<Character>> assignment, List<Bag> bagList, int bagMax) {
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
	
	int domainSize(Item item, Map<Character, List<Character>> assignment, List<Bag> bagList, int bagMax){
		int output = 0;
		for (Bag bag : bagList){
			if (CSP.consistant(bag, item, assignment, bagMax)){
				output += 1;
			}
		}
		return output;
	}

}
