package Testing.sahib;

import java.io.*;

public class NewReducer {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			String line = "";
			String prevHashTag = null;
			String prevTweetId = null;
			StringBuilder sb = new StringBuilder();
			String prevTweet = null;

			while ((line = br.readLine()) != null) {
				try {
					String[] parts = line.split("\t");
					String[] splitTweet = parts[1].split("&");

					// abc abc lmn xyz
					// check if first time value
					if (prevHashTag == null || prevHashTag.isEmpty()) {
						prevHashTag = parts[0];
						prevTweetId = splitTweet[1];
						sb.append(parts[0]).append("\t").append(parts[1]);
					} else {
						if (prevHashTag.equals(parts[0])) {
							// same hashtag different tweet
							if (!prevTweetId.equals(splitTweet[1])) {
								sb.append("$").append(parts[1]);
							}
						}
						// this is the case when new hashtag comes
						else {
							System.out.println(sb.toString());
							prevHashTag = parts[0];
							prevTweetId = splitTweet[1];
							sb = new StringBuilder();
							sb.append(parts[0]).append("\t").append(parts[1]);
						}
					}
					prevTweet = parts[1];
				} catch (Exception e) {
					System.out.println(e);
				}
			}

			// print last value here
			if (!sb.toString().isEmpty() || sb != null)
				System.out.println(sb.toString());
		} catch (Exception e) {

		}

	}

}