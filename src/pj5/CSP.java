package pj5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CSP {
	//algorithm wrappers
	private Selector selector;
	private Selector2 selector2;
	private Orderer orderer;
	private String filename; //input filename
	private Map<Character, Bag> bagMap; //key is bag name
	private Map<Character, Item> itemMap; //key is item name

	private List<Bag> bagList;
	private List<Item> itemList;

	private int high; 
	private int low;
	public CSP(String filename, Selector selector, Selector2 selector2, Orderer orderer){
		this.filename = filename;
		this.selector = selector;
		this.bagMap = new HashMap<>();
		this.itemMap = new HashMap<>();
		this.high = Integer.MAX_VALUE;
		this.low = 0;
		this.bagList = new ArrayList<>();
		this.itemList = new ArrayList<>();	
		this.selector2 = selector2;
		this.orderer = orderer;
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
		assignment = recursiveBackchecking(assignment, 0, "");
		return assignment;
	}

	public Map<Character, List<Character>> recursiveBackchecking(Map<Character, List<Character>> assignment, int debugdepth, String format){
		if (isCompleted(assignment)){
			//System.out.println("1");
			if (checkBeforeOutput()){
				//System.out.println("2");
				return assignment;
			} else {
				return null;
			}
		}

		for (Item item : itemList){
			for (Bag bag: bagList){
				if (consistant(bag, item, assignment, high)){
					item.addPossibleBag(bag.getName());
				}
			}
		}
		Item item = selectUnassignedVariable(assignment, itemList);
		List<Bag> domainList = orderDomainValue(item, assignment, bagList, high);
		//int i = 0;
		for (Bag bag: domainList){
			char bagName = bag.getName();
			addAssignment(bag,item, assignment, bagMap, itemMap);
			List<Character> unassignedVar = getUnassignedVar(assignment, itemList);
			if(selector2.checkFurther(bagMap, item, bagName, unassignedVar)){ //used to implements forward checking
				Map<Character, List<Character>> result = recursiveBackchecking(assignment, debugdepth + 1, format + "\t");
				if (result != null){
					return result;
				}
			}
			removeAssignment(bag,item, assignment, bagMap, itemMap);
			/*debug stuff
			if (debugdepth <= 10){
				System.out.println(format + "At depth "+ String.valueOf(debugdepth) + " on iteration " + String.valueOf(i));
			}
			i+=1;*/
		}
		return null;
	}

	public static boolean consistant(Bag bag, Item item, Map<Character, List<Character>> assignment, int bagMax){
		char bagName = bag.getName();
		//check capacity
		if (bag.getCapacity() < item.getWeight()){
			return false;
		}
		//bag fit-limit
		if (bag.getStored() >= bagMax){
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
		HashSet<Item> friends = item.getFriends();
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
		HashSet<Item> enemies = item.getEnemies();
		for(Item i: enemies){
			if (assignedCurBag !=null && assignedCurBag.contains(i.getName())){
				return false;
			}
		}

		//mutual Inclusive
		List<Item> mutualFriends = item.getMutualFriends();
		List<Character> bagListA = item.getMutualA();
		List<Character> bagListB = item.getMutualB();

		//mutual friends
		for (int index = 0; index < mutualFriends.size(); index++){
			Item f = mutualFriends.get(index);
			if (assigned.contains(f.getName())){
				//if friend assigned to a "mutual inclusive" bag 
				if (f.getStored() == bagListB.get(index)){
					if (bagName != bagListA.get(index)){
						return false;
					}
				}
				//if current item assigned to a "mutual inclusive" bag
				if (bagName == bagListA.get(index)){
					if(f.getStored() != bagListB.get(index)){
						return false;
					}
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

	public List<Character> getUnassignedVar(Map<Character, List<Character>> assignment, List<Item> itemList){
		List<Character> unassignedVar = new ArrayList<>();
		List<Character> assignedVar = getAssignedVar(assignment);
		for(Item i: itemList){
			if (!assignedVar.contains(i.getName())){
				unassignedVar.add(i.getName());
			}
		}
		return unassignedVar;
	}

	public static List<Character> getAssignedVar(Map<Character, List<Character>> assignment){
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

	public List<Bag> orderDomainValue(Item var, Map<Character, List<Character>> assignment, List<Bag> baglist, int bagMax){
		return orderer.orderDomainValue(var, itemList, assignment, baglist, bagMax);
		/*List<Bag> output = new ArrayList<Bag>();
		for (Bag bag: baglist)
		{
			if (consistant(bag, var, assignment, bagMax))
			{
				output.add(bag);
			}
		}
		return output;*/
	}

	public static void addAssignment(Bag bag, Item item, Map<Character, List<Character>> assignment, Map<Character, Bag> bagmap, Map<Character, Item> itemmap){
		addAssignmentHelper(bag, item, assignment);
		updateMaps(bag, item, assignment, bagmap, itemmap);
	}
	//helper function to modify an assignment by putting the given item in the given bag
	public static void addAssignmentHelper(Bag bag, Item item, Map<Character, List<Character>> assignment){
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
	}
	//helper function to update appropriate maps
	public static void updateMaps(Bag bag, Item item, Map<Character, List<Character>> assignment, Map<Character, Bag> bagmap, Map<Character, Item> itemmap){
		bagmap.put(bag.getName(), bag);
		itemmap.put(item.getName(), item);
	}


	public static void removeAssignment(Bag bag,Item item, Map<Character, List<Character>> assignment, Map<Character, Bag> bagmap, Map<Character, Item> itemmap){
		removeAssignmentHelper(bag, item, assignment);
		updateMaps(bag, item, assignment, bagmap, itemmap);
	}

	public static void removeAssignmentHelper(Bag bag, Item item, Map<Character, List<Character>> assignment){
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
		if (args.length != 5){
			System.out.println("[Error]Usage: csp input.txt useMRVHeuristic useDegreeHeurstic useForwardChecking useLCVHeuristic\nwhere useMRVHeuristic, useDegreeHeuristic, useForwardChecking, and useLCVHeuristic should be booleans (either True or False).");
			return;
		}
		String filename = args[0];

		//choose whether we'll use Minimum Remaining Values Heurstic or Not
		Selector s;
		Selector2 s2;
		Orderer o;

		if (Boolean.valueOf(args[1]) && Boolean.valueOf(args[2])){
			s = new MRVAndDegreeSelector();
			//System.out.println("using MRV+Degree");
		}
		else if (Boolean.valueOf(args[2])){
			s = new DegreeSelector();
			//System.out.println("using Degree");
		}
		else if (Boolean.valueOf(args[1])){
			s = new MinimumRemainingValuesSelector();
			//System.out.println("using MRV");
		}
		else {
			s = new DefaultSelector();
			//System.out.println("using default selector");
		}
		//if using forward checking or not
		if (Boolean.valueOf(args[3])){
			s2 = new ForwardChecking();
		} else {
			s2 = new DefaultSelector2();
		}

		if (Boolean.valueOf(args[4])){
			o = new LCVOrderer();
		}
		else{
			o = new DefaultOrderer();
		}
		CSP csp = new CSP(filename, s, s2, o);
		//CSP csp = new CSP("inputs/input18.txt");
		csp.input();
		Map<Character, List<Character>> assignment = new HashMap<>();
		assignment = csp.backtracking();
		csp.output(assignment);
		//csp.test1();
		return;
	}
}

