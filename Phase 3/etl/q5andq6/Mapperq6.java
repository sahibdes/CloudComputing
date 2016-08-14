package Testing.sahib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Mapperq6 {

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
			JsonObject js_user = js_main.get("user").getAsJsonObject();
			JsonObject js_place = new JsonObject();
			JsonElement place_element = js_main.get("place");
			if (js_user.has("id_str")) {
				output = js_user.get("id_str").getAsString() + "\t"
						+ (place_element.isJsonNull() ? "null" : place_element);
			}
		}
		return output;
	}
}
