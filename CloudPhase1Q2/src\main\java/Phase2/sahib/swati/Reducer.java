package Phase2.sahib.swati;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Reducer {
	public static void main(String[] args) throws FileNotFoundException {
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));
		String str = "";
		try {
			while ((str = bfr.readLine()) != null) {
				if (str != "" && !str.isEmpty())
					System.out.println(str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
