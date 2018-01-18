package com.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

public class Utilization {

    public static void main(String[] args) throws IOException {
    	Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("D:\\Academics\\PERCCOM_Academics\\Semester3_Skelleftea\\CloudServices\\config.properties");
		prop.load(input);
		String identity = prop.getProperty("accesskey");
		String credential = prop.getProperty("secretaccesskey");
		
        final String awsAccessKey = identity;
        final String awsSecretKey = credential;
        final String instanceId = "i-0beb7b1f5b6a3356b";
        //final String metric = "NetworkIn";
        
    /*  CPUUtilization
        NetworkOut
        NetworkIn
        VolumeWriteOps
        VolumeReadOps
        RequestCount*/

        final AmazonCloudWatchClient client = client(awsAccessKey, awsSecretKey);
        final GetMetricStatisticsRequest request = request(instanceId); 
        final GetMetricStatisticsResult result = result(client, request);
        toStdOut(result, instanceId);   
    }

    @SuppressWarnings("deprecation")
	private static AmazonCloudWatchClient client(final String awsAccessKey, final String awsSecretKey) {
        final AmazonCloudWatchClient client = new AmazonCloudWatchClient(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        client.setEndpoint("http://monitoring.us-east-2.amazonaws.com");
        return client;
    }

    private static GetMetricStatisticsRequest request(final String instanceId) {
        final long twentyFourHrs = 1000 * 60 * 60 * 24;
        final int oneHour = 60 * 60;
        return new GetMetricStatisticsRequest()
            .withStartTime(new Date(new Date().getTime()- twentyFourHrs))
            .withNamespace("AWS/EC2")
            .withPeriod(oneHour)
            .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
            .withMetricName("CPUUtilization")
            .withStatistics("Average", "Maximum")
            .withUnit("Percent")
            .withEndTime(new Date());
    }

    private static GetMetricStatisticsResult result(
            final AmazonCloudWatchClient client, final GetMetricStatisticsRequest request) {
         return client.getMetricStatistics(request);
    }

    private static void toStdOut(final GetMetricStatisticsResult result, final String instanceId) {
        System.out.println(result); // outputs empty result: {Label: CPUUtilization,Datapoints: []}
        for (final Datapoint dataPoint : result.getDatapoints()) {
            System.out.printf("%s instance's average utilization : %s%n", instanceId, dataPoint.getAverage());      
            System.out.printf("%s instance's max utilization : %s%n", instanceId, dataPoint.getMaximum());
        }
    }
}
