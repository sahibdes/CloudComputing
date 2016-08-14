import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Cloud1 {
	public void removeNonEnglish() throws IOException {
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		try {
			BufferedReader bfr = new BufferedReader(new FileReader(new File(
					"pagecounts-20141101-000000")));
			int val = 1;
			String str = null;
			// filtering conditions
			while ((str = bfr.readLine()) != null) {
				if (str.startsWith("en ")) {
					String[] s;
					s = str.split("\\s");
					if (!s[1].startsWith("Media:")
							&& !s[1].startsWith("Special:")
							&& !s[1].startsWith("Talk:")
							&& !s[1].startsWith("User:")
							&& !s[1].startsWith("User_talk:")
							&& !s[1].startsWith("Project:")
							&& !s[1].startsWith("Project_talk:")
							&& !s[1].startsWith("File:")
							&& !s[1].startsWith("File_talk:")
							&& !s[1].startsWith("MediaWiki:")
							&& !s[1].startsWith("MediaWiki_talk:")
							&& !s[1].startsWith("Template:")
							&& !s[1].startsWith("Template_talk:")
							&& !s[1].startsWith("Help:")
							&& !s[1].startsWith("Help_talk:")
							&& !s[1].startsWith("Category:")
							&& !s[1].startsWith("Category_talk:")
							&& !s[1].startsWith("Portal:")
							&& !s[1].startsWith("Wikipedia:")
							&& !s[1].startsWith("Wikipedia_talk:")) {
						if ((!(s[1].charAt(0) >= 97 && s[1].charAt(0) <= 122))) {
							if (!s[1].endsWith(".jpg")
									&& !s[1].endsWith(".gif")
									&& !s[1].endsWith(".png")
									&& !s[1].endsWith(".JPG")
									&& !s[1].endsWith(".GIF")
									&& !s[1].endsWith(".PNG")
									&& !s[1].endsWith(".txt")
									&& !s[1].endsWith(".ico")) {
								if (!s[1].contains("404_error")
										&& !s[1].contains("Main_Page")
										&& !s[1].contains("Hypertext_Transfer_Protocol")
										&& !s[1].contains("Search")) {
									int i = Integer.valueOf(s[2]);
									// if valid add to hash map
									hmap.put(s[1], i);
									val++;
									str = null;
									s = null;
								}
							}
						}
					}
				}
			}
			// close buffer reader
			bfr.close();

			File f = new File("output");
			if (f.exists() && !f.isDirectory())
				f.delete();

			FileWriter writer = new FileWriter(f);
			BufferedWriter bfrWriter = new BufferedWriter(writer);
			// sort and create the file using hash map
			for (Map.Entry<String, Integer> entry : sortMap(hmap).entrySet()) {
				bfrWriter.write(entry.getKey() + '\t' + entry.getValue());
				bfrWriter.newLine();
			}

			// close the writer
			bfrWriter.flush();
			bfrWriter.close();
			System.out.println(val);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Method to sort map by values
	@SuppressWarnings({ "rawtypes", "hiding" })
	public <String extends Comparable, Integer extends Comparable> Map<String, Integer> sortMap(
			Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> mapEntries = new LinkedList<Map.Entry<String, Integer>>(
				map.entrySet());

		Collections.sort(mapEntries,
				new Comparator<Map.Entry<String, Integer>>() {

					@SuppressWarnings("unchecked")
					@Override
					public int compare(Entry<String, Integer> val1,
							Entry<String, Integer> val2) {
						return val1.getValue().compareTo(val2.getValue());
					}
				});

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : mapEntries)
			sortedMap.put(entry.getKey(), entry.getValue());

		return sortedMap;
	}

	public static void main(String[] args) throws IOException {
		Cloud1 c = new Cloud1();
		c.removeNonEnglish();
	}
}
