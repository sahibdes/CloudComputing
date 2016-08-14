import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Reducer {
	public static void main(String[] args) {

		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;

		try {
			LinkedHashMap<String, Integer> hmap = new LinkedHashMap<String, Integer>();
			for (int i = 1; i < 31; i++)
				hmap.put(i < 10 ? "2014110" + i : "201411" + i, 0);

			TreeMap<String, Integer> tmap = null;
			String currentWord = null;
			int totalCount = 0;
			int currCount = 0;

			while ((str = bfr.readLine()) != null) {
				String[] parts = str.split("\t");
				String[] datePart = parts[2].split("pagecounts-");
				String date = (String) datePart[1].subSequence(0, 8);
				String prevWord = parts[0];
				int count = Integer.parseInt(parts[1]);

				if ((currentWord != null) && (currentWord.equals(prevWord))) {
					totalCount = totalCount + count;
					currCount = hmap.get(date);
					currCount = count + currCount;
					hmap.put(date, currCount);
				} else {
					if (currentWord != null) {
						if (totalCount > 100000) {
							System.out.printf("%d\t%s\t", totalCount,
									currentWord);

							tmap = new TreeMap<String, Integer>(hmap);
							for (Map.Entry<String, Integer> entry : tmap
									.entrySet()) {
								System.out.printf("%s:%d\t", entry.getKey(),
										entry.getValue());
							}
							System.out.println();
						}
						totalCount = 0;
					}

					hmap = null;
					currentWord = prevWord;
					currCount = count;
					totalCount = totalCount + count;

					hmap = new LinkedHashMap<String, Integer>();
					for (int i = 1; i < 31; i++)
						hmap.put(i < 10 ? "2014110" + i : "201411" + i, 0);

					tmap = new TreeMap<String, Integer>(hmap);
					hmap.put(date, currCount);
				}
			}

			if ((currentWord != null) && (totalCount > 100000)) {
				System.out.printf("%d\t%s\t", totalCount, currentWord);

				tmap = new TreeMap<String, Integer>(hmap);
				for (Map.Entry<String, Integer> entry : tmap.entrySet()) {
					System.out.printf("%s:%d\t", entry.getKey(),
							entry.getValue());
				}
				System.out.println();
			}

			bfr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}