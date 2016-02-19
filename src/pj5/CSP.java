package pj5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CSP {
	private String filename; //input filename
	private Map<Character, Bag> bagMap; //key is bag name
	private Map<Character, Item> itemMap; //key is item name
	
	private List<Bag> bagList;
	private List<Item> itemList;
	
	private int high; 
	private int low;
	private boolean equity;
	public CSP(String filename){
		this.filename = filename;
		this.bagMap = new HashMap<>();
		this.itemMap = new HashMap<>();
		this.high = Integer.MAX_VALUE;
		this.low = 0;
		this.equity = false;
		this.bagList = new ArrayList<>();
		this.itemList = new ArrayList<>();
	}

	public void Readfile(){
		try{
			FileReader fileReader = new FileReader(filename);
			BufferedReader br = new BufferedReader(fileReader);
			String line = null;
			int section = 0; //used to decide which section is reading
			while ((line = br.readLine()) != null){
				//System.out.println(line);
				if (line.startsWith("#####")){
					section++;
				} else {
					String[] info = line.split(" ");
					if (section == 1){	//variables
						char name = info[0].charAt(0);
						int weight = Integer.parseInt(info[1]);
						Item i = new Item(name, weight);
						itemMap.put(name, i);
					}else if (section == 2){   //domain
						char name = info[0].charAt(0);
						int weight = Integer.parseInt(info[1]);
						Bag b = new Bag(name, weight);
						bagMap.put(name, b);
					}else if (section == 3){ //fitting limits
						low = Integer.parseInt(info[0]);
						high = Integer.parseInt(info[1]);
					}else if (section == 4){ //unary inclusive
						char itemName = info[0].charAt(0);
						Item i = itemMap.get(itemName);
						for ( String str: info) {     
							if(str != info[0]){
								i.addAllowed(str.charAt(0));
							}
						}
						itemMap.put(itemName, i);
					}else if (section == 5){ //unary exclusive
						char itemName = info[0].charAt(0);
						Item i = itemMap.get(itemName);
						for ( String str: info) {     
							if(str != info[0]){
								i.addForbidden(str.charAt(0));
							}
						}
						itemMap.put(itemName, i);
					}else if (section == 6){ //binary equals
						char itemName1 = info[0].charAt(0);
						char itemName2 = info[1].charAt(0);
						Item i1 = itemMap.get(itemName1);
						Item i2 = itemMap.get(itemName2);
						i1.addFriends(i2);
						i2.addFriends(i1);
						itemMap.put(itemName1, i1);
						itemMap.put(itemName2, i2);
					}else if (section == 7){ //binary not equals
						char itemName1 = info[0].charAt(0);
						char itemName2 = info[1].charAt(0);
						Item i1 = itemMap.get(itemName1);
						Item i2 = itemMap.get(itemName2);
						i1.addEnemies(i2);
						i2.addEnemies(i1);
						itemMap.put(itemName1, i1);
						itemMap.put(itemName2, i2);
					}else if (section == 8){ //binary simultaneous			
						char itemName1 = info[0].charAt(0);
						char itemName2 = info[1].charAt(0);
						char bagName1 = info[2].charAt(0);
						char bagName2 = info[3].charAt(0);
						Item i1 = itemMap.get(itemName1);
						Item i2 = itemMap.get(itemName2);
						//?????????????????
						i1.addMutualFriends(i2);
						i1.addMutualA(bagName1);
						i1.addMutualB(bagName2);
						//i1.mutualFriends.add(i2);
						//i1.mutualA.add(bagName2);
						//i1.mutualB.add(bagName1);
						i2.addMutualFriends(i1);
						i2.addMutualA(bagName2);
						i2.addMutualB(bagName1);
						//i2.mutualFriends.add(i1);
						//i2.mutualA.add(bagName1);
						//i2.mutualB.add(bagName2);

					}else{
						System.out.println("Wrong format of information");
						br.close();
						return;
					}
				}
			}
			br.close();
			itemList = new ArrayList<>(itemMap.values());
			bagList = new ArrayList<>(bagMap.values());
			
		}catch(IOException e){
			System.out.println("Warning: IOException");
			System.out.println(this.filename + " is not exist");
		}
	}

	//following is backtracking algorithm
	public Map<Character, List<Character>> backtracking(){
		Map<Character, List<Character>> assignment = new HashMap<>();
		return recursiveBackchecking(assignment);
	}
	
	public Map<Character, List<Character>> recursiveBackchecking(Map<Character, List<Character>> assignment){
		if (isCompleted(assignment) && checkBeforeOutput()){
			return assignment;
		}
		Item item = selectUnassignedVariable(itemMap);
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
		//List<Character> itemList = assignment.get(bagName);
		//gets current assignment for bag
		//Bag bag = bagMap.get(bagName);
		char bagName = bag.getName();
		//check capacity
		if ((bag.getCapacity() - item.getWeight()) < 0){
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
		for(Item i: friends){
			if (itemList.contains(i.getName())){
				this.equity = true;
			}
		}
		//inequity
		List<Item> enemies = item.getEnemies();
		for(Item i: enemies){
			if (itemList.contains(i.getName())){
				return false;
			}
		}
		//mutual Inclusive
		List<Item> mutualFriends = item.getMutualFriends();
		for(Item i: mutualFriends){
			List<Character> iBagListA = i.getMutualA();
			List<Character> iBagListB = i.getMutualB();
			for (char c: iBagListA){
				if (assignment.get(c).contains(i.getName())){
					if (!iBagListB.contains(bagName)){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public Item selectUnassignedVariable(Map<Character, Item> itemMap){	
		Item i = (Item) itemMap.entrySet().iterator().next();
		return i;
	}
	
	
	public boolean checkBeforeOutput(){
		if(equity == false){
			return false;
		}
		for (Bag bag: bagMap.values()){
			int ratio = bag.getCapacity()/bag.getMax();
			if (ratio < 0.9 || ratio > 1){
				return false;
			}
		}
		return true;
	}

	public boolean isCompleted(Map<Character, List<Character>> assignment){
		return (assignment.size() == bagMap.size());		
	}

	public List<Bag> orderDomainValue(Item var, Map<Character, List<Character>> assignment){
		return bagList;
	}
	
	public void addAssignment(Bag bag, Item item, Map<Character, List<Character>> assignment){
		char bagName = bag.getName();
		List<Character> itemList = assignment.get(bagName);
		if(itemList != null){
			itemList.add(item.getName());
			assignment.put(bagName, itemList);		
		}else{
			assignment.put(bagName, new ArrayList<>(item.getName()));
		}
		bag.reduceCapacity(item);
		bagMap.put(bagName, bag);
	}
	

	public void removeAssignment(Bag bag,Item item, Map<Character, List<Character>> assignment){
		char bagName = bag.getName();
		List<Character> itemList = assignment.get(bagName);
		if(itemList != null){
			itemList.remove(item.getName());
			assignment.put(bagName, itemList);
		}
		bag.addCapacity(item);
		bagMap.put(bagName, bag);
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
		CSP csp = new CSP(filename);
		//CSP csp = new CSP("inputs/input24.txt");
		csp.Readfile();

		//csp.test1();
		return;
	}
}

