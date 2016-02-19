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
	private String filename;
	private Map<Character, Bag> bagMap; 
	private Map<Character, Item> itemMap; 
	private int high;
	private int low;
	private boolean equity;
	private Map<Character, List<Character>> assignment;
	public CSP(String filename){
		this.filename = filename;
		this.bagMap = new HashMap<>();
		this.itemMap = new HashMap<>();
		this.high = Integer.MAX_VALUE;
		this.low = 0;
		this.equity = false;
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
		}catch(IOException e){
			System.out.println("Warning: IOException");
			System.out.println(this.filename + " is not exist");
		}
	}
	
/*	
	public void backtracking(){
		recursiveBackchecking();
	}
	
	public void recursiveBackchecking(){
		Item item = selectUnassignedVariable(itemMap);
		List<Character> domainList = orderDomainValue(item);
		for (char bagName: domainList){
			if (consistant(bagName, item, assignment)){
				Bag b = bagMap.get(bagName);
				b.addAssignment(item.getName());
				Assignment result = recursiveBackchecking(assignment);
				if (result != null){
					return result;
				}
				assignment.removeAssignment(bagName, item.getName());
			}
		}
		return null;
	}
	
	public boolean consistant(char bagName, Item item, Assignment assignment){
		List<Character> itemList = assignment.getValue(bagName);
		//gets current assignment for bag
		Bag bag = bagMap.get(bagName);

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
				if (assignment.getItemNames(c).contains(i.getName())){
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
	*/
/*	
	public boolean checkBeforeOutput(Assignment assignment){
		if(equity == false){
			return false;
		}
	}
	
	public boolean isCompleted(Assignment assignment){
		
	}
*/
/*	
	public List<Character> orderDomainValue(Item var){
		List<Character> bagNameList = new ArrayList<Character>(bagMap.keySet());
		if (var.getAllowed().size() != 0){
			return var.getAllowed();
		} else if (var.getForbidden().size() != 0){
			List<Character> forbiddenList = var.getForbidden();		
			for(char bn: bagNameList){
				if (!forbiddenList.contains(bn)){
					var.addAllowed(bn);
				}
			}
			return var.getAllowed();
		} else {
			return bagNameList;
		}
	}
*/
	public List<Character> orderDomainValue(Item var){
		List<Character> bagNameList = new ArrayList<Character>(bagMap.keySet());
		return bagNameList;
	}
	
	public void addAssignment(char bagName, char itemName){
		List<Character> itemList = assignment.get(bagName);
		if(itemList != null){
			itemList.add(itemName);
			assignment.put(bagName, itemList);
			bagMap.get(bagName);
		}else{
			assignment.put(bagName, new ArrayList<>(itemName));
		}
	}
	
	public List<Character> getValue(char bagName){
		return assignment.get(bagName);
	}
	
	public void removeAssignment(char bagName, char itemName){
		List<Character> itemList = assignment.get(bagName);
		if(itemList != null){
			itemList.remove(itemName);
			assignment.put(bagName, itemList);
		}
	}
	
	public List<Character> getItemNames(char bagName){
		return assignment.get(bagName);
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

