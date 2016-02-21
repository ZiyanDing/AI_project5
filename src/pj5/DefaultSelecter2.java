package pj5;

import java.util.List;
import java.util.Map;

public class DefaultSelecter2 implements Selecter2{
	
	public DefaultSelecter2(){}
	
	public boolean checkFurther(Map<Character, Bag> bagMap, Item item, char bagName, List<Character> unassignedVar){
		return true;
	}
}
