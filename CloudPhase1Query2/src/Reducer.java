import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Reducer {
	public static void main(String[] args) {

		// BufferedReader bfr = new BufferedReader(
		// new InputStreamReader(System.in));

		String dbUser = "project";
		String dbPass = "project";
		String jdbcURL = "jdbc:mysql://ec2-54-174-65-175.compute-1.amazonaws.com:3306/twitter";
		String str = "464327710618423296	Thu May 08 04:55:27 EDT 2014	Vilena Yakhina, My Way: Последние новости с мест)) http://t.co/pQJUJi0wbs	2392089990";
		Connection con = null;
		int rsltSet;
		try {
			con = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
			PreparedStatement prepStatement = null;
			// while ((str = bfr.readLine()) != null) {
			String[] arrTweets = str.split("\t");
			prepStatement = con
					.prepareStatement("Insert into twitter (user_id,time_stamp,tweet_text,tweet_id) values ("
							+ arrTweets[0]
							+ ","
							+ "'"
							+ arrTweets[1]
							+ "'"
							+ ","
							+ "'"
							+ arrTweets[2]
							+ "'"
							+ ","
							+ arrTweets[3] + ")");

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
