import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.util.StringUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;

public class BucketCapture {

	public static void main(String[] args) throws IOException {
		Properties p = new Properties();
		try {
			p.load(BucketCapture.class
					.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {

			e.printStackTrace();
		}
		AWSCredentials credentials = new BasicAWSCredentials(
				p.getProperty("accessKey"), p.getProperty("secretKey"));

		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTP);

		// cpnnect to mysql
		String dbUser = "project";
		String dbPass = "project";
		String jdbcURL = "jdbc:mysql://ec2-54-172-114-139.compute-1.amazonaws.com:3306/twitter";
		Connection con = null;
		try {
			con = DriverManager.getConnection(jdbcURL, dbUser, dbPass);

			AmazonS3 conn = new AmazonS3Client(credentials, clientConfig);
			List<Bucket> buckets = conn.listBuckets();
			BufferedReader bfr;
			String str = "";
			for (Bucket bucket : buckets) {
				if (bucket.getName().equals(new String("phase1log10"))) {
					ObjectListing objects = conn.listObjects(bucket.getName());
					do {
						for (S3ObjectSummary objectSummary : objects
								.getObjectSummaries()) {

							if (objectSummary.getKey().contains(
									"output6/part-00000")) {
								System.out.println(objectSummary.getKey());
								S3Object objectComplete = conn
										.getObject(new GetObjectRequest(
												"phase1log10", objectSummary
														.getKey()));
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(
												objectComplete
														.getObjectContent()));
								// while ((str = reader.readLine()) != null)
								connectMySQL(con, "part-00000");
							}
						}
						objects = conn.listNextBatchOfObjects(objects);
					} while (objects.isTruncated());
				}
			}
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

	public static void displayfile(File node) {

		System.out.println(node.getAbsoluteFile());

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				displayfile(new File(node, filename));
			}
		}

	}

	private static void connectMySQL(Connection con, String path)
			throws SQLException {
		int rsltSet;
		String sqlCmd = "LOAD DATA LOCAL INFILE '" + path
				+ "' INTO table twitter" + " FIELDS TERMINATED BY 'sahibswati'";
		Statement stmt = con.createStatement();
		stmt.execute(sqlCmd);
		/*
		 * PreparedStatement prepStatement = null; if (!str.isEmpty() &&
		 * !str.equals("\t")) { String[] arrTweets1 = str.split("\t"); String[]
		 * arrtweet_key = arrTweets1[0].split("sahibswati"); prepStatement = con
		 * .prepareStatement(
		 * "Insert into twitter (user_id,time_stamp,tweet_id,tweet_text,sentiment_score) values ( ?,?,?,?,?)"
		 * );
		 * 
		 * prepStatement.setString(1, arrtweet_key[0]);
		 * prepStatement.setString(2, arrtweet_key[1]);
		 * 
		 * String[] arrtweet_value = arrTweets1[1].split("sahibswati"); int
		 * tweet_length = arrtweet_value.length; for (int i = 0; i <
		 * tweet_length; i += 3) { prepStatement.setString(3,
		 * arrtweet_value[i]); prepStatement.setString(4, arrtweet_value[i +
		 * 1]); prepStatement.setString(5, arrtweet_value[i + 2]); rsltSet =
		 * prepStatement.executeUpdate();
		 * 
		 * } }
		 */
	}
}