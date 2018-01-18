package com.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

public class TerminateInstance {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("D:\\Academics\\PERCCOM_Academics\\Semester3_Skelleftea\\CloudServices\\config.properties");
		prop.load(input);
		String identity = prop.getProperty("accesskey");
		String credential = prop.getProperty("secretaccesskey");

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(identity, credential);

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
				.withRegion("us-west-2")
				.build();

		TerminateInstancesRequest request = new TerminateInstancesRequest()
				.withInstanceIds("i-012234371307deffe");

		TerminateInstancesResult result = ec2.terminateInstances(request);
		System.out.println("terminated the instance.."+result);
	}

}
