import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SortValuesDesc {

	public static void main(String[] args) {
		TreeMap<String, Double> hmap = new TreeMap<String, Double>();
		hmap.put("a", 0.14);
		hmap.put("b", 0.004);
		hmap.put("c", 0.24);
		hmap.put("d", 0.34);
		hmap.put("e", 0.24);

		for (Map.Entry<String, Double> entry : sortMap(hmap).entrySet()) {
			System.out.println(entry.getKey() + '\t' + entry.getValue());
		}
	}

	// Method to sort map by values
	@SuppressWarnings({ "rawtypes", "hiding" })
	public static <String extends Comparable, Double extends Comparable> Map<String, Double> sortMap(
			Map<String, Double> map) {
		List<Map.Entry<String, Double>> mapEntries = new LinkedList<Map.Entry<String, Double>>(
				map.entrySet());

		Collections.sort(mapEntries,
				new Comparator<Map.Entry<String, Double>>() {

					@SuppressWarnings("unchecked")
					@Override
					public int compare(Entry<String, Double> val1,
							Entry<String, Double> val2) {
						return val2.getValue().compareTo(val1.getValue());
					}
				});

		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> entry : mapEntries)
			sortedMap.put(entry.getKey(), entry.getValue());

		return sortedMap;
	}
}
