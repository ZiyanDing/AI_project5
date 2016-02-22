package pj5;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface Orderer {
	public List<Bag> orderDomainValue(Item var, List<Item> itemList, Map<Character, HashSet<Character>> assignment, List<Bag> bagList, int bagMax);
}
