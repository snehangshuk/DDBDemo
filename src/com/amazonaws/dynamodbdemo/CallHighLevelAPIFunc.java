package com.amazonaws.dynamodbdemo;

import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.TableDescription;


public class CallHighLevelAPIFunc {

	public static void main(String[] args) {
		DDBHighLevelAPIDemo demo=new DDBHighLevelAPIDemo();
		AWSResources.DYNAMODB.setRegion(AWSResources.REGION);
		DescribeTableRequest request = new DescribeTableRequest(AWSResources.DDB_TABLE_NAME);
		TableDescription table = AWSResources.DYNAMODB.describeTable(request).getTable();
		if(table.getTableStatus().equalsIgnoreCase("ACTIVE"))
			demo.deleteTable();
		demo.createTable();
		demo.putItems();
		demo.display(demo.retrieveItem(103));
	}	
}
