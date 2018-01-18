package com.aws.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class LatencyMeasure {
	static List<String> uploadList;
	static List<String> dwList;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		AWSCredentials credentials = null;
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		// load a properties file
		prop.load(input);
		final String NEW_LINE_SEPARATOR = "\n";
		FileWriter uploadOut = new FileWriter("upload.csv", true);
		CSVPrinter up_printer = CSVFormat.DEFAULT.withHeader("REGION", "BUCKETNAME", "FILENAME", "UPLOADTIME")
								.withRecordSeparator(NEW_LINE_SEPARATOR).print(uploadOut);

		FileWriter dwout = new FileWriter("download.csv", true);
		CSVPrinter dw_printer = CSVFormat.DEFAULT.withHeader("REGION", "BUCKETNAME",  "FILENAME", "DOWNLOADTIME")
								.withRecordSeparator(NEW_LINE_SEPARATOR).print(dwout);


		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. ",e);
		}

		AmazonS3 s3_region1 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(prop.getProperty("region1"))
				.build();

		AmazonS3 s3_region2 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(prop.getProperty("region2"))
				.build();

		AmazonS3 s3_region3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(prop.getProperty("region3"))
				.build();

		String bucket1 = prop.getProperty("bucket1");
		String bucket2 = prop.getProperty("bucket2");
		String bucket3 = prop.getProperty("bucket3");
		File file = new File(args[0]);
		String key = file.getName();
		String dwloadLoc = prop.getProperty("downloadLocation");

		uploadList = upload(s3_region1, bucket1, file, key, prop.getProperty("region1"));
		up_printer.printRecord(uploadList);
		uploadList = upload(s3_region2, bucket2, file, key, prop.getProperty("region2"));
		up_printer.printRecord(uploadList);
		uploadList = upload(s3_region3, bucket3, file, key, prop.getProperty("region3"));
		up_printer.printRecord(uploadList);
		dwList = download(s3_region1, bucket1, key, prop.getProperty("region1"), dwloadLoc);
		dw_printer.printRecord(dwList);
		dwList = download(s3_region2, bucket2, key, prop.getProperty("region2"), dwloadLoc);
		dw_printer.printRecord(dwList);
		dwList = download(s3_region3, bucket3, key, prop.getProperty("region3"), dwloadLoc);
		dw_printer.printRecord(dwList);
		
		uploadOut.flush();
		uploadOut.close();
		dwout.flush();
		dwout.close();
	}



	static List<String> upload(AmazonS3 s3Obj, String bucket, File file, String key, String region){
		List<String> uploadListCsv = new ArrayList<String>();
		try{
			uploadListCsv.add(region);
			uploadListCsv.add(bucket);
			uploadListCsv.add(key);
			System.out.println("Uploading File to bucket::: "+bucket);
			Instant start = Instant.now();
			s3Obj.putObject(new PutObjectRequest(bucket, key, file));
			Instant end = Instant.now();
			System.out.println("Done uploading!");
			long d = Duration.between(start, end).toMillis();
			String timeElapsed = Long.toString(d);
			System.out.println("timeLapsed_upload - "+bucket+" - "+timeElapsed+"\n");
			uploadListCsv.add(timeElapsed);
		}

		catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, the request was made "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return uploadListCsv;

	}

	static List<String> download(AmazonS3 s3Obj, String bucket, String key, String region, String dwLoc){
		List<String> dwListCsv = new ArrayList<String>();
		try{
			dwListCsv.add(region);
			dwListCsv.add(bucket);
			dwListCsv.add(key);
			System.out.println("Downloading File from bucket - "+bucket);
			File localFile = new File(dwLoc+bucket+"_"+key+".bin");
			Instant s = Instant.now();
			s3Obj.getObject(new GetObjectRequest(bucket, key),localFile);
			Instant e = Instant.now();
			long d = Duration.between(s, e).toMillis();
			String timeElapsed = Long.toString(d);
			System.out.println("timeLapsed_download - "+bucket+" - "+timeElapsed+"\n");
			dwListCsv.add(timeElapsed);
		}

		catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, the request was made "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

		return dwListCsv;
	}

}

























