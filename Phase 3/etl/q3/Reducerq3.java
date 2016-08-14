package Testing.sahib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Reducerq3 {
	static HashMap<String, String> hmap = new HashMap<String, String>();
	static HashMap<String, String> hmap1 = new HashMap<String, String>();

	public static void main(String[] args) throws ParseException, IOException,
			SQLException {
		BufferedReader bfr = new BufferedReader(
				new FileReader(new File("test")));

		StringBuilder value = new StringBuilder();
		StringBuilder output = new StringBuilder();
		String line = "";
		String user_id = "";

		while ((line = bfr.readLine()) != null) {
			String[] parts = line.split("\t");

			TreeMap<String, String> tmap = new TreeMap<String, String>();
			TreeMap<String, String> tmap1 = new TreeMap<String, String>();

			if (user_id.isEmpty() || !user_id.equals(parts[0])) {
				if (!user_id.isEmpty()) {
					output.append(user_id + "\t");
					for (Entry entry : hmap.entrySet()) {
						String[] arrVal = entry.getValue().toString()
								.split("\t");

						for (String st : arrVal) {

							// get the retwettid
							String[] arrRetweets = st.split(" ");
							// now on this got id check for same retweetids
							if (hmap1.isEmpty()) {
								hmap1.put(arrRetweets[0], arrRetweets[1]
										.split("\\(")[1].split("\\)")[0]);

							} else {
								if (hmap1.containsKey(arrRetweets[0])) {
									hmap1.put(
											arrRetweets[0],
											hmap1.get(arrRetweets[0])
													+ " "
													+ arrRetweets[1]
															.split("\\(")[1]
															.split("\\)")[0]);
								} else {
									hmap1.put(arrRetweets[0], arrRetweets[1]
											.split("\\(")[1].split("\\)")[0]);
								}
							}
						}
					}

					int count_0 = 0;
					int count_1 = 0;
					for (Entry entry : hmap1.entrySet()) {
						String[] arrVals = entry.getValue().toString()
								.split(" ");
						for (String st : arrVals) {
							if (st.equals("0"))
								count_0++;
							else
								count_1++;
						}

						if (count_0 == count_1) {
							tmap.put(entry.getKey().toString() + "*", "*" + " "
									+ count_0 + " " + entry.getKey().toString());
						} else if (count_0 < count_1) {
							if (count_0 > 0)
								tmap.put(entry.getKey().toString() + "*", "*"
										+ " " + count_0 + " "
										+ entry.getKey().toString());

							tmap.put(entry.getKey().toString() + "-", "-" + " "
									+ (count_1 - count_0) + " "
									+ entry.getKey().toString());

						} else if (count_0 > count_1) {
							if (count_1 > 0)
								tmap.put(entry.getKey().toString() + "*", "*"
										+ " " + count_0 + " "
										+ entry.getKey().toString());

							tmap.put(entry.getKey().toString() + "+", "+" + " "
									+ (count_0 - count_1) + " "
									+ entry.getKey().toString());
						}

						// now find top 5 based on values and send to database
						for (Entry<String, String> entry1 : tmap.entrySet()) {

						}

						count_0 = 0;
						count_1 = 0;
						// System.out.println(output.toString());
						// String[] arrStr = output.toString().split("\t");
						tmap = new TreeMap<>();
					}

					// sort * then value then + then value then - then value

					System.out.println(output.toString());
					hmap1 = new HashMap<String, String>();
					value = new StringBuilder();
					hmap = new HashMap<String, String>();
					output = new StringBuilder();
					tmap = new TreeMap<>();
				}

				value.append(parts[1]);
				user_id = parts[0];
				hmap.put(user_id, value.toString());

			} else {
				value.append("\t" + parts[1]);
				hmap.put(user_id, value.toString());
			}

		}
	}

	// Method to sort map by values
	@SuppressWarnings({ "rawtypes", "hiding" })
	public static <String extends Comparable, String1 extends Comparable> Map<String, String> sortMap(
			Map<String, String> map) {
		List<Map.Entry<String, String>> mapEntries = new LinkedList<Map.Entry<String, String>>(
				map.entrySet());

		Collections.sort(mapEntries,
				new Comparator<Map.Entry<String, String>>() {

					@SuppressWarnings("unchecked")
					@Override
					public int compare(Entry<String, String> val1,
							Entry<String, String> val2) {

						return val1.getValue().toString().split(" ")[0]
								.compareTo(val2.getValue().toString());
					}
				});

		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> entry : mapEntries)
			sortedMap.put(entry.getKey(), entry.getValue());

		return sortedMap;
	}
}
