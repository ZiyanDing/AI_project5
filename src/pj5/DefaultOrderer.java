package pj5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DefaultOrderer implements Orderer {

	public DefaultOrderer(){} //tricking java into doing what we want 

	@Override
	public List<Bag> orderDomainValue(Item var, List<Item> itemList, Map<Character, HashSet<Character>> assignment, List<Bag> bagList, int bagMax) {
		List<Bag> output = new ArrayList<Bag>();
		for (Bag bag: bagList)
		{
			if (CSP.consistant(bag, var, assignment, bagMax))
			{
				output.add(bag);
			}
		}
		return output;
	}

}
