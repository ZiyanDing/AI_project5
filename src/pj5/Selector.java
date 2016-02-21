package pj5;

import java.util.List;
import java.util.Map;

public interface Selector {
	Item select(Map<Character, List<Character>> assignment, List<Item> itemList);
}
