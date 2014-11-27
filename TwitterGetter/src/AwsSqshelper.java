import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AwsSqshelper {
	protected static AmazonSQS awsSqs;
	protected static String myQueue = null;
	public AwsSqshelper() {
		AWSCredentials credentials = null; 
		try {
	         credentials = new ProfileCredentialsProvider("default").getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (/Users/daniel/.aws/credentials), and is in valid format.",
	                    e);
	        }
		 awsSqs = new AmazonSQSClient(credentials);
		 Region region = Region.getRegion(Regions.US_EAST_1);
		 awsSqs.setRegion(region);
		 
		 try {
			 ListQueuesRequest listRequest = new ListQueuesRequest("workerQueue");
			 ListQueuesResult listResult = awsSqs.listQueues(listRequest);
			 List<String> urlQueue = listResult.getQueueUrls();
			 myQueue = urlQueue.get(0);
		 } catch(Exception e) {
			   throw new AmazonClientException("Cannot get the SQS which is named workQueue, please check");
		 }
	}
	public void SendMessageToQueue(String Message) {
		awsSqs.sendMessage(new SendMessageRequest(myQueue, Message));
	}
	public Map<String,AttributeValue> GetMessageFromQueue() {
		ReceiveMessageResult result = awsSqs.receiveMessage(new ReceiveMessageRequest(myQueue).withMaxNumberOfMessages(1).withWaitTimeSeconds(10));
		List<Message> list = result.getMessages();
		String messageReceiptHandle = list.get(0).getReceiptHandle();
		List<String> listString = new ArrayList<String>();
		Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
		for (Message message : list) {
			String myMessageString = message.getBody();
			listString.add(myMessageString);
		}
		JSONObject jsonObject;
		try {
			String parseString = listString.get(0);
			jsonObject = new JSONObject(parseString);
			awsSqs.deleteMessage(new DeleteMessageRequest(myQueue, messageReceiptHandle));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			map.put("username",new AttributeValue().withS(jsonObject.getString("username")));
			map.put("text",new AttributeValue().withS(jsonObject.getString("text")));
			map.put("timpstamp", new AttributeValue().withS(jsonObject.getString("timestamp")));
			map.put("latitude", new AttributeValue().withS(jsonObject.getString("latitude")));
			map.put("longtitude", new AttributeValue().withS(jsonObject.getString("longtitude")));
			map.put("keyword", new AttributeValue().withS(jsonObject.getString("keyword")));
            map.put("url", new AttributeValue().withS(jsonObject.getString("url")));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return map;	
	}
}
