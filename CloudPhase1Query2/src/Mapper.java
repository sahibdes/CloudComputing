import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;

import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

public class Mapper {
	public static void main(String[] args) {
		try {
			File f = new File("15619s15twitter-partb-ag");
			BufferedReader bfr = new BufferedReader(new FileReader(f));
			String str = null;
			// String fileName = System.getenv("mapreduce_map_input_file");

			while ((str = bfr.readLine()) != null) {
				System.out.println(jsonToString(str));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String jsonToString(String tweet) throws ParseException,
			UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		if (!tweet.isEmpty()) {
			Gson gs = new Gson();

			LinkedHashMap<String, String> arr = gs.fromJson(tweet,
					LinkedHashMap.class);
			SimpleDateFormat format = new SimpleDateFormat(
					"E MMM dd HH:mm:ss Z yyyy", Locale.US);
			Date createdAt = format.parse(arr.get("created_at"));

			ArrayList<String> arrWords = new ArrayList<String>();
			if (createdAt.after(format.parse("Sun Apr 20 00:00:00 +0000 2014"))) {
				Object arrUser = arr.get("user");

				com.google.gson.internal.LinkedTreeMap userMap = (com.google.gson.internal.LinkedTreeMap) arrUser;
				if (!arr.get("id_str").isEmpty() || !arr.get("text").isEmpty()
						|| !((String) userMap.get("id_str")).isEmpty()
						|| !createdAt.toString().isEmpty()) {
					sb.append(arr.get("id_str")).append("\t")
							.append(createdAt.toString()).append("\t")
							.append(arr.get("text")).append("\t")
							.append((String) userMap.get("id_str"));
				}
			}
		}
		return sb.toString();
	}

	public static void sqlInsert(String str) {

		// BufferedReader bfr = new BufferedReader(
		// new InputStreamReader(System.in));
		if (!str.isEmpty()) {
			String dbUser = "project";
			String dbPass = "project";
			String jdbcURL = "jdbc:mysql://ec2-54-174-65-175.compute-1.amazonaws.com:3306/twitter";
			// String str =
			// "464327710618423296	Thu May 08 04:55:27 EDT 2014	Vilena Yakhina, My Way: Последние новости с мест)) http://t.co/pQJUJi0wbs	2392089990";
			Connection con = null;
			int rsltSet;
			try {
				con = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
				PreparedStatement prepStatement = null;
				// while ((str = bfr.readLine()) != null) {
				String[] arrTweets = str.split("\t");
				prepStatement = con
						.prepareStatement("Insert into twitter (user_id,time_stamp,tweet_text,tweet_id) values ( ?,?,?,?)");

				prepStatement.setString(1, arrTweets[0]);
				prepStatement.setString(2, arrTweets[1]);
				prepStatement.setString(3, arrTweets[2]);
				prepStatement.setString(4, arrTweets[3]);
				rsltSet = prepStatement.executeUpdate();
				// }
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (!con.isClosed())
						con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
