import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Q9 {
	public static void main(String[] args) {
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		try {
			while ((str = bfr.readLine()) != null) {
				String[] s = null;
				s = str.split("\t");
				String[] s1 = s[2].split(":");
				if (s1[1].equals("0")) {
					System.out.println(s[1]);
					break;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}