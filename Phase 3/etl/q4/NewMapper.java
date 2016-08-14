package Testing.sahib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewMapper {
	// static PrintWriter pwr;

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			// File f = new File("Test");
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					System.in));

			// bfr = new BufferedReader(new FileReader(f));
			// pwr = new PrintWriter(new File("MapperOut.txt"));
			String str = null;

			while ((str = bfr.readLine()) != null) {
				jsonToString(str);
			}
			// pwr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void jsonToString(String tweet) throws ParseException,
			UnsupportedEncodingException, FileNotFoundException {

		SimpleDateFormat originalformat = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss Z yyyy");
		SimpleDateFormat targetformat = new SimpleDateFormat(
				"yyyy-MM-dd+HH:mm:ss");
		SimpleDateFormat targetformat2 = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		if (!tweet.isEmpty()) {
			Gson gs = new Gson();
			JsonParser parser = new JsonParser();
			JsonObject arr = parser.parse(tweet).getAsJsonObject();

			// check if key exists

			if (arr.has("created_at") && arr.has("text") && arr.has("id_str")) {
				if (arr.has("entities")) {
					JsonObject arr3 = arr.get("entities").getAsJsonObject();
					if (arr3.has("hashtags")) {
						JsonArray arr4 = arr3.get("hashtags").getAsJsonArray();
						if (arr4.size() > 0) {
							for (int i = 0; i < arr4.size(); i++) {

								SimpleDateFormat format = new SimpleDateFormat(
										"E MMM dd HH:mm:ss Z yyyy", Locale.US);

								JsonObject obj = arr4.get(i).getAsJsonObject();
								String ht = obj.get("text").getAsString();
								if (ht.matches("[A-Za-z0-9]+")) {

									Date createdAt = new Date(0);
									if (!arr.get("created_at").getAsString()
											.isEmpty())
										createdAt = format.parse(arr.get(
												"created_at").getAsString());
									d = originalformat.parse(createdAt
											.toString());
									Calendar c = Calendar.getInstance();
									c.setTime(d);
									String formattedDate = targetformat
											.format(c.getTime());
									String formattedDate2 = targetformat2
											.format(c.getTime());
									JsonObject arr2 = arr.get("user")
											.getAsJsonObject();
									if (arr2.has("id_str")) {
										if (!arr.get("id_str").getAsString()
												.isEmpty()
												&& !arr.get("text")
														.getAsString()
														.isEmpty()
												&& !arr2.get("id_str")
														.getAsString()
														.isEmpty()) {
											System.out.println(ht
													+ "\t"
													+ arr2.get("id_str")
															.getAsString()
													+ "&"
													+ arr.get("id_str")
															.getAsString()
													+ "&" + formattedDate);
											// pwr.append(ht
											// + "\t"
											// + arr2.get("id_str")
											// .getAsString()
											// + "&"
											// + arr.get("id_str")
											// .getAsString()
											// + "&" + formattedDate);
											// pwr.append("\n");
											/*
											 * pwr.println(ht + "\t" +
											 * formattedDate2 +"$" +
											 * arr2.get("id_str") .getAsString()
											 * + "&" + arr.get("id_str")
											 * .getAsString() + "&" +
											 * formattedDate );
											 */
										}
									}
								}
							}
						}
					}

				}

			}
		}

	}
}
