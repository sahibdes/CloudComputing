import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeletePolicyRequest;
import com.amazonaws.services.autoscaling.model.DescribePoliciesRequest;
import com.amazonaws.services.autoscaling.model.InstanceMonitoring;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
import com.amazonaws.services.autoscaling.model.ScalingPolicy;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.DeleteAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;

public class Cloud2 {
	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.load(Cloud2.class
					.getResourceAsStream("/AwsCredentials.properties"));
			// access credentials
			BasicAWSCredentials bawsc = new BasicAWSCredentials(
					properties.getProperty("accessKey"),
					properties.getProperty("secretKey"));
			AmazonEC2 ec2 = new AmazonEC2Client(bawsc);

			// create security group
			String groupId = createsecurityGroup(bawsc);

			Thread.sleep(300000);

			// create elastic load balancer
			String dnsName = createELB(bawsc, groupId);
			// create auto scale group
			createAWGLaunch(bawsc, groupId);

			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < 6; i++) {
				// warm up
				warmUp(dnsName, bawsc);
				try {
					Thread.sleep(300000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// final run
			finalRun(dnsName, bawsc);

			try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// delete alarms
			AmazonCloudWatchClient acw = new AmazonCloudWatchClient(bawsc);
			DeleteAlarmsRequest deleteAlarmsRequest = new DeleteAlarmsRequest();
			List<String> deleteAlarmNames = new ArrayList<String>();
			deleteAlarmNames.add("upAlarm");
			deleteAlarmNames.add("downAlarm");
			deleteAlarmsRequest.withAlarmNames(deleteAlarmNames);
			acw.deleteAlarms(deleteAlarmsRequest);

			AmazonAutoScalingClient autoScalingClient = new AmazonAutoScalingClient(
					bawsc);
			// terminate instances attached to ASG
			TerminateInstanceInAutoScalingGroupRequest terminateInstanceInAutoScalingGroupRequest = new TerminateInstanceInAutoScalingGroupRequest();
			for (AutoScalingInstanceDetails as : autoScalingClient
					.describeAutoScalingInstances().getAutoScalingInstances()) {
				if (as.getAutoScalingGroupName().equals("Cloud2ASG")) {
					terminateInstanceInAutoScalingGroupRequest.withInstanceId(
							as.getInstanceId())
							.withShouldDecrementDesiredCapacity(Boolean.FALSE);
					autoScalingClient
							.terminateInstanceInAutoScalingGroup(terminateInstanceInAutoScalingGroupRequest);
				}
			}

			// delete policies
			DeletePolicyRequest deletePolicyRequest = new DeletePolicyRequest();
			DescribePoliciesRequest describePoliciesRequest = new DescribePoliciesRequest();

			for (ScalingPolicy policy : autoScalingClient.describePolicies(
					describePoliciesRequest).getScalingPolicies()) {
				deletePolicyRequest.withPolicyName(policy.getPolicyARN());

				autoScalingClient.deletePolicy(deletePolicyRequest);
			}

			// delete ASG
			DeleteAutoScalingGroupRequest deleteAutoScalingGroupRequest = new DeleteAutoScalingGroupRequest();
			deleteAutoScalingGroupRequest.withAutoScalingGroupName("Cloud2ASG")
					.withForceDelete(Boolean.TRUE);
			autoScalingClient
					.deleteAutoScalingGroup(deleteAutoScalingGroupRequest);

			// delete Launch Configuration
			DeleteLaunchConfigurationRequest deleteLaunchConfigurationRequest = new DeleteLaunchConfigurationRequest();
			deleteLaunchConfigurationRequest
					.withLaunchConfigurationName("Cloud_2_lc");
			autoScalingClient
					.deleteLaunchConfiguration(deleteLaunchConfigurationRequest);

			AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(
					bawsc);
			// delete load balances
			DeleteLoadBalancerRequest deleteLoadBalancerRequest = new DeleteLoadBalancerRequest();
			deleteLoadBalancerRequest.withLoadBalancerName("Cloud2elb");
			elb.deleteLoadBalancer(deleteLoadBalancerRequest);

			try {
				Thread.sleep(180000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// delete security group
			DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest();
			deleteSecurityGroupRequest.withGroupId(groupId);
			ec2.deleteSecurityGroup(deleteSecurityGroupRequest);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// method to create Security Group
	public static String createsecurityGroup(BasicAWSCredentials bawsc)
			throws IOException {
		// Create the AmazonEC2Client object so we can call various APIs.
		AmazonEC2 ec2 = new AmazonEC2Client(bawsc);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		ec2.setRegion(usEast1);

		// Create a new security group.
		try {
			CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest(
					"msbsg4", "MSB Secrutiy Group");

			CreateSecurityGroupResult result = ec2
					.createSecurityGroup(securityGroupRequest);
		} catch (AmazonServiceException ase) {
			// Likely this means that the group is already created, so ignore.
			System.out.println(ase.getMessage());
		}

		String ipAddr = "0.0.0.0/0";

		// Create a range that you would like to populate.
		List<String> ipRanges = Collections.singletonList(ipAddr);

		IpPermission ipPermission = new IpPermission().withIpProtocol("-1")
				.withIpRanges(ipRanges);

		List<IpPermission> ipPermissions = Collections
				.singletonList(ipPermission);
		try {
			// Authorize the ports to the used.
			AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(
					"msbsg4", ipPermissions);
			ec2.authorizeSecurityGroupIngress(ingressRequest);
			String groupID = "";
			List<SecurityGroup> lsgrp = ec2.describeSecurityGroups()
					.getSecurityGroups();
			for (SecurityGroup securityGroup : lsgrp) {
				if (securityGroup.getGroupName().equals("msbsg4"))
					groupID = securityGroup.getGroupId();
			}
			return groupID;
		} catch (AmazonServiceException ase) {
			System.out.println(ase.getMessage());
			return "Not a Security Group";
		}
	}

	// create ELB
	public static String createELB(BasicAWSCredentials bawsc, String groupID) {
		AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(
				bawsc);
		CreateLoadBalancerRequest loadBalanceRqst = new CreateLoadBalancerRequest();

		// set parameters for Load Balancer Request object
		loadBalanceRqst.setLoadBalancerName("Cloud2elb");
		loadBalanceRqst.setRequestCredentials(bawsc);

		com.amazonaws.services.elasticloadbalancing.model.Tag tag = new com.amazonaws.services.elasticloadbalancing.model.Tag()
				.withKey("Project").withValue("2.2");

		loadBalanceRqst.withAvailabilityZones("us-east-1a")
				.withSecurityGroups(groupID).withTags((tag));

		List<Listener> listeners = new ArrayList<Listener>();
		listeners.add(new Listener("HTTP", 80, 80));
		loadBalanceRqst.setListeners(listeners);

		// define health check parameters
		HealthCheck healthChk = new HealthCheck().withHealthyThreshold(2)
				.withInterval(30).withTarget("HTTP:80/heartbeat")
				.withUnhealthyThreshold(10).withTimeout(5);

		ConfigureHealthCheckRequest hlthChkRqst = new ConfigureHealthCheckRequest(
				"Cloud2elb", healthChk);

		CreateLoadBalancerResult createlbRslt = elb
				.createLoadBalancer(loadBalanceRqst);

		ConfigureHealthCheckResult hlthChkRslt = elb.configureHealthCheck(
				hlthChkRqst).withHealthCheck(healthChk);

		return createlbRslt.getDNSName();
	}

	// Create ASG
	public static void createAWGLaunch(BasicAWSCredentials bawsc, String groupID) {
		AmazonAutoScalingClient autoScalingClient = new AmazonAutoScalingClient(
				bawsc);
		AmazonCloudWatchClient cloudWatchClient = new AmazonCloudWatchClient(
				bawsc);

		CreateLaunchConfigurationRequest createLaunchConfigurationRequest = new CreateLaunchConfigurationRequest();
		createLaunchConfigurationRequest
				.withInstanceMonitoring(
						new InstanceMonitoring().withEnabled(Boolean.TRUE))
				.withSecurityGroups(groupID).withInstanceType("m3.medium")
				.withImageId("ami-7c0a4614")
				.withLaunchConfigurationName("Cloud_2_lc");
		autoScalingClient
				.createLaunchConfiguration(createLaunchConfigurationRequest);

		com.amazonaws.services.autoscaling.model.Tag tag = new com.amazonaws.services.autoscaling.model.Tag()
				.withKey("Project").withValue("2.2");

		CreateAutoScalingGroupRequest autoScalingGroupRequest = new CreateAutoScalingGroupRequest()
				.withAutoScalingGroupName("Cloud2ASG")
				.withAvailabilityZones("us-east-1a").withDefaultCooldown(120)
				.withHealthCheckGracePeriod(300)
				.withLoadBalancerNames("Cloud2elb").withMaxSize(5)
				.withMinSize(1).withTags(tag)
				.withLaunchConfigurationName("Cloud_2_lc")
				.withHealthCheckType("ELB").withDesiredCapacity(2);

		autoScalingClient.createAutoScalingGroup(autoScalingGroupRequest);

		PutScalingPolicyRequest UpscalingPolicyRequest = new PutScalingPolicyRequest()
				.withAutoScalingGroupName("Cloud2ASG")
				.withPolicyName("Cloud22PolicyUp").withScalingAdjustment(1)
				.withCooldown(600).withAdjustmentType("ChangeInCapacity");

		PutScalingPolicyResult UpscalingPolicyResult = autoScalingClient
				.putScalingPolicy(UpscalingPolicyRequest);

		String pArn = UpscalingPolicyResult.getPolicyARN();

		String upArn = pArn;

		PutMetricAlarmRequest upMetricAlarmRequest = new PutMetricAlarmRequest()
				.withAlarmName("upAlarm")
				.withMetricName("CPUUtilization")
				.withComparisonOperator(ComparisonOperator.GreaterThanThreshold)
				.withStatistic(Statistic.Average)
				.withUnit(StandardUnit.Percent).withThreshold(80d)
				.withPeriod(300).withEvaluationPeriods(2)
				.withNamespace("AWS/EC2");

		List alarmActionList = new ArrayList();
		alarmActionList.add(upArn);

		upMetricAlarmRequest.setAlarmActions(alarmActionList);

		cloudWatchClient.putMetricAlarm(upMetricAlarmRequest);

		PutScalingPolicyRequest downScalingPolicyRequest = new PutScalingPolicyRequest()
				.withAutoScalingGroupName("Cloud2ASG")
				.withPolicyName("Cloud22Policydown").withScalingAdjustment(-1)
				.withCooldown(600).withAdjustmentType("ChangeInCapacity");

		PutScalingPolicyResult downScalingPolicyResult = autoScalingClient
				.putScalingPolicy(downScalingPolicyRequest);

		String downArn = downScalingPolicyResult.getPolicyARN();

		PutMetricAlarmRequest downMetricAlarmRequest = new PutMetricAlarmRequest()
				.withAlarmName("downAlarm").withMetricName("CPUUtilization")
				.withComparisonOperator(ComparisonOperator.LessThanThreshold)
				.withStatistic(Statistic.Average)
				.withUnit(StandardUnit.Percent).withThreshold(20d)
				.withPeriod(300).withEvaluationPeriods(2)
				.withNamespace("AWS/EC2");

		List downAlarmActionList = new ArrayList();
		downAlarmActionList.add(downArn);

		downMetricAlarmRequest.setAlarmActions(downAlarmActionList);

		cloudWatchClient.putMetricAlarm(downMetricAlarmRequest);
	}

	// Run warm up and final run
	public static void warmUp(String elbDnsName, BasicAWSCredentials bawsc) {
		String url = "http://" + findLoadGenDNS(bawsc) + "/warmup?dns="
				+ elbDnsName;
		String testLog = "";

		testLog = executePost(url);
	}

	public static void finalRun(String elbDnsName, BasicAWSCredentials bawsc) {
		String url = "http://" + findLoadGenDNS(bawsc) + "/junior?dns="
				+ elbDnsName;
		String testLog = executePost(url);
	}

	// execute request
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
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}

	// sleep thread
	public static void threadSleep() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// find DNS Name of load generator
	public static String findLoadGenDNS(BasicAWSCredentials bawsc) {
		String lgPublicDnsName = null;
		// Launch an EC2 Client
		AmazonEC2Client amazonEC2Client = new AmazonEC2Client(bawsc);

		// Obtain a list of Reservations
		List reservations = amazonEC2Client.describeInstances()
				.getReservations();
		int reservationCount = reservations.size();
		for (int i = 0; i < reservationCount; i++) {
			List instances = ((Reservation) reservations.get(i)).getInstances();
			int instanceCount = instances.size();

			// Print the instance IDs of every instance in the reservation.
			for (int j = 0; j < instanceCount; j++) {
				Instance instance = (Instance) instances.get(j);
				if (!instance.getPublicDnsName().isEmpty()
						&& (instance.getImageId().equals("ami-ae0a46c6"))) {

					lgPublicDnsName = instance.getPublicDnsName();
				}
			}
		}
		return lgPublicDnsName;
	}
}
