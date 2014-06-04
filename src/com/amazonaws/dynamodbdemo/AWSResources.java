package com.amazonaws.dynamodbdemo;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


public class AWSResources {
	
	public static final String DDB_TABLE_NAME = "customer";
	public static final Region REGION = Region.getRegion(Regions.AP_SOUTHEAST_1);
	public static final AWSCredentialsProvider CREDENTIAL_PROVIDER = new AWSCredentialsProviderChain(
																	new InstanceProfileCredentialsProvider(),
																	new ProfileCredentialsProvider("DevOnAWS-QwikLab"));
	public static final AmazonDynamoDBClient DYNAMODB = new AmazonDynamoDBClient(CREDENTIAL_PROVIDER);
	
	private static String cID;
	private static String cName;
	private static String cDate;
	public static final String getcID() {
		return cID;
	}
	public static final void setcID(String cID) {
		AWSResources.cID = cID;
	}
	public static final String getcName() {
		return cName;
	}
	public static final void setcName(String cName) {
		AWSResources.cName = cName;
	}
	public static final String getcDate() {
		return cDate;
	}
	public static final void setcDate(String cDate) {
		AWSResources.cDate = cDate;
	}
	
	
	
}
	