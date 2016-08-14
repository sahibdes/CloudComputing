package Phase2.sahib.swati;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Mapper {
	public static void main(String[] args) {
		try {
			// File f = new File("Testing_My_Life.txt");
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					System.in));
			String str = null;
			// String fileName = System.getenv("mapreduce_map_input_file");
			// PrintWriter pwr = new PrintWriter(new File("MapperOut.txt"));
			while ((str = bfr.readLine()) != null) {
				String output = jsonToString(str);
				if (!output.isEmpty())
					System.out.println(output);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String jsonToString(String tweet) throws ParseException,
			UnsupportedEncodingException, FileNotFoundException {

		String json = "";
		SimpleDateFormat originalformat = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss Z yyyy");
		SimpleDateFormat targetformat = new SimpleDateFormat(
				"yyyy-MM-dd+HH:mm:ss");
		Date d = null;
		if (!tweet.isEmpty()) {
			Gson gs = new Gson();
			JsonParser parser = new JsonParser();
			JsonObject arr = parser.parse(tweet).getAsJsonObject();

			// check if key exists
			if (arr.has("created_at") && arr.has("text") && arr.has("id_str")) {
				SimpleDateFormat format = new SimpleDateFormat(
						"E MMM dd HH:mm:ss Z yyyy", Locale.US);
				Date createdAt = new Date(0);
				if (!arr.get("created_at").getAsString().isEmpty())
					createdAt = format.parse(arr.get("created_at")
							.getAsString());

				if (createdAt.after(format
						.parse("Sun Apr 20 00:00:00 +0000 2014"))) {
					d = originalformat.parse(createdAt.toString());
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					String formattedDate = targetformat.format(c.getTime());

					JsonObject arr2 = arr.get("user").getAsJsonObject();
					if (arr2.has("id_str")) {
						if (!arr.get("id_str").getAsString().isEmpty()
								&& !arr.get("text").getAsString().isEmpty()
								&& !arr2.get("id_str").getAsString().isEmpty()) {
							Tweet setTweet = new Tweet(arr.get("id_str")
									.getAsString(), formattedDate, arr.get(
									"text").getAsString(), arr2.get("id_str")
									.getAsString());
							json = gs.toJson(setTweet);
						}
					}
				}

			}
		}
		return json;
	}
}
