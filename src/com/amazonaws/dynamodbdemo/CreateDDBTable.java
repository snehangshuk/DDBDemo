package com.amazonaws.dynamodbdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class CreateDDBTable {
	
	public static void main(String[] args) {
		
		createTable();
	}
	static void CreateItems()
	{
		Map<String,AttributeValue> attributeValues = new HashMap<String,AttributeValue>();
		
		AWSResources.setcID("1001");
		AWSResources.setcName("Snehangshu Karmakar");
		AWSResources.setcDate("16-02-1978");
		attributeValues.put("cId", new AttributeValue().withS(AWSResources.getcID()));
		attributeValues.put("cName", new AttributeValue().withS(AWSResources.getcName()));
		attributeValues.put("cDate", new AttributeValue().withS(AWSResources.getcDate()));
		
		PutItemRequest putItemRequest = new PutItemRequest().withTableName(AWSResources.DDB_TABLE_NAME).withItem(attributeValues);
		AWSResources.DYNAMODB.putItem(putItemRequest);
	}
	static void createTable() {
		CreateTableRequest request = new CreateTableRequest().withTableName(AWSResources.DDB_TABLE_NAME);
		
		request.setProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(2L));
		
		ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
		tableKeySchema.add(new KeySchemaElement().withAttributeName("cId").withKeyType(KeyType.HASH));
		tableKeySchema.add(new KeySchemaElement().withAttributeName("cDate").withKeyType(KeyType.RANGE));
		request.setKeySchema(tableKeySchema);
		
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("cId").withAttributeType(ScalarAttributeType.S));
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("cDate").withAttributeType(ScalarAttributeType.S));
		request.setAttributeDefinitions(attributeDefinitions);
		
		AWSResources.DYNAMODB.setRegion(AWSResources.REGION);
		
		AWSResources.DYNAMODB.createTable(request);
		
		try {
			waitForTableToBecomeActive(AWSResources.DDB_TABLE_NAME);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void waitForTableToBecomeActive(String tableName) throws InterruptedException {
		System.out.println("Waiting for "+ tableName + " table to become ACTIVE..");
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (10 * 60 * 1000);
		while ( System.currentTimeMillis() < endTime ) {
			Thread.sleep(1000 * 20);
			try {
				DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
				TableDescription table = AWSResources.DYNAMODB.describeTable(describeTableRequest).getTable();
				if (table == null) continue;
				
				String tableStatus = table.getTableStatus();
				System.out.println(" - current state: " + tableStatus);
				if (tableStatus.equals(TableStatus.ACTIVE.toString())){
					System.out.println ("Table Created...");
					System.out.println("Items being populated...");
					CreateItems();
					return;
				}
			}catch (AmazonServiceException ase){
				if (!ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException"))
						throw ase;
			}
		}
	}
}
