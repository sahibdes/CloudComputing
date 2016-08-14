import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Q5 {

	public static void main(String[] args) {
		args = new String[2];
		args[0] = "Google";
		args[1] = "Amazon.com";
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));

		String str = null;
		HashMap<String, Integer> hmapG = null;
		HashMap<String, Integer> hmapA = null;
		try {
			while ((str = bfr.readLine()) != null) {
				String[] s = str.split("\t");
				if (s[1].equals(args[0])) {
					hmapG = new HashMap<String, Integer>();
					for (int i = 2; i < s.length; i++) {
						String[] sGoogle = s[i].split(":");
						hmapG.put(sGoogle[0], Integer.parseInt(sGoogle[1]));
					}
				}

				for (Map.Entry<String, Integer> entry : hmapG.entrySet()) {
					System.out
							.println(entry.getKey() + "\t" + entry.getValue());
				}
				if (s[1].equals(args[1])) {
					hmapA = new HashMap<String, Integer>();
					for (int i = 2; i < s.length; i++) {
						String[] sAmazon = s[i].split(":");
						hmapA.put(sAmazon[0], Integer.parseInt(sAmazon[1]));
					}
				}
			}

			int countDays = 0;

			for (Map.Entry<String, Integer> entryG : hmapG.entrySet()) {
				for (Map.Entry<String, Integer> entryA : hmapA.entrySet()) {
					if (entryG.getKey().equals(entryA.getKey())) {
						if (entryG.getValue() > entryA.getValue())
							++countDays;
					}
				}
			}
			System.out.println(countDays);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
