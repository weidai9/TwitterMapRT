import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.policy.Condition;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;


public class SNSSender {
	public static AmazonSNSClient snsClient;
	public static AmazonDynamoDBClient dbclient;
	public static String concerntopic="thanksgiving";
	public static String topicArn;
	public static String endpoint="http://www.";
	public static String tablename="twitterTable";
	public static AWSCredentials credentials;
 public SNSSender(){
	 credentials = null; 
		try {
	         credentials = new ProfileCredentialsProvider("default").getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (/Users/daniel/.aws/credentials), and is in valid format.",
	                    e);
	        }
	 dbclient = new AmazonDynamoDBClient(credentials);
	    dbclient.setRegion(Region.getRegion(Regions.US_EAST_1));  
	 snsClient = new AmazonSNSClient(credentials);		 
	 snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));	
	 ListTopicsResult listTopicsResult=snsClient.listTopics();
	 List<Topic> topics=listTopicsResult.getTopics();
	 boolean flag=true;
	 for (Topic topic:topics){
		 if (topic.getTopicArn().toString().contains("TwitMap"))
		 {
			 flag=false;
		 }
	 }	 
	 if(flag){
	 CreateTopicRequest createTopicRequest = new CreateTopicRequest("TwitMap");
	 CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
	 System.out.println("The topic created is:"+createTopicResult);
	 SubscribeRequest subscribeRequest=new SubscribeRequest();
	 topicArn=createTopicResult.getTopicArn();
	 subscribeRequest.withProtocol("http").withTopicArn(topicArn).withEndpoint(endpoint);
	 //subscribeRequest.withProtocol("email").withTopicArn(topicArn).withEndpoint("wilsonwang1119@gmail.com");
	 }
 }

public static void main(String args[]){	
	
	AwsSqshelper awssqshelper=new AwsSqshelper();
	SNSSender snssender=new SNSSender();
	/*if(awssqshelper.GetMessageFromQueue()!=null){
		System.out.println(awssqshelper.GetMessageFromQueue());
	}*/

	snssender.NotificationSender(awssqshelper.GetMessageFromQueue());    
}



public long findId(String tableName) {
	long max = 0;
	ScanResult sResult = dbclient.scan(new ScanRequest(tableName).withAttributesToGet("id"));
	List<Map<String,AttributeValue>> list = sResult.getItems();
	for (Map<String, AttributeValue> map : list) {
		long temp = Long.parseLong(map.get("id").getS());
		if (temp  > max) {
			max = temp;
		}
	}
	return max+1;
}

public void NotificationSender(Map<String, AttributeValue> item){
	String text = item.get("text").toString();
	String ctopic=item.get("keyword").toString();
	//String text=item.values().toString();
	Sentiment sentiment=new Sentiment();
	String[] sentimentcontent=sentiment.judge(text, ctopic);
	if(sentimentcontent[1]==null){
		sentimentcontent[1]="neutral";
	}
	if(sentimentcontent[2]==null){
		sentimentcontent[2]="0";
	}	
	item.put("id", new AttributeValue(Long.toString(findId(tablename))));
	item.put("sentiment", new AttributeValue(sentimentcontent[1]));
	item.put("score", new AttributeValue(sentimentcontent[2]));	
	System.out.print(item);
	dbclient.putItem(tablename, item);
	String msg=sentimentcontent[0];	
	//PublishRequest publishRequest = new PublishRequest(topicArn, msg);
	//PublishResult publishResult = snsClient.publish(publishRequest);
}

}