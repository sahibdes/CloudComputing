import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;

import io.undertow.Undertow;
import io.undertow.io.Sender;
import io.undertow.server.*;
import io.undertow.util.Headers;

public class UndertowPhase1 {

	// final static String jdbcUrl = "jdbc:mysql://" +
	// "ec2-52-5-107-31.compute-1.amazonaws.com"
	// + ":3306/hashtagdb";
	
	static Connection con=null;

	public static void main(String[] args) {
		Undertow server = Undertow.builder().addHttpListener(8080, "0.0.0.0")
				.setHandler(new HttpHandler() {
					
					@SuppressWarnings("deprecation")
					public void handleRequest(final HttpServerExchange exchange)
							throws Exception {
						
						Sender sender = exchange.getResponseSender();

						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,
								"text/plain");

						if (exchange.getRequestPath().contains("q1")) {
							
							Map<String, Deque<String>> params = exchange
									.getQueryParameters();
							BigInteger keyInt = new BigInteger(params
									.get("key").getFirst());
							BigInteger X = new BigInteger(
									"8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773");

							BigInteger Y = keyInt.divide(X);
							String spiralized_cipher = spiralize(params.get(
									"message").getFirst());
							BigInteger twentyFive = new BigInteger("25");
							BigInteger one = new BigInteger("1");

							BigInteger Z = one.add((Y.mod(twentyFive)));
							String teamID = "MadHatters";

							String awsID1 = "2404-3085-8626 ";
							String awsID2 = "1166-8424-3822 ";
							String awsID3 = "5574-2236-0141";

							
							String message = caesarify(Z, spiralized_cipher);

							StringBuilder sb = new StringBuilder();
							sb.append(teamID)
									.append(",")
									.append(awsID1)
									.append(",")
									.append(awsID2)
									.append(",")
									.append(awsID3)
									.append("\n")
									.append(new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.format(new java.util.Date()))
									.append("\n").append(message).append("\n");

							exchange.getResponseSender().send(sb.toString());
						}else if(exchange.getRequestPath().contains("q2")) {
							StringBuilder output = new StringBuilder();

							output.append("MadHatters,")
									.append("1166-8424-3822,")
									.append("2404-3085-8626,")
									.append("5574-2236-0141").append("\n");
							sender.send(output.toString());
							output = new StringBuilder();
						}else if (exchange.getRequestPath().contains("q3")) {
							
							con = DataConnector.getInstance().getConnection();
							Map<String, Deque<String>> params = exchange
									.getQueryParameters();

							String user_id = params.get("userid").getFirst();

							String sqlCmd = "select value from hashtagdb.q3 where user_id='\""
									+ user_id + "\"'";

							System.out.println(sqlCmd);
							PreparedStatement ps = con.prepareStatement(sqlCmd);

							ResultSet rs = ps.executeQuery();
							StringBuilder output = new StringBuilder();

							output.append("MadHatters,")
									.append("1166-8424-3822,")
									.append("2404-3085-8626,")
									.append("5574-2236-0141").append("\n");

							String value = "";
							while (rs.next())
								value = rs.getString(1);

							StringBuilder output_1 = new StringBuilder();
							StringBuilder output_2 = new StringBuilder();
							StringBuilder output_3 = new StringBuilder();
							String[] arrVals = value.split(" ");

							for (String str : arrVals) {
								String val = str.replaceAll("\"", "");
								str = val;
								if (str.contains("*"))
									output_1.append(str).append("\n");
								if (str.contains("+"))
									output_2.append(str).append("\n");
								if (str.contains("-"))
									output_3.append(str).append("\n");
							}
							output.append(output_1).append(output_2)
									.append(output_3);

							sender.send(output.toString());
							output = new StringBuilder();
							con.close();
						}else if (exchange.getRequestPath().contains("q4")) {
							
							
							con = DataConnector.getInstance().getConnection();
							Map<String, Deque<String>> params = exchange
									.getQueryParameters();

							String hashtag = params.get("hashtag").getFirst();
							String startDate = params.get("start").getFirst();
							String endDate = params.get("end").getFirst();
						
							Date fromDate = Date.valueOf(startDate);
							Date toDate = Date.valueOf(endDate);
							// GET
							// /q4?hashtag=SamSmith&start=2014-03-26&end=2014-03-30

							String sqlCmd = "select tweet_info from hashtagdb.popularhashtag where hashtag = \""
									+ hashtag + "\"";

							// System.out.println(sqlCmd);
							PreparedStatement ps = con.prepareStatement(sqlCmd);

							ResultSet rs = ps.executeQuery();
							StringBuilder output = new StringBuilder();
							TreeMap<Long, String> tmap = new TreeMap<Long, String>();

							output.append("MadHatters,")
									.append("1166-8424-3822,")
									.append("2404-3085-8626,")
									.append("5574-2236-0141").append("\n");

							while (rs.next()) {

								String[] tweets = rs.getString("tweet_info")
										.split("\\$");

								for (int i = 0; i < tweets.length; i++) {

									String[] userinfo = tweets[i].split("\\&");

									String[] dateSplit = userinfo[2]
											.split("\\+");
									Date toCheck = Date.valueOf(dateSplit[0]);
									if ((toCheck.equals(fromDate) || toCheck
											.equals(toDate))
											|| (toCheck.after(fromDate) && toCheck
													.before(toDate))) {
										tmap.put(Long.valueOf(userinfo[1]),
												userinfo[0] + "," + userinfo[2]
														+ "\n");

									}
								}
								for (java.util.Map.Entry<Long, String> entry : tmap
										.entrySet()) {
									output.append(entry.getKey()).append(",")
											.append(entry.getValue());
								}
							}
							// rs.close();

							sender.send(output.toString());
							output = new StringBuilder();
							con.close();
						} else if (exchange.getRequestPath().contains("q5")) {
							
							con = DataConnector.getInstance().getConnection();
							Map<String, Deque<String>> params = exchange
									.getQueryParameters();

							String userlist = params.get("userlist").getFirst();
							String startDate = params.get("start").getFirst();
							String endDate = params.get("end").getFirst();

							Date fromDate = Date.valueOf(startDate);
							Date toDate = Date.valueOf(endDate);

							// GET
							// /q5?userlist=12,16,18&start=2010-01-01&end=2014-12-31

							ResultSet rs;
							StringBuilder output = new StringBuilder();
							output.append("MadHatters,")
									.append("1166-8424-3822,")
									.append("2404-3085-8626,")
									.append("5574-2236-0141").append("\n");

							String sqlCmd = new String(
									"select userid,SUM(total_tweet_score)+MAX(total_friend_score)+MAX(total_follower_score) TT FROM hashtagdb.q5 WHERE userid in ("
											+ userlist
											+ ")  AND timestamp >='"
											+ fromDate
											+ "' AND timestamp <='"
											+ toDate
											+ "' group by userid order by TT Desc");

							// System.out.println(sqlCmd);
							PreparedStatement ps = con.prepareStatement(sqlCmd);

							rs = ps.executeQuery();

							while (rs.next()) {
								BigInteger TT = new BigInteger(rs
										.getString("TT"));
								BigInteger userid = new BigInteger(rs
										.getString("userid"));
								output.append(userid.toString() + ","
										+ TT.toString() + "\n");

							}

							// rs.close();

							sender.send(output.toString());
							output = new StringBuilder();
							con.close();
						} else if (exchange.getRequestPath().contains("q6")) {
							
							con = DataConnector.getInstance().getConnection();
							Map<String, Deque<String>> params = exchange
									.getQueryParameters();

							String m = params.get("m").getFirst();
							String n = params.get("n").getFirst();

							//System.out.println(m + " " + n);

							String sqlCmd = "select count(*) from hashtagdb.q6 where user_id between '"
									+ m + "' and '" + n + "'";

							PreparedStatement ps = con.prepareStatement(sqlCmd);

							ResultSet rs = ps.executeQuery();
							int count = 0;
							while (rs.next())
								count = rs.getInt(1);

							//System.out.println(count);
							StringBuilder output = new StringBuilder();

							output.append("MadHatters,")
									.append("1166-8424-3822,")
									.append("2404-3085-8626,")
									.append("5574-2236-0141").append("\n")
									.append(count).append("\n");

							sender.send(output.toString());
							output = new StringBuilder();
							con.close();

						}

					}
				}).build();
		server.start();

	}

	public static String caesarify(BigInteger Z, String spiralized_cipher) {

		String s = spiralized_cipher;
		int k = Z.intValue();
		StringBuffer decoded = new StringBuffer();

		for (int i = 0; i < s.length(); i++) {
			int x = (((int) s.charAt(i))) - k;
			if (x < 65) {
				x += 26;
			}

			decoded.append((char) (x));
		}
		return decoded.toString();
	}

	public static String spiralize(String s) {

		int len = s.length();
		int m = (int) Math.sqrt(len);
		int n = (int) Math.sqrt(len);
		StringBuffer Cipher_2D = new StringBuffer();
		char a[][] = new char[m][n];

		int index = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = s.charAt(index);
				index++;
			}
		}

		int t = 0, b = m - 1, l = 0, r = n - 1;
		int dir = 0;

		while (t <= b && l <= r) {
			if (dir == 0) {
				for (int i = l; i <= r; i++) {
					// System.out.print(a[t][i] + " ");
					Cipher_2D.append(a[t][i]);
				}
				t++;
			}

			else if (dir == 1) {
				for (int i = t; i <= b; i++) {
					// System.out.print(a[i][r]+ " ");
					Cipher_2D.append(a[i][r]);
				}
				r--;
			}

			else if (dir == 2) {
				for (int i = r; i >= l; i--) {
					// System.out.print(a[b][i]+ " ");
					Cipher_2D.append(a[b][i]);
				}
				b--;
			} else if (dir == 3) {
				for (int i = b; i >= t; i--) {
					// System.out.print(a[i][l]+ " ");
					Cipher_2D.append(a[i][l]);
				}
				l++;
				dir = 3;
			}
			dir = (dir + 1) % 4;
		}
		return (Cipher_2D.toString());
	}
}