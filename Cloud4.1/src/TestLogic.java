import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;

public class TestLogic {

	public static void main(String[] args) throws IOException {
		BufferedReader str = new BufferedReader(
				new FileReader(new File("test")));
		String line = "";
		String keyText = "";
		String valText = "";
		StringBuilder sb = new StringBuilder();
		while ((line = str.readLine()) != null) {

			String[] arrWords = line.replaceAll("\t", " ").split(" ");

			if (Integer.valueOf(arrWords[arrWords.length - 1]) > 2) {
				// break and get the last word

				String key1 = "";
				for (int i = 0; i < arrWords.length - 2; i++)
					key1 += arrWords[i] + " ";

				String value1 = arrWords.length > 2 ? arrWords[arrWords.length - 2]
						+ "_" + arrWords[arrWords.length - 1]
						: arrWords[arrWords.length - 1];

				if (arrWords.length > 2) {
					// keyText.set(key1);
					// valText.set(value1);
					System.out.println(key1.trim() + " " + value1.trim());
					// context.write(keyText, valText);
				}

				String key2="";
				for (int i = 0; i < arrWords.length - 2; i++)
					key2 += arrWords[i] + " ";

				value1 = arrWords[arrWords.length - 1];
				// keyText.set(key1);
				// valText.set(value1);
				System.out.println(key1.trim() + " " + value1.trim());
			}
		}
	}
}
