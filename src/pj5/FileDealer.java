package pj5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileDealer {
	String filename;
	Map<Character, Item> itemMap;
	Map<Character, Bag> bagMap;
	int low;
	int high;
	List<Item> itemList;
	List<Bag> bagList;
	
	public FileDealer(String filename, Map<Character, Item> itemMap, Map<Character, Bag> bagMap, int low, int high, List<Item> itemList, List<Bag> bagList){
		this.filename = filename;
		this.itemMap = itemMap;
		this.bagMap = bagMap;
		this.low = low;
		this.high = high;
		this.itemList = itemList;
		this.bagList = bagList;
	}
	
	public Map<Character, Item> getItemMap(){return itemMap;}
	public Map<Character, Bag> getBagMap(){return bagMap;}
	public int getLow(){return low;}
	public int getHigh(){return high;}
	public List<Item> getItemList(){return itemList;}
	public List<Bag> getBagList(){return bagList;}
	
	public void readfile(){
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
						int i;
						int size = info.length;
						List<Character> itemName = new ArrayList<>();
						for(i=0; i<size; i++){
							char itemN = info[i].charAt(0);
							itemName.add(itemN);
						}
						for(i=0; i<size;i++){
							char itemN = itemName.get(i);
							Item it = itemMap.get(itemN);
							for(char c: itemName){
								if (c != it.getName()){
									it.addFriends(itemMap.get(c));
								}
							}
							itemMap.put(itemN, it);
						}
						
					}else if (section == 7){ //binary not equals
						int i;
						int size = info.length;
						List<Character> itemName = new ArrayList<>();
						for(i=0; i<size; i++){
							char itemN = info[i].charAt(0);
							itemName.add(itemN);
						}
						for(i=0; i<size;i++){
							char itemN = itemName.get(i);
							Item it = itemMap.get(itemN);
							for(char c: itemName){
								if (c != it.getName()){
									it.addEnemies(itemMap.get(c));
								}
							}
							itemMap.put(itemN, it);
						}
						
					}else if (section == 8){ //binary simultaneous			
						char itemName1 = info[0].charAt(0);
						char itemName2 = info[1].charAt(0);
						char bagName1 = info[2].charAt(0);
						char bagName2 = info[3].charAt(0);
						Item i1 = itemMap.get(itemName1);
						Item i2 = itemMap.get(itemName2);
						i1.addMutualFriends(i2);
						i1.addMutualA(bagName1);
						i1.addMutualB(bagName2);
						i1.addMutualFriends(i2);
						i1.addMutualA(bagName2);
						i1.addMutualB(bagName1);
						
						i2.addMutualFriends(i1);
						i2.addMutualA(bagName1);
						i2.addMutualB(bagName2);
						i2.addMutualFriends(i1);
						i2.addMutualA(bagName2);
						i2.addMutualB(bagName1);

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
			//System.out.println("itemList " + itemList.size());
			//System.out.println("bagList " + bagList.size());
		}catch(IOException e){
			System.out.println("Warning: IOException");
			System.out.println(this.filename + " is not exist");
		}
	}
	
	public void writeFile(Map<Character, List<Character>> assignment){
		File file = new File("output.txt");
		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			if (assignment == null){
				writer.write("No possible assignment.");
				writer.flush();
				writer.close();
				return;
			}
			for (Map.Entry<Character, List<Character>> entry : assignment.entrySet()){
				
				char bagName = entry.getKey();
				Bag bag = bagMap.get(bagName);
				int capacity = bag.getCapacity();
				int max = bag.getMax();
				List<Character> itemNames = entry.getValue();
				writer.write(bagName + " ");
				//System.out.println("assignment  " + bagName + " " +itemNames);
				for (char c: itemNames){
					writer.write(c + " ");
				}
				writer.write("\n");
				writer.write("Number of items: " + itemNames.size() + "\n");
				writer.write("Total weight: " + (max-capacity) +"/" + max + "\n");
				writer.write("Wasted capacity: " + capacity);
				writer.write("\n\n");
			}
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
