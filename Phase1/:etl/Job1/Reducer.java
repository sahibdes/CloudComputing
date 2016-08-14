package Testing.sahib.swati;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Reducer {
	public static void main(String[] args) throws FileNotFoundException {
		try {
			// reader for taking in the system output

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			String input;
			String word = null;
			String currentWord = null;
			StringBuilder currTweet = null;

			HashMap<String, StringBuilder> values = new HashMap<String, StringBuilder>();
			// initializing my HashMap that stores the count per day
			while ((input = br.readLine()) != null) {
				try {
					String[] parts = input.split("\t");
					word = parts[0];
					String tweet = parts[1];
					if ((currentWord != null) && (currentWord.equals(word))) {
						// check for duplicate tweets
						String[] currentTweet = parts[1].split("sahibswati");
						// String[] wordArr = tweet.split("sahibswati");
						if (!values.get(currentWord).toString()
								.contains(currentTweet[0])) {
							// totalTweet = totalTweet.append(tweet);
							currTweet = values.get(parts[0]);
							currTweet = currTweet.append("sahibpittswati")
									.append(tweet);
							values.put(parts[0], currTweet);
						}
						continue;

					} else {
						if (currentWord != null) {
							// printing out the record
							System.out.printf("%s\t%s", currentWord, values
									.get(currentWord).toString());
						}
						System.out.println();
						currTweet = null;
					}
					currentWord = word;
					currTweet = new StringBuilder(tweet);
					// totalTweet = new StringBuilder();
					// totalTweet = totalTweet.append(tweet);
					values = null;
					values = new HashMap<String, StringBuilder>();
					// initializing my HashMap
					values.put(parts[0], currTweet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (currentWord != null) {
				System.out.printf("%s\t%s", currentWord, values
						.get(currentWord).toString());
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
