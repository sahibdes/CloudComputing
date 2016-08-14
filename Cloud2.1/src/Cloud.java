import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

public class Cloud {
	public static void main(String[] args) {

		Properties properties = new Properties();
		try {
			properties.load(Cloud.class
					.getResourceAsStream("/AwsCredentials.properties"));

			BasicAWSCredentials bawsc = new BasicAWSCredentials(
					properties.getProperty("accessKey"),
					properties.getProperty("secretKey"));

			// create security group
			createsecurityGroup(bawsc);
			// create load generator instance
			Instance loadInstance = createLGInstance(bawsc);

			threadSleep();
			// create data center instance
			Instance dcInstance = createDCInstance(bawsc);

			threadSleep();

			AmazonEC2Client amazonEC2Client = new AmazonEC2Client(bawsc);

			List reservations = amazonEC2Client.describeInstances()
					.getReservations();
			int reservationCount = reservations.size();

			String lgPublicDnsName = "";
			String dcPublicDnsName = "";

			// loop on the created instance and check for lgi and dci
			for (int i = 0; i < reservationCount; i++) {
				List instances = ((Reservation) reservations.get(i))
						.getInstances();
				int instanceCount = instances.size();
				for (int j = 0; j < instanceCount; j++) {
					Instance instance = (Instance) instances.get(j);
					if (!instance.getPublicDnsName().isEmpty()
							&& (instance.getImageId().equals("ami-b04106d8") || instance
									.getImageId().equals("ami-4c4e0f24"))) {
						if (instance.getImageId().equals("ami-4c4e0f24"))
							lgPublicDnsName = instance.getPublicDnsName();
						else
							dcPublicDnsName = instance.getPublicDnsName();
					}
				}
			}

			String testLog = executePost("http://" + lgPublicDnsName
					+ "/test/horizontal" + "?dns=" + dcPublicDnsName);
			String[] str = testLog
					.split("<!DOCTYPE html><html><head><title>MSB Load Generator</title></head><body><a href='");
			String[] str1 = str[1].split("'");

			while (executeINI("http://" + lgPublicDnsName + str1[0]) < 4000) {
				dcInstance = createDCInstance(bawsc);
				threadSleep();

				// find the latest dci to pass to test url
				boolean isRunning = true;
				while (isRunning) {
					List reservationList = amazonEC2Client.describeInstances()
							.getReservations();
					int reservationCounts = reservationList.size();
					for (int i = 0; i < reservationCounts; i++) {
						List instances = ((Reservation) reservationList.get(i))
								.getInstances();
						int instanceCount = instances.size();
						// Print the instance IDs of every instance in the
						// reservation.
						for (int j = 0; j < instanceCount; j++) {
							Instance instance = (Instance) instances.get(j);
							if (instance.getInstanceId().equals(
									dcInstance.getInstanceId())) {
								if (instance.getState().getName()
										.equals("running")) {
									isRunning = false;
									dcPublicDnsName = instance
											.getPublicDnsName();

								}
							}
						}
					}
				}

				try {
					String str2 = executePost("http://" + lgPublicDnsName
							+ "/test/horizontal/add" + "?dns="
							+ dcPublicDnsName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				threadSleep();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void createsecurityGroup(BasicAWSCredentials bawsc)
			throws IOException {

		// Create the AmazonEC2Client object so we can call various APIs.
		AmazonEC2 ec2 = new AmazonEC2Client(bawsc);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		ec2.setRegion(usEast1);

		// Create a new security group.
		try {
			CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest(
					"msbsg3", "MSB Secrutiy Group");

			CreateSecurityGroupResult result = ec2
					.createSecurityGroup(securityGroupRequest);
		} catch (AmazonServiceException ase) {
			// Likely this means that the group is already created, so ignore.
			System.out.println(ase.getMessage());
		}

		String ipAddr = "0.0.0.0/0";

		// Create a range that you would like to populate.
		List<String> ipRanges = Collections.singletonList(ipAddr);

		// Open up port 22 for TCP traffic to the associated IP from above (e.g.
		// ssh traffic).
		IpPermission ipPermission = new IpPermission().withIpProtocol("tcp")
				.withFromPort(new Integer(80)).withToPort(new Integer(80))
				.withIpRanges(ipRanges);

		List<IpPermission> ipPermissions = Collections
				.singletonList(ipPermission);

		try {
			// Authorize the ports to the used.
			AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(
					"msbsg3", ipPermissions);
			ec2.authorizeSecurityGroupIngress(ingressRequest);
		} catch (AmazonServiceException ase) {
			System.out.println(ase.getMessage());
		}
	}

	public static Instance createLGInstance(BasicAWSCredentials bawsc)
			throws IOException {

		// Create an Amazon EC2 Client
		AmazonEC2Client ec2 = new AmazonEC2Client(bawsc);

		// Create Instance Request
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

		// Configure Instance Request
		runInstancesRequest.withImageId("ami-4c4e0f24")
				.withInstanceType("m3.medium").withMinCount(1).withMaxCount(1)
				.withKeyName("15619121").withSecurityGroups("msbsg1");

		// Launch Instance
		RunInstancesResult runInstancesResult = ec2
				.runInstances(runInstancesRequest);

		// Return the Object Reference of the Instance just Launched
		Instance loadInstance = runInstancesResult.getReservation()
				.getInstances().get(0);

		CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		createTagsRequest.withResources(loadInstance.getInstanceId()).withTags(
				new Tag("Project", "2.1"));
		ec2.createTags(createTagsRequest);
		return loadInstance;
	}

	public static Instance createDCInstance(BasicAWSCredentials bawsc)
			throws IOException {

		// Create an Amazon EC2 Client
		AmazonEC2Client ec2 = new AmazonEC2Client(bawsc);

		// Create Instance Request
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

		// Configure Instance Request
		runInstancesRequest.withImageId("ami-b04106d8")
				.withInstanceType("m3.medium").withMinCount(1).withMaxCount(1)
				.withKeyName("15619121").withSecurityGroups("msbsg1");

		// Launch Instance
		RunInstancesResult runInstancesResult = ec2
				.runInstances(runInstancesRequest);

		// Return the Object Reference of the Instance just Launched
		Instance dcInstance = runInstancesResult.getReservation()
				.getInstances().get(0);

		threadSleep();

		CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		createTagsRequest.withResources(dcInstance.getInstanceId()).withTags(
				new Tag("Project", "2.1"));
		ec2.createTags(createTagsRequest);

		return dcInstance;
	}

	public static String executePost(String targetURL) {
		StringBuffer response = new StringBuffer();
		String str = "";
		try {
			URL url = new URL(targetURL);

			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((str = bfr.readLine()) != null) {
				response.append(str);
				response.append('\r');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}

	public static double executeINI(String targetURL) {
		try {
			try {
				URL url = new URL(targetURL);

				BufferedReader bfr = new BufferedReader(new InputStreamReader(
						url.openStream()));
				Ini ini = new Ini(bfr);
				double rpsVal = 0;
				for (String sectionName : ini.keySet()) {
					rpsVal = 0;
					if (sectionName.contains("Minute")) {
						Section section = ini.get(sectionName);
						for (String optionKey : section.keySet()) {
							rpsVal = rpsVal
									+ Double.parseDouble(section.get(optionKey));
						}
					}
				}
				bfr.close();
				return rpsVal;
			} catch (InvalidFileFormatException fex) {
				fex.printStackTrace();
				return 0;
			} catch (FileNotFoundException ffx) {
				ffx.printStackTrace();
				return 0;
			} catch (IOException ex) {
				ex.printStackTrace();
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void threadSleep() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}