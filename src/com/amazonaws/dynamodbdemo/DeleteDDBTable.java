package com.amazonaws.dynamodbdemo;

import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class DeleteDDBTable {

	public static void main(String[] args) {
		DeleteTableRequest deleteTableRequest = new DeleteTableRequest().withTableName(AWSResources.DDB_TABLE_NAME);
		AWSResources.DYNAMODB.setRegion(AWSResources.REGION);
		AWSResources.DYNAMODB.deleteTable(deleteTableRequest);
		waitForTableToBeDeleted(AWSResources.DDB_TABLE_NAME);
	}
	static void waitForTableToBeDeleted(String tableName)
	{
		System.out.println("Waiting for "+ tableName + " table to be DELETING..");
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (10 * 60 * 1000);
			while ( System.currentTimeMillis() < endTime ) {
				try {
					DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
					TableDescription table = AWSResources.DYNAMODB.describeTable(describeTableRequest).getTable();
				if (table == null) continue;
				
				String tableStatus = table.getTableStatus();
				System.out.println(" - current state: " + tableStatus);
				if (tableStatus.equals(TableStatus.ACTIVE.toString())) return;
				
				} catch (ResourceNotFoundException e) {
				System.out.println("Table "+ tableName + " is not found. It was deleted.");
				return;
				}
				try {Thread.sleep(1000 * 20);} catch (Exception e){}
			}
			throw new RuntimeException ("Table "+ tableName + " was never deleted");
		}
	}	