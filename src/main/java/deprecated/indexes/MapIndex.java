package deprecated.indexes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import deprecated.model.books.YearBook;

public class MapIndex {

	private static Map<YearBook, Map<Class<? extends MapIndex>, Map<String, Long>>> computedIndexes = new HashMap<>();
	
	protected static Optional<Map<String, Long>> alreadyComputedIndex(YearBook yb,Class<? extends MapIndex> index){
		Optional<Map<String, Long>> result = (computedIndexes.containsKey(yb) && computedIndexes.get(yb).containsKey(index))? Optional.of(computedIndexes.get(yb).get(index)) : Optional.empty();
		return result;
	}
	
	protected static void addComputedIndex(YearBook yb,Class<? extends MapIndex> index,Map<String, Long> map) {
		if(!computedIndexes.containsKey(yb)) {
			computedIndexes.put(yb, new HashMap<>());
		}
		computedIndexes.get(yb).put(index, map);
	}
}
