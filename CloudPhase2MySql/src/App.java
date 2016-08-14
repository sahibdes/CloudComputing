import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import io.undertow.io.Sender;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class App {

	public static void main(String[] args) throws Exception {
		// final ObjectMapper mapper = new ObjectMapper();
		final String jdbcUrl = "jdbc:mysql://" + "localhost" + ":" + "3306"
				+ "/" + "CloudTest";
		Undertow.builder().addHttpListener(8080, "0.0.0.0")
				.setHandler(new HttpHandler() {

					public void handleRequest(HttpServerExchange exchange)
							throws Exception {
						// TODO Auto-generated method stub
						Sender sender = exchange.getResponseSender();
						if (exchange.getRequestPath().contains("q3")) {
							Connection con = null;

							con = DriverManager.getConnection(jdbcUrl, "root",
									"Q3tech123");

							String[] queryStr = exchange.getQueryString()
									.split("&");
							String[] user_id = queryStr[0].split("=");

							String sqlCmd = "SELECT count(retweet_user) count1,retweet_user  FROM CloudTest.retweet_db where tweet_user='"
									+ user_id[1]
									+ "'"
									+ "  group by retweet_user ";

							Statement stmt = con.createStatement();
							ResultSet rs = stmt.executeQuery(sqlCmd);
							StringBuilder output = new StringBuilder();

							output.append("MadHatters,")
									.append("1234-5678-1234,")
									.append("1234-5678-1234,")
									.append("1234-5678-1234").append("\n");
							TreeMap<String, Integer> tmap1 = new TreeMap<String, Integer>();
							TreeMap<String, Integer> tmap2 = new TreeMap<String, Integer>();
							TreeMap<String, Integer> tmap3 = new TreeMap<String, Integer>();
							while (rs.next()) {
								String retweet_id_X = rs
										.getString("retweet_user");

								// get tweets of retweeting users
								String sqlCmd1 = "SELECT count(retweet_user) count1,retweet_user  FROM CloudTest.retweet_db where tweet_user='"
										+ retweet_id_X
										+ "'"
										+ " group by retweet_user";

								stmt = con.createStatement();
								ResultSet rs1 = stmt.executeQuery(sqlCmd1);

								int astCount = 0;
								int minusCount = 0;
								Boolean isAst = false;
								String retweet_id_Y = "";
								while (rs1.next()) {
									retweet_id_Y = rs1
											.getString("retweet_user");
									if (retweet_id_Y.equals("1210547647")) {
										isAst = true;
										astCount = Integer.valueOf(rs1
												.getString("count1"));
										break;
									}
								}
								if (isAst) {
									astCount += Integer.valueOf(rs
											.getString("count1"));
									tmap1.put(retweet_id_X, astCount);
									// outputAst.append("*,").append(astCount)
									// .append(",").append(retweet_id_X)
									// .append("\n");
								} else {
									minusCount = Integer.valueOf(rs
											.getString("count1"));
									tmap2.put(retweet_id_X, minusCount);
									// outputMinus.append("-,").append(minusCount)
									// .append(",").append(retweet_id_X)
									// .append("\n");
								}
							}

							// plus relationship
							String sqlCmd2 = "Select count(*) count1,tweet_user from retweet_db where retweet_user='1210547647'and tweet_user not in (select retweet_user from retweet_db where tweet_user='1210547647') group by tweet_user";

							stmt = con.createStatement();
							ResultSet rs3 = stmt.executeQuery(sqlCmd2);

							while (rs3.next()) {
								int plusCount = Integer.valueOf(rs3
										.getString("count1"));
								String retweet_id = rs3.getString("tweet_user");
								tmap3.put(retweet_id, plusCount);
								// outputPlus.append("+,").append(plusCount)
								// .append(",").append(retweet_id)
								// .append("\n");
							}

							for (Entry entry : sortMap(tmap1).entrySet()) {
								output.append("*,").append(entry.getKey())
										.append(",").append(entry.getValue())
										.append("\n");
							}

							for (Entry entry : sortMap(tmap3).entrySet()) {
								output.append("+,").append(entry.getKey())
										.append(",").append(entry.getValue())
										.append("\n");
							}

							for (Entry entry : sortMap(tmap2).entrySet()) {
								output.append("-,").append(entry.getKey())
										.append(",").append(entry.getValue())
										.append("\n");
							}

							// sender.send(content);
							rs.close();
							rs3.close();

							sender.send(output.toString());
						}

					}

					// Method to sort map by values
					@SuppressWarnings({ "rawtypes", "hiding" })
					public <String extends Comparable, Integer extends Comparable> Map<String, Integer> sortMap(
							Map<String, Integer> map) {
						LinkedList<Entry<String, Integer>> mapEntries = new LinkedList<Map.Entry<String, Integer>>(
								map.entrySet());

						Collections.sort(mapEntries,
								new Comparator<Map.Entry<String, Integer>>() {

									@SuppressWarnings("unchecked")
									public int compare(
											Entry<String, Integer> val1,
											Entry<String, Integer> val2) {
										return val2.getValue().compareTo(
												val1.getValue());
									}
								});

						Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
						for (Map.Entry<String, Integer> entry : mapEntries)
							sortedMap.put(entry.getKey(), entry.getValue());

						return sortedMap;
					}

				}).build().start();

	}
}
