package Testing.sahib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Reducerq6 {

	public static void main(String[] args) throws JsonSyntaxException,
			ParseException, IOException, SQLException {
		BufferedReader bfr = new BufferedReader(
				new InputStreamReader(System.in));

		String user_id = "";
		boolean invalid = false;
		StringBuilder value = new StringBuilder();
		String line = "";
		while ((line = bfr.readLine()) != null) {

			String[] parts = line.split("\t");

			Gson gs = new Gson();
			JsonParser parser = new JsonParser();

			if (user_id.isEmpty() || !user_id.equals(parts[0])) {
				if (!user_id.isEmpty()) {
					invalid = false;
					String[] val = value.toString().split("\t");
					for (String st : val) {
						if (!st.equals("null")) {
							invalid = true;
							break;
						}
					}
					if (!invalid)
						System.out.println(user_id);
				}
				invalid = false;
				user_id = parts[0];
				JsonObject js_place = parts[1].equals("null") ? null : parser
						.parse(parts[1]).getAsJsonObject();

				JsonElement js_placename = js_place == null ? null : (js_place)
						.get("name");
				value = new StringBuilder();
				value.append(js_placename == null ? "null" : js_placename
						.toString());
			} else {
				JsonObject js_place = parts[1].equals("null") ? null : parser
						.parse(parts[1]).getAsJsonObject();

				JsonElement js_placename = js_place == null ? null : (js_place)
						.get("name");

				value.append("\t"
						+ (js_placename == null ? "null"
								: js_placename == null ? "null" : js_placename));
			}
		}

		String[] val = value.toString().split("\t");
		for (String st : val) {
			if (!st.equals("null")) {
				invalid = true;
				break;
			}
		}
		// print remaining
		if (!invalid)
			System.out.println(user_id);
	}
}
