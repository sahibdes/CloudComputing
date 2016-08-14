import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Q6 {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		try {
			HashMap<Integer, String> hmap = null;
			TreeMap<Integer, String> tmap = null;
			while ((str = bfr.readLine()) != null) {
				ArrayList<Integer> dailyMax = new ArrayList<Integer>();
				String[] s = str.split("\t");

				if (s[1].equals(args[0]) || s[1].equals(args[1])
						|| s[1].equals(args[2]) || s[1].equals(args[3])
						|| s[1].equals(args[4])) {
					hmap = new HashMap<Integer, String>();
					for (int i = 2; i < s.length; i++) {
						String[] s1 = s[i].split(":");
						dailyMax.add(Integer.parseInt(s1[1]));
					}
					hmap.put(Collections.max(dailyMax), s[1]);

					tmap = new TreeMap<Integer, String>(hmap);
					Map<Integer, String> newMap = tmap.descendingMap();
					for (Map.Entry<Integer, String> entry : newMap.entrySet()) {
						System.out.println(entry.getValue());
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
