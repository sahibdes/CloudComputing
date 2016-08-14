import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class Q8 {
	public static void main(String[] args) {
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));

		String str = null;
		try {
			TreeMap<Integer, String> tmap = null;
			while ((str = bfr.readLine()) != null) {
				String[] s = str.split("\t");
				if (s[1].equals("Interstellar_(film)")) {
					tmap = new TreeMap<Integer, String>();
					for (int i = 2; i < s.length; i++) {
						String[] s1 = s[i].split(":");
						tmap.put(Integer.parseInt(s1[1]), s1[0]);
					}
					System.out.println(tmap.lastEntry().getValue());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
