import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Mapper {
	static TreeSet<String> bannedSet = new TreeSet<String>();
	static LinkedHashMap<String, Integer> afinnMap = new LinkedHashMap<String, Integer>();

	public static void main(String[] args) throws IOException {
		File f = new File("banned.txt");
		BufferedReader bfr2 = new BufferedReader(new FileReader(f));
		String bannedWords = null;

		// read banned words and apply ROT13D Algorithm
		while ((bannedWords = bfr2.readLine()) != null) {
			bannedWords = bannedWords.toLowerCase();
			StringBuffer decoded = new StringBuffer();
			for (int i = 0; i < bannedWords.length(); i++) {
				int x = (((int) bannedWords.charAt(i)));
				if (x >= 97 && x <= 109)
					x += 13;
				else if (x >= 110 && x <= 122)
					x -= 13;
				decoded.append((char) (x));
			}
			bannedSet.add(decoded.toString());
		}

		f = new File("afinn.txt");
		BufferedReader bfr1;
		bfr1 = new BufferedReader(new FileReader(f));
		String sentimentWords = null;

		// read AFINN words
		while ((sentimentWords = bfr1.readLine()) != null) {
			String[] arrSentimentWords = sentimentWords.split("\t");

			if (!arrSentimentWords[0].contains(" "))
				afinnMap.put(arrSentimentWords[0],
						Integer.valueOf(arrSentimentWords[1]));
		}

		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					System.in));
			String str = "";
			PrintWriter pwr = new PrintWriter(new File("output.txt"));
			String output = "";
			while ((str = bfr.readLine()) != null) {
				output = jsonToString(str);
				if (!output.isEmpty()) {
					pwr.write(output);
					pwr.write("\n");
				}
			}
			pwr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String jsonToString(String tweet)
			throws UnsupportedEncodingException {
		if (!tweet.isEmpty()) {
			// containing the json from files
			@SuppressWarnings("unchecked")
			JsonParser parser = new JsonParser();

			JsonObject tweetMap = parser.parse(tweet).getAsJsonObject();

			String tweetVal = tweetMap.get("tweet_text").getAsString();			

			String[] tweetTextWords = tweetMap.get("tweet_text").getAsString()
					.split("\\P{Alpha}+");
			int tweetScore = 0;
			// loop and censor the words
			for (String word : tweetTextWords) {
				StringBuilder b = new StringBuilder();
				if (afinnMap.keySet().contains(word.toLowerCase())) {
					tweetScore += afinnMap.get(word.toLowerCase());
				}

				if (bannedSet.contains(word.toLowerCase())) {
					int k = word.length();
					for (int i = 0; i < k; i++) {
						if (i != 0 && i != k - 1) {
							b.append("*");
						} else
							b.append(word.charAt(i));
					}
					tweetVal = tweetVal.replace(word, b.toString());
				}
			}

			String user_id_timeStamp = tweetMap.get("userID").getAsString()
					+ "sahibswati" + tweetMap.get("createdAt").getAsString();

			String tweet_id_text_score = tweetMap.get("id_str").getAsString()
					+ "sahibswati"
					+ URLEncoder.encode(tweetMap.get("tweet_text")
							.getAsString(), "UTF-8") + "sahibswati"
					+ tweetScore;
			return user_id_timeStamp + "\t" + tweet_id_text_score;
		}
		return "";
	}
}
