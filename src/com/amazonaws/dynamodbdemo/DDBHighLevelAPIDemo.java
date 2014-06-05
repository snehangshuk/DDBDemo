/**
 * Create a DDB Table using CLI or using SDK
 * aws dynamodb create-table --table-name ProductCatalog 
 * --attribute-definitions AttributeName=Id,AttributeType=N 
 * --key-schema AttributeName=Id,KeyType=HASH 
 * --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
 */
package com.amazonaws.dynamodbdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

@DynamoDBTable(tableName = "ProductCatalog")
public class DDBHighLevelAPIDemo {
	
	private Integer id;
	private String title;
	private String ISBN;
	private Set<String> bookAuthors;
	private String someProp;
	

	@DynamoDBHashKey(attributeName = "Id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName = "Title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@DynamoDBAttribute(attributeName = "ISBN")
	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	
	@DynamoDBAttribute(attributeName = "Authors")
	public Set<String> getBookAuthors() {
		return bookAuthors;
	}

	public void setBookAuthors(Set<String> bookAuthors) {
		this.bookAuthors = bookAuthors;
	}

	@DynamoDBIgnore
	public String getSomeProp() {
		return someProp;
	}

	public void setSomeProp(String someProp) {
		this.someProp = someProp;
	}

	public void putItems()
	{
		System.out.println("Items being populated...");
		AWSResources.DYNAMODB.setRegion(AWSResources.REGION);
		DDBHighLevelAPIDemo item=new DDBHighLevelAPIDemo();
		item.setId(103);
		item.setTitle("Book 103 Title");
		item.setISBN("222-2222222456");
		item.setBookAuthors(new HashSet<String>(Arrays.asList("Author 4","Author 5")));
		item.setSomeProp("Test1");
		AWSResources.MAPPER.save(item);
	}
	
	public DDBHighLevelAPIDemo retrieveItem(int keyId)
	{
		AWSResources.DYNAMODB.setRegion(AWSResources.REGION);
		return AWSResources.MAPPER.load(DDBHighLevelAPIDemo.class, keyId);
	}
	
	public void updateItem(int keyId)
	{
		DDBHighLevelAPIDemo itemsRetrieved = retrieveItem(keyId);
		itemsRetrieved.setISBN("622-222222222");
		itemsRetrieved.setBookAuthors(new HashSet<String>(Arrays.asList("Author2","Author3")));
		AWSResources.MAPPER.save(itemsRetrieved);
	}
	
	public void deleteItem(int keyId)
	{
		AWSResources.MAPPER.delete(retrieveItem(keyId));
	}
	
	public void createTable()
	{
		AWSResources.DYNAMODB.setRegion(AWSResources.REGION);
		CreateTableRequest request = AWSResources.MAPPER.generateCreateTableRequest(DDBHighLevelAPIDemo.class);
		request.setProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
		
		ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
		tableKeySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH));
		request.setKeySchema(tableKeySchema);
		
		ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType(ScalarAttributeType.N));
		request.setAttributeDefinitions(attributeDefinitions);
		
		AWSResources.DYNAMODB.createTable(request);
		try {
			waitForTableToBecomeActive(AWSResources.DDB_TABLE_NAME);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	public void deleteTable()
	{
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
						return;
					}
				}catch (AmazonServiceException ase){
					if (!ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException"))
							throw ase;
				}
			}
	}
	
	public void display(DDBHighLevelAPIDemo items)
	{
		System.out.println("Item Retrieved...");
		System.out.println(items.getId() +"|"+items.getTitle()+"|"+items.getBookAuthors()+"|"+items.getISBN());
	}
}
