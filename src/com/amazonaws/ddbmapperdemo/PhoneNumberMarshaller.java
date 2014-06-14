package com.amazonaws.ddbmapperdemo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;

public class PhoneNumberMarshaller implements DynamoDBMarshaller<PhoneNumber>
{
	
	@Override
	public String marshall(PhoneNumber number)
	{
		return "(" + number.getCountryCode() + ") " + number.getAreaCode() + "-" + number.getPhoneNumber();
	}
	
	@Override
	public PhoneNumber unmarshall(Class<PhoneNumber> clazz, String s)
	{
		String[] countryCodeAndNumber = s.split(" ");
    String countryCode = countryCodeAndNumber[0].substring(1,4);
    String[] phoneNumberAndSlid = countryCodeAndNumber[1].split("-");
    PhoneNumber number = new PhoneNumber();
    number.setCountryCode(countryCode);
    number.setAreaCode(phoneNumberAndSlid[0]);
    number.setPhoneNumber(phoneNumberAndSlid[1]);
    return number;
	}
}