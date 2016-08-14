import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Servlet implementation class servlet
 */
@WebServlet("/q2")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static org.apache.hadoop.conf.Configuration configuration;

	/**
	 * Default constructor.
	 */
	public Servlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/plain");
		PrintWriter p = response.getWriter();

		String userid = request.getParameter("userid");
		String tweet_time = request.getParameter("tweet_time");

		// connect to mysql
		String dbUser = "project";
		String dbPass = "project";
		String jdbcURL = "jdbc:mysql://ec2-54-172-114-139.compute-1.amazonaws.com:3306/twitter";
		Connection con = null;

		try {
			con = DriverManager.getConnection(jdbcURL, dbUser, dbPass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// p.println("MadHatters,1166-8424-3822,2404-3085-8626,8246-0444-6166");
		// p.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		// .format(new java.util.Date()));
		// p.println(messageVal);
		try {
			p.println(connectHBase(con, userid, tweet_time, response));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String connectHBase(Connection con, String user_id,
			String time_stamp, HttpServletResponse response)
			throws SQLException, UnsupportedEncodingException {
		try {
			org.apache.hadoop.conf.Configuration hBaseCon = HBaseConfiguration
					.create();
			hBaseCon.set("hbase.zookeeper.quorum", "172.31.26.105:60000");
			hBaseCon.set("hbase.zookeeper.property.clientPort", "2181");
			configuration = hBaseCon;

			HBaseAdmin.checkHBaseAvailable(configuration);

			HTable table = new HTable(configuration, "twitter");
			PrintWriter p = response.getWriter();
			Get get = new Get(
					Bytes.toBytes(user_id + "sahibswati" + time_stamp));
			get.addFamily(Bytes.toBytes("tweet_text"));
			Result rs = table.get(get);
			for (org.apache.hadoop.hbase.KeyValue keyval : rs.raw()) {
				p.println(keyval.getQualifier());
				p.println(keyval.getValue().toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//
		// Statement stmt = con.createStatement();
		// ResultSet rs = stmt.executeQuery(sqlCmd);
		StringBuilder sb = new StringBuilder();
		// while (rs.next()) {
		// String userid = rs.getString("user_id");
		// String timestamp = rs.getString("time_stamp");
		// String tweetid = rs.getString("tweet_id");
		// String tweet_text = rs.getString("tweet_text");
		// String[] splitTweets = tweet_text.split("sahibpaswati");
		//
		// sb.append(teamID).append(",").append(aws1).append(",").append(aws2)
		// .append(",").append("1234-5678-1234").append("\n");
		// StringBuilder sb1 = new StringBuilder();
		// for (String s : splitTweets) {
		// String[] splitTweetidText = s.split("sahibpittswati");
		// sb1.append(splitTweetidText[0])
		// .append(":")
		// .append(URLDecoder.decode(splitTweetidText[2], "UTF-8"))
		// .append(":").append(splitTweetidText[1]).append("\n");
		// }
		// sb.append(sb1);
		// }
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private static String connectMySQL(Connection con, String user_id,
			String time_stamp, String teamID, String aws1, String aws2)
			throws SQLException, UnsupportedEncodingException {

		String sqlCmd = "Select distinct * from twitter where user_id_timestamp='"
				+ user_id + "sahibswati" + time_stamp + "'";

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sqlCmd);
		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			String userid_timestamp = rs.getString("user_id_timestamp");
			String tweet_id_score_text = rs.getString("tweet_text");

			String[] splitTweets = tweet_id_score_text.split("sahibpittswati");
			sb.append(teamID).append(",").append(aws1).append(",").append(aws2)
					.append(",").append("1234-5678-1234").append("\n");
			for (String tweet : splitTweets) {
				String[] tweetSplit = tweet.split("sahibpaswati");
				sb.append(tweetSplit[0]).append(":")
						.append(URLDecoder.decode(tweetSplit[2], "UTF-8"))
						.append(":").append(tweetSplit[1]).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
