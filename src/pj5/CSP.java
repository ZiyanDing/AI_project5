package pj5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CSP {
	//algorithm wrappers
	private Selector selector;
	
	private String filename; //input filename
	private Map<Character, Bag> bagMap; //key is bag name
	private Map<Character, Item> itemMap; //key is item name
	
	private List<Bag> bagList;
	private List<Item> itemList;
	
	private int high; 
	private int low;
	public CSP(String filename, Selector selector){
		this.filename = filename;
		this.selector = selector;
		this.bagMap = new HashMap<>();
		this.itemMap = new HashMap<>();
		this.high = Integer.MAX_VALUE;
		this.low = 0;
		this.bagList = new ArrayList<>();
		this.itemList = new ArrayList<>();	
	}
	public void input(){	
		FileDealer fd = new FileDealer(filename, itemMap, bagMap, low, high, itemList, bagList);
		fd.readfile();
		this.bagMap = fd.getBagMap();
		this.itemMap = fd.getItemMap();
		this.high = fd.getHigh();
		this.low = fd.getLow();
		this.bagList = fd.getBagList();
		this.itemList = fd.getItemList();
	}
	
	public void output(Map<Character, List<Character>> assignment){
		FileDealer fd = new FileDealer(filename, itemMap, bagMap, low, high, itemList, bagList);
		fd.writeFile(assignment);
	}

	//following is backtracking algorithm
	public Map<Character, List<Character>> backtracking(){
		Map<Character, List<Character>> assignment = new HashMap<>();
		assignment = recursiveBackchecking(assignment);
		return assignment;
	}
	
	public Map<Character, List<Character>> recursiveBackchecking(Map<Character, List<Character>> assignment){
		if (isCompleted(assignment)){
			System.out.println("1");
			if (checkBeforeOutput()){
				System.out.println("2");
				return assignment;
			} else {
				return null;
			}
		}
		Item item = selectUnassignedVariable(assignment, itemList);
		List<Bag> domainList = orderDomainValue(item, assignment);
		for (Bag bag: domainList){
			if (consistant(bag, item, assignment)){
				addAssignment(bag,item, assignment);
				Map<Character, List<Character>> result = recursiveBackchecking(assignment);
				if (result != null){
					return result;
				}
				removeAssignment(bag,item, assignment);
			}
		}
		return null;
	}
	
	public boolean consistant(Bag bag, Item item, Map<Character, List<Character>> assignment){
		char bagName = bag.getName();
		//check capacity
		if (bag.getCapacity() < item.getWeight()){
			return false;
		}
		//bag fit-limit
		if (bag.getStored() >= high){
			return false;
		}
		//unary constraints
		if ((item.getAllowed().size()!= 0) && !(item.getAllowed().contains(bagName))){
			return false;
		}
		if (item.getForbidden().contains(bagName)){
			return false;
		}
		//binary constrains
		//equity
		List<Item> friends = item.getFriends();
		List<Character> assignedCurBag = assignment.get(bag.getName());
		List<Character> assigned = getAssignedVar(assignment);
		
		for (Item f: friends){
			if (assigned != null && assigned.contains(f.getName())){
				if (assignedCurBag == null || !assignedCurBag.contains(f.getName())){
					return false;
				}
			}
		}
		
		//inequity
		List<Item> enemies = item.getEnemies();
		for(Item i: enemies){
			if (assignedCurBag !=null && assignedCurBag.contains(i.getName())){
				return false;
			}
		}
		
		//mutual Inclusive
		List<Item> mutualFriends = item.getMutualFriends();
		//System.out.println();
		//System.out.println("----------item " + item.getName() + "  " +item);
		//System.out.println("mutual friends " + mutualFriends);
		List<Character> bagListA = item.getMutualA();
		List<Character> bagListB = item.getMutualB();
		
		List<Item> mf = new ArrayList<>();
		List<Character> myList = new ArrayList<>();
		List<Character> hisList = new ArrayList<>();
			
		//System.out.println("myBagList " + item.getName() + "  " + bagListA);
		//System.out.println("hisBagList " + item.getName() + "  " + bagListB);
		//System.out.println("myBagList1 " + item.getName() + "  " + myList);
		//System.out.println("hisBagList2 " + item.getName() + "  " + hisList);
		
		//if mutual friend is assigned
		int index;
		for (index = 0; index < mutualFriends.size();index++){
			if(assigned.contains(mutualFriends.get(index).getName())){	
				Item f = mutualFriends.get(index);
				//System.out.println("item---" + f.getName());
				char bn = f.getStored();
				if (bn == bagListB.get(index)){
					if (bagName != bagListA.get(index)){
						return false;
					} 
					//System.out.println("=========" + bagName);
				}
			}
		}
		int idx;
		if (bagListA.contains(bag.getName())){
			for(idx = 0; idx < bagListA.size(); idx++){
				if (bagListA.get(idx).equals(bagName)){
					mf.add(mutualFriends.get(idx));
					myList.add(bagListA.get(idx));
					hisList.add(bagListB.get(idx));
				}
			}
		}
		
		//detects mutual exclusive
		for (idx = 0; idx < mf.size(); idx++){
			Item it = mf.get(idx);
			char bn = hisList.get(idx);
			int occurrences = Collections.frequency(mf, it);
			if (occurrences > 1){
				return false;
			}
			if (assigned.contains(it.getName())){
				//System.out.println("assigned  " + it.getName());
				//System.out.println("assigned 2 " + bn);
				//System.out.println("myBagList1 " + item.getName() + "  " + myList + bagName);
				//System.out.println("hisBagList2 " + item.getName() + "  " + hisList);
				List<Character> tempList = assignment.get(bn); 
				if (tempList == null || !tempList.contains(it.getName())){
					//System.out.println("false");
					return false;
				}
			} 
		} 
		return true;
	}
	
	public Item selectUnassignedVariable(Map<Character, List<Character>> assignment, List<Item> itemList){	
		return selector.select(assignment, itemList);
		/*
		List<Character> assignedValues = getAssignedVar(assignment);
		for (Item i: itemList){
			if (!assignedValues.contains(i.getName())){
				return i;
			}
		}

		return null;*/
	}
	
	public List<Character> getAssignedVar(Map<Character, List<Character>> assignment){
		List<Character> assignedValues = new ArrayList<>();
		if (assignment.size() > 0){
			for (Map.Entry<Character, List<Character>> entry : assignment.entrySet()){
				assignedValues.addAll(entry.getValue());
			}
		}
		return assignedValues;
	}
	
	
	public boolean checkBeforeOutput(){
		for (Bag bag: bagMap.values()){
			int used = bag.getMax() - bag.getCapacity();
			int requiredStored = (int) (bag.getMax() * 0.9);
			if (used < requiredStored){
				return false;
			}
			if (bag.getStored() < low){
				return false;
			}
		}
		return true;
	}

	
	public boolean isCompleted(Map<Character, List<Character>> assignment){
		return (itemList.size() == getAssignedVar(assignment).size());	
	}

	public List<Bag> orderDomainValue(Item var, Map<Character, List<Character>> assignment){
		return bagList;
	}
	
	public void addAssignment(Bag bag, Item item, Map<Character, List<Character>> assignment){
		char bagName = bag.getName();
		char itemName = item.getName();
		List<Character> tempItemList = assignment.get(bagName);
		if(tempItemList != null){
			tempItemList.add(itemName);
			assignment.put(bagName, tempItemList);		
		}else{
			List<Character> tempItemList2 = new ArrayList<>();
			tempItemList2.add(itemName);
			assignment.put(bagName, tempItemList2);
			
		}
		bag.reduceCapacity(item);
		bag.addStored();
		item.addStored(bag);
		bagMap.put(bagName, bag);
		itemMap.put(itemName, item);
	}
	

	public void removeAssignment(Bag bag,Item item, Map<Character, List<Character>> assignment){
		char bagName = bag.getName();
		List<Character> tempItemList = assignment.get(bagName);
		int index = 0;
		if(tempItemList != null){
			for (index = 0; index < tempItemList.size(); index++){
				if (tempItemList.get(index) == item.getName()){
					tempItemList.remove(index);
					break;
				}
			}
			assignment.put(bagName, tempItemList);
		}
		bag.addCapacity(item);
		bag.reduceStored();
		item.removeStored(bag);
		bagMap.put(bagName, bag);
		itemMap.put(item.getName(), item);
	}
	
	
	public void test1(){
		for (Map.Entry<Character, Item> entry : itemMap.entrySet()){
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
			System.out.println("name " + entry.getValue().getName());
			System.out.println("weight " + entry.getValue().getWeight());
			System.out.println("allowed "+ entry.getValue().getAllowed());
			System.out.println("forbidden " + entry.getValue().getForbidden());
			System.out.println("friends " + entry.getValue().getFriends());
			System.out.println("enemies "+ entry.getValue().getEnemies());
			System.out.println("mutual Friends "+ entry.getValue().getMutualFriends());
			System.out.println("mutualA "+entry.getValue().getMutualA());
			System.out.println("mutualB "+entry.getValue().getMutualB());
		}
		for (Entry<Character, Bag> entry : bagMap.entrySet()){
			System.out.println(entry.getKey());
			System.out.println("Bag name "+entry.getValue().getName());
			System.out.println("capacity "+entry.getValue().getCapacity());
			System.out.println("max "+entry.getValue().getMax());
			System.out.println("stored "+entry.getValue().getStored());
		}
		System.out.println("high "+high);
		System.out.println("low "+low);
	}

	public static void main(String[] args) throws IOException{
		if (args.length != 1){
			System.out.println("[Error]Usage: csp input.txt");
			return;
		}
		String filename = args[0];
		DefaultSelector s = new DefaultSelector();
		CSP csp = new CSP(filename, s);
		//CSP csp = new CSP("inputs/input18.txt");
		csp.input();
		Map<Character, List<Character>> assignment = new HashMap<>();
		assignment = csp.backtracking();
		csp.output(assignment);
		//csp.test1();
		return;
	}
}

