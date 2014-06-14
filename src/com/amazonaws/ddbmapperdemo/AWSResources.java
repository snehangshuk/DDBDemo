package com.amazonaws.ddbmapperdemo;


import java.util.ArrayList;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class AWSResources {

	public static final String TABLENAME = "UsersTable";
	public static final Region REGION = Region.getRegion(Regions.AP_SOUTHEAST_1);
	public static final AWSCredentialsProvider CREDENTAILS_PROVIDER = new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider(),
																																					new ProfileCredentialsProvider("DevOnAWS-QwikLab"));
	public static final AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(CREDENTAILS_PROVIDER);
	
	public static void createTable() throws InterruptedException
	{
		
		CreateTableRequest req = new CreateTableRequest().withTableName(TABLENAME);
		
		req.setProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(2L));
		
		ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
		tableKeySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH));
		req.setKeySchema(tableKeySchema);
		
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType(ScalarAttributeType.S));
		req.setAttributeDefinitions(attributeDefinitions);
		
		dbClient.setRegion(REGION);
		dbClient.createTable(req);
		
		System.out.println("Table creation is in progress...");
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (10 * 60 * 1000);
		while ( System.currentTimeMillis() < endTime ) {
			Thread.sleep(1000 * 20);
			try {
				DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(TABLENAME);
				TableDescription table = dbClient.describeTable(describeTableRequest).getTable();
				if (table == null) continue;
				
				String tableStatus = table.getTableStatus();
				System.out.println(" - current state: " + tableStatus);
				if (tableStatus.equals(TableStatus.ACTIVE.toString())){
					System.out.println ("Table Created...");
					return;
				}
			}	
			catch (AmazonServiceException ase){
				if (!ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException"))
						throw ase;
			}
		}
	}
}
