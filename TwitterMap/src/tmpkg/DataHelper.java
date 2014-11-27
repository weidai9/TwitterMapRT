package tmpkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class DataHelper {

	private AmazonDynamoDBClient dynamoDB;

	public DataHelper() {

		dynamoDB = new AmazonDynamoDBClient(new AWSCredentialsProviderChain(
				new InstanceProfileCredentialsProvider(),
				new ClasspathPropertiesFileCredentialsProvider()));
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		dynamoDB.setRegion(usEast1);
	}

	public ArrayList<Twitter> getDataById(String id) {
		String tableName = "twitterTable";
		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
		Condition condition = new Condition().withComparisonOperator(
				ComparisonOperator.EQ.toString()).withAttributeValueList(
				new AttributeValue().withS(id));
		scanFilter.put("id", condition);
		ScanRequest scanRequest = new ScanRequest(tableName)
				.withScanFilter(scanFilter);
		ScanResult scanResult = dynamoDB.scan(scanRequest);
		ArrayList<Twitter> twitList = new ArrayList<Twitter>();
		List<Map<String, AttributeValue>> resultList = scanResult.getItems();
		for (Map<String, AttributeValue> res : resultList) {
			Twitter twit = new Twitter();
			twit.setId(res.get("id").getN());
			twit.setKeyword(res.get("keyword").getS());
			twit.setLatitude(res.get("latitude").getS());
			twit.setLongtitude(res.get("longtitude").getS());
			String text = res.get("text").getS();
			twit.setText(text);
			twit.setTimestamp(res.get("timestamp").getS());
			twit.setUrl(res.get("url").getS());
			String username = res.get("username").getS();
			twit.setSentiment(res.get("sentiment").getS());
			twit.setUsername(username);
			twitList.add(twit);
		}

		return twitList;
	}

	public ArrayList<Twitter> getAllData() {
		String tableName = "twitterTable";
		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
		/*
		 * Condition condition = new Condition()
		 * .withComparisonOperator(ComparisonOperator.GT.toString())
		 * .withAttributeValueList(new AttributeValue().withN("1985"));
		 * scanFilter.put("year", condition);
		 */
		ScanRequest scanRequest = new ScanRequest(tableName)
				.withScanFilter(scanFilter);
		ScanResult scanResult = dynamoDB.scan(scanRequest);
		ArrayList<Twitter> twitList = new ArrayList<Twitter>();
		List<Map<String, AttributeValue>> resultList = scanResult.getItems();
		for (Map<String, AttributeValue> res : resultList) {
			Twitter twit = new Twitter();
			twit.setId(res.get("id").getN());
			twit.setKeyword(res.get("keyword").getS());
			twit.setLatitude(res.get("latitude").getS());
			twit.setLongtitude(res.get("longtitude").getS());
			String text = res.get("text").getS();
			twit.setText(text);
			twit.setTimestamp(res.get("timestamp").getS());
			twit.setUrl(res.get("url").getS());
			twit.setSentiment(res.get("sentiment").getS());
			String username = res.get("username").getS();
			twit.setUsername(username);
			twitList.add(twit);
		}

		return twitList;
	}
}
