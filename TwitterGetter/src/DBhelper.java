import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class DBhelper {
	protected static AmazonDynamoDBClient DynamoClient;
	public DBhelper() {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("please make sure your credential is at the right place");
		}
		DynamoClient = new AmazonDynamoDBClient(credentials);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		DynamoClient.setRegion(usEast1);
	}
	public boolean testAndCheck(String tableName) {
		if(Tables.doesTableExist(DynamoClient, tableName)) {
			return true;
		}
		return false;
	}
	public boolean addItem(String tableName,Map<String, AttributeValue> item) {
		PutItemRequest putItemRequest = new PutItemRequest(tableName,item);
		DynamoClient.putItem(putItemRequest);
		return true;
	}
	public ScanResult searchItem(String tableName,Map<String, Condition> scanFilter , ArrayList<String> attribute) {
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		ScanResult rs = DynamoClient.scan(scanRequest);
		return rs;
	}
	public boolean deleteItem(String tableName, Map<String,AttributeValue> item) {
		DeleteItemRequest deleteItemRequest = new DeleteItemRequest().withTableName(tableName).withKey(item);
		DynamoClient.deleteItem(deleteItemRequest);
		return true;
	}
	public long findId(String tableName) {
		long max = 0;
		HashMap<String, Condition> scanfilter = new HashMap<String,Condition>();
		ScanResult sResult = DynamoClient.scan(new ScanRequest(tableName).withScanFilter(scanfilter).withAttributesToGet("id"));
		List<Map<String,AttributeValue>> list = sResult.getItems();
		for (Map<String, AttributeValue> map : list) {
			long temp = Long.parseLong(map.get("id").getN());
			if (temp  > max) {
				max = temp;
			}
		}
		return max;
	}
}
