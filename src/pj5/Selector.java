package pj5;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface Selector {
	Item select(Map<Character, HashSet<Character>> assignment, List<Item> itemList);
}
