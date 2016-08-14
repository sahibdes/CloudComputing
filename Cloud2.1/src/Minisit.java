import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.util.json.JSONObject;

public class Minisit {

	public static void main(String[] args) throws Exception {
		// if (exchange.getRequestPath().contains("step3")) {
		AWSCredentials credentials = new ProfileCredentialsProvider()
				.getCredentials();
		AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentials);
		// String[] queryStr = exchange.getQueryString().split("&");
		// String[] user_id = queryStr[1].split("=");
		// System.out.println(Arrays.toString(queryStr));
		HashMap<String, AttributeValue> map = new HashMap<String, AttributeValue>();
		map.put("userid", new AttributeValue().withN("1000"));

		GetItemRequest request = new GetItemRequest().withTableName("Images")
				.withKey(map).withAttributesToGet(Arrays.asList("time", "url"));

		GetItemResult itemResult = dbClient.getItem(request);
		// {"time":"{S: 2014-01-08,}"}
		String createdDate = itemResult.getItem().get("time").getS();
		String imageUrl = itemResult.getItem().get("url").getS();
		JSONObject response = new JSONObject();
		response.put("time", createdDate);
		response.put("url", imageUrl);
		// String content = "returnRes(" + mapper.writeValueAsString(response)
		// + ")";
		// sender.send(content);

	}
}
