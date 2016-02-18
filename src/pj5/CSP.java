package pj5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CSP {
	String filename;
	Map<Character, Bag> bagMap;
	Map<Character, Item> itemMap;
	int high;
	int low;
	public CSP(String filename){
		this.filename = filename;
		this.bagMap = new HashMap<>();
		this.itemMap = new HashMap<>();
		this.high = Integer.MAX_VALUE;
		this.low = 0;
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
								i.allowed.add(str.charAt(0));
							}
						}
						itemMap.put(itemName, i);
					}else if (section == 5){ //unary exclusive
						char itemName = info[0].charAt(0);
						Item i = itemMap.get(itemName);
						for ( String str: info) {     
							if(str != info[0]){
								i.forbidden.add(str.charAt(0));
							}
						}
						itemMap.put(itemName, i);
					}else if (section == 6){ //binary equals
						char itemName1 = info[0].charAt(0);
						char itemName2 = info[1].charAt(0);
						Item i1 = itemMap.get(itemName1);
						Item i2 = itemMap.get(itemName2);
						i1.friends.add(i2);
						i2.friends.add(i1);
						itemMap.put(itemName1, i1);
						itemMap.put(itemName2, i2);
					}else if (section == 7){ //binary not equals
						char itemName1 = info[0].charAt(0);
						char itemName2 = info[1].charAt(0);
						Item i1 = itemMap.get(itemName1);
						Item i2 = itemMap.get(itemName2);
						i1.enemies.add(i2);
						i2.enemies.add(i1);
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
						i1.mutualFriends.add(i2);
						i1.mutualA.add(bagName1);
						i1.mutualB.add(bagName2);
						//i1.mutualFriends.add(i2);
						//i1.mutualA.add(bagName2);
						//i1.mutualB.add(bagName1);
						i2.mutualFriends.add(i1);
						i2.mutualA.add(bagName2);
						i2.mutualB.add(bagName1);
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

	public void test1(){
		for (Map.Entry<Character, Item> entry : itemMap.entrySet()){
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
			System.out.println("name " + entry.getValue().name);
			System.out.println("weight " + entry.getValue().weight);
			System.out.println("allowed "+ entry.getValue().allowed);
			System.out.println("forbidden " + entry.getValue().forbidden);
			System.out.println("friends " + entry.getValue().friends);
			System.out.println("enemies "+ entry.getValue().enemies);
			System.out.println("mutual Friends "+ entry.getValue().mutualFriends);
			System.out.println("mutualA "+entry.getValue().mutualA);
			System.out.println("mutualB "+entry.getValue().mutualB);
		}
		for (Entry<Character, Bag> entry : bagMap.entrySet()){
			System.out.println(entry.getKey());
			System.out.println("Bag name "+entry.getValue().name);
			System.out.println("capacity "+entry.getValue().capacity);
			System.out.println("max "+entry.getValue().max);
			System.out.println("stored "+entry.getValue().stored);
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
