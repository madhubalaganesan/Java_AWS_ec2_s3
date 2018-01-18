package com.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

public class MonitorCPUUtil {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("D:\\Academics\\PERCCOM_Academics\\Semester3_Skelleftea\\CloudServices\\config.properties");
		prop.load(input);
		String identity = prop.getProperty("accesskey");
		String credential = prop.getProperty("secretaccesskey");

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(identity, credential);

		try {
			AmazonCloudWatchClient cw = new AmazonCloudWatchClient(awsCreds) ;
			long offsetInMilliseconds = 1000 * 60 * 60 * 24;

			GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
					.withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
					.withNamespace("AWS/EC2")
					.withPeriod(60 * 60)
					.withDimensions(new Dimension().withName("i-0c4ee31a410c44adb").withValue("i-0c4ee31a410c44adb"))
					.withMetricName("CPUUtilization")
					.withStatistics("Average", "Maximum")
					.withEndTime(new Date());
			GetMetricStatisticsResult getMetricStatisticsResult = cw.getMetricStatistics(request);

			System.out.println("label : " + getMetricStatisticsResult.getLabel());

			TreeMap metricValues = new TreeMap<Long, Double>();
			//logger.error("metrics label = " + result.getLabel() + " result datapoints size " + result.getDatapoints().size());

			for (Datapoint dp : getMetricStatisticsResult.getDatapoints()) {
				metricValues.put(dp.getTimestamp().getTime(), dp.getAverage());
			}

			Set set = metricValues.entrySet();
			Iterator i = set.iterator();
			while(i.hasNext()) {
				Map.Entry me = (Map.Entry)i.next();
				System.out.print(me.getKey() + ": ");
				System.out.println(me.getValue());
			}

		} catch (AmazonServiceException ase) {
			System.out.println(ase);

			/*logger.error("Caught an AmazonServiceException, which means the request was made "
			+ "to Amazon EC2, but was rejected with an error response for some reason.");
			logger.error("Error Message: " + ase.getMessage());
			logger.error("HTTP Status Code: " + ase.getStatusCode());
			logger.error("AWS Error Code: " + ase.getErrorCode());
			logger.error("Error Type: " + ase.getErrorType());
			logger.error("Request ID: " + ase.getRequestId());*/


		}

	}
}