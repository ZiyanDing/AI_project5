package pj5;

import java.util.List;
import java.util.Map;

public interface Selecter2 {
	boolean checkFurther(Map<Character, Bag> bagMap, Item item, char bagName, List<Character> unassignedVar); 
}
