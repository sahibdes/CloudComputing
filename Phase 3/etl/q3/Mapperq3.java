package Testing.sahib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Mapperq3 {

	public static void main(String[] args) throws IOException,
			JsonSyntaxException, ParseException {
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));
		String str = "";

		while ((str = bfr.readLine()) != null) {
			if (!jsonToString(str).isEmpty())
				System.out.println(jsonToString(str));
		}
	}

	@SuppressWarnings("unused")
	private static String jsonToString(String tweet) throws ParseException,
			JsonSyntaxException, IOException {
		String output = "";
		if (!tweet.isEmpty()) {
			Gson gs = new Gson();
			JsonParser parser = new JsonParser();
			JsonObject js_main = parser.parse(tweet).getAsJsonObject();
			if (js_main.has("user")) {
				JsonObject js_user = js_main.get("user").getAsJsonObject();
				if (js_user.has("id_str")) {
					if (js_main.has("retweeted_status")) {

						JsonObject js_retweet = js_main.get("retweeted_status")
								.getAsJsonObject();
						JsonObject js_retweetUser = new JsonObject();
						if (!js_retweet.isJsonNull()) {
							if (js_retweet.has("user"))
								js_retweetUser = js_retweet
										.getAsJsonObject("user");
							if (!js_retweetUser.isJsonNull()
									&& js_retweetUser.has("id_str")) {
								System.out.println(js_user.get("id_str") + "\t"
										+ js_retweetUser.get("id_str") + " "
										+ "(1)");
								System.out.println(js_retweetUser.get("id_str")
										+ "\t" + js_user.get("id_str") + " "
										+ "(0)");
							}
						}
					}

				}
			}

		}
		return output;
	}
}
