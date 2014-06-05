/**
 * Create a DDB Table using CLI or using SDK
 * aws dynamodb create-table --table-name ProductCatalog 
 * --attribute-definitions AttributeName=Id,AttributeType=N 
 * --key-schema AttributeName=Id,KeyType=HASH 
 * --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
 */
package com.amazonaws.dynamodbdemo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

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



	public static void main(String[] args) {
		
		DynamoDBMapper mapper = new DynamoDBMapper(AWSResources.DYNAMODB);
		AWSResources.DYNAMODB.setRegion(AWSResources.REGION);
		DDBHighLevelAPIDemo item=new DDBHighLevelAPIDemo();
		item.setId(102);
		item.setTitle("Book 102 Title");
		item.setISBN("222-2222222");
		item.setBookAuthors(new HashSet<String>(Arrays.asList("Author 1","Author 2")));
		item.setSomeProp("Test");
		mapper.save(item);
	}
}
