import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class Q7 {
	public static void main(String[] args) {
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		try {
			TreeMap<Integer, String> tmap = new TreeMap<Integer, String>();
			while ((str = bfr.readLine()) != null) {
				String[] s = str.split("\t");
				if (s[1].equals(args[0]) || s[1].equals(args[1])
						|| s[1].equals(args[2]) || s[1].equals(args[3])
						|| s[1].equals(args[4])) {
					System.out.println(s[0] + "\t" + s[1]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
