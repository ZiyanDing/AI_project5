package pj5;

import java.util.List;
import java.util.Map;

public interface Orderer {
	public List<Bag> orderDomainValue(Item var, Map<Character, List<Character>> assignment, List<Bag> bagList, int bagMax);
}
