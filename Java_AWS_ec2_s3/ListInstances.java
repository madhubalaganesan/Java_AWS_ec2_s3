package com.test;

import java.io.FileInputStream;



import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

public class ListInstances {

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
				.withRegion("us-east-2")
				.build();
		
		    DescribeInstancesRequest request = new DescribeInstancesRequest();
		    DescribeInstancesResult response = ec2.describeInstances(request);
		    int count = 0;
		    for(Reservation reservation : response.getReservations()) {
		        for(Instance instance : reservation.getInstances()) {
		        	count++;
		        	System.out.println("ID : "+instance.getInstanceId()+" AMI : "+instance.getImageId()+" Type : "+instance.getInstanceType()+" State : "+instance.getState().getName()+" Monitoring : "+instance.getMonitoring().getState()+" region : "+instance.getPlacement().getAvailabilityZone());
		        }
		    }
		    System.out.println("count::"+count);

		}

	}

