package pj5;

import java.util.List;
import java.util.Map;

public class DefaultSelector2 implements Selector2{
	
	public DefaultSelector2(){}

	public boolean checkFurther(Map<Character, Bag> bagMap, Item item, char bagName, List<Character> unassignedVar){
		return true;
	}
}
