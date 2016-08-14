import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Q10 {

	public static void main(String[] args) {
		int countMaxArt = 0;
		int globalMax = 0;
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));
		String str = null;
		try {
			//while ((str = bfr.readLine()) != null) {
			
				int checkVal = 0;
				String s[] = str.split("\t");
				int count = 1;
				for (int i = 2; i < s.length; i++) {
					String[] dateView = s[i].split(":");
					if (checkVal <= Integer.parseInt(dateView[1])) {
						checkVal = Integer.parseInt(dateView[1]);
						count++;
					} else {
						if (globalMax < count) {
							globalMax = count;
						}

						else if (globalMax == count)
							countMaxArt++;
					}

				}
		//	}
			System.out.println(countMaxArt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
