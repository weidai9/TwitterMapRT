package tmpkg;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint(value="/markerupdate", encoders = {TwitterEncoder.class})
public class MapUpdateServer extends HttpServlet{
	private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getAnonymousLogger();
	private static Set<Session> sessionslist = Collections.synchronizedSet(new HashSet<Session>());
	@OnOpen
    public void onOpen(Session session){
        sessionslist.add(session);
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Message from " + session.getId() + ": " + message);
        
        DataHelper dh = new DataHelper();
        int intid = 1 + (int)(Math.random()*500); 
        ArrayList<Twitter> list = dh.getDataById(""+intid);
        if(list.size()>0){
        	Twitter t = list.get(0);
        	TwitterJson tj = new TwitterJson(Json.createObjectBuilder()
        			.add("latitude",t.getLatitude())
        			.add("longtitude", t.getLongtitude())
        			.add("text", t.getText())
        			.add("username", t.getUsername())
        			.add("timestamp", t.getTimestamp())
        			.add("sentiment", t.getSentiment())
        			.build());
        	broadcastData(tj);
        }
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session){
        sessionslist.remove(session);
    }
    
    public void broadcastData(TwitterJson tj) {
    	for (Session s : sessionslist){
                try {
					s.getBasicRemote().sendObject(tj);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (EncodeException e) {
					e.printStackTrace();
				}
    	}
    }
    
    @Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
    	//Get the message type header.
    			String messagetype = request.getHeader("x-amz-sns-message-type");
    			//If message doesn't have the message type header, don't process it.
    			if (messagetype == null)
    				return;

    			// Parse the JSON message in the message body
    			// and hydrate a Message object with its contents 
    			// so that we have easy access to the name/value pairs 
    			// from the JSON message.
    			Scanner scan = new Scanner(request.getInputStream());
    			StringBuilder builder = new StringBuilder();
    			while (scan.hasNextLine()) {
    				builder.append(scan.nextLine());
    			}

    			SNSMessage msg = readMessageFromJson(builder.toString());

    			// The signature is based on SignatureVersion 1. 
    			// If the sig version is something other than 1, 
    			// throw an exception.
    			if (msg.getSignatureVersion().equals("1")) {
    				// Check the signature and throw an exception if the signature verification fails.
    				if (isMessageSignatureValid(msg))
    					System.out.println(">>Signature verification succeeded");
    				else {
    					System.out.println(">>Signature verification failed");
    					throw new SecurityException("Signature verification failed.");
    				}
    			}
    			else {
    				System.out.println(">>Unexpected signature version. Unable to verify signature.");
    				throw new SecurityException("Unexpected signature version. Unable to verify signature.");
    			}
    			
    			// Process the message based on type.
    			if (messagetype.equals("Notification")) {
    				//TODO: Do something with the Message and Subject.
    				//Just log the subject (if it exists) and the message.
    				String logMsgAndSubject = ">>Notification received from topic " + msg.getTopicArn();
    				if (msg.getSubject() != null)
    					logMsgAndSubject += " Subject: " + msg.getSubject();
    				logMsgAndSubject += " Message: " + msg.getMessage();
    				String mes = msg.getMessage();
    				DataHelper dh = new DataHelper();
    		        ArrayList<Twitter> list = dh.getDataById(mes);
    		        if(list.size()>0){
    		        	Twitter t = list.get(0);
    		        	TwitterJson tj = new TwitterJson(Json.createObjectBuilder()
    		        			.add("latitude",t.getLatitude())
    		        			.add("longtitude", t.getLongtitude())
    		        			.add("text", t.getText())
    		        			.add("username", t.getUsername())
    		        			.add("timestamp", t.getTimestamp())
    		        			.add("sentiment", t.getSentiment())
    		        			.build());
    		        	broadcastData(tj);
    		        }
    				System.out.println(logMsgAndSubject);
    					
    				}
    			else if (messagetype.equals("SubscriptionConfirmation"))
    			{
    				Scanner sc = new Scanner(new URL(msg.getSubscribeURL()).openStream());
    				StringBuilder sb = new StringBuilder();
    				while (sc.hasNextLine()) {
    					sb.append(sc.nextLine());
    				}
    				System.out.println(">>Subscription confirmation (" + msg.getSubscribeURL() +") Return value: " + sb.toString());
    				//TODO: Process the return value to ensure the endpoint is subscribed.
    				SNSHelper.INSTANCE.confirmTopicSubmission(msg);
    			}
    			else if (messagetype.equals("UnsubscribeConfirmation")) {
    				System.out.println(">>Unsubscribe confirmation: " + msg.getMessage());
    			}
    			else {
    				//TODO: Handle unknown message type.
    				System.out.println(">>Unknown message type.");
    			}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print("Come on, I am another type of server. You are trying to access this uri in a wrong way.");
	}
	
	private boolean isMessageSignatureValid(SNSMessage msg) {

		try {
			URL url = new URL(msg.getSigningCertUrl());
			InputStream inStream = url.openStream();
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
			inStream.close();

			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(cert.getPublicKey());
			sig.update(getMessageBytesToSign(msg));
			return sig.verify(Base64.decodeBase64(msg.getSignature().getBytes()));
		}
		catch (Exception e) {
			throw new SecurityException("Verify method failed.", e);

		}
	}

	private byte[] getMessageBytesToSign(SNSMessage msg) {

		byte [] bytesToSign = null;
		if (msg.getType().equals("Notification"))
			bytesToSign = buildNotificationStringToSign(msg).getBytes();
		else if (msg.getType().equals("SubscriptionConfirmation") || msg.getType().equals("UnsubscribeConfirmation"))
			bytesToSign = buildSubscriptionStringToSign(msg).getBytes();
		return bytesToSign;
	}

	//Build the string to sign for Notification messages.
	private static String buildNotificationStringToSign( SNSMessage msg) {
		String stringToSign = null;

		//Build the string to sign from the values in the message.
		//Name and values separated by newline characters
		//The name value pairs are sorted by name 
		//in byte sort order.
		stringToSign = "Message\n";
		stringToSign += msg.getMessage() + "\n";
		stringToSign += "MessageId\n";
		stringToSign += msg.getMessageId() + "\n";
		if (msg.getSubject() != null) {
			stringToSign += "Subject\n";
			stringToSign += msg.getSubject() + "\n";
		}
		stringToSign += "Timestamp\n";
		stringToSign += msg.getTimestamp() + "\n";
		stringToSign += "TopicArn\n";
		stringToSign += msg.getTopicArn() + "\n";
		stringToSign += "Type\n";
		stringToSign += msg.getType() + "\n";
		return stringToSign;
	}

	//Build the string to sign for SubscriptionConfirmation 
	//and UnsubscribeConfirmation messages.
	private static String buildSubscriptionStringToSign(SNSMessage msg) {
		String stringToSign = null;
		//Build the string to sign from the values in the message.
		//Name and values separated by newline characters
		//The name value pairs are sorted by name 
		//in byte sort order.
		stringToSign = "Message\n";
		stringToSign += msg.getMessage() + "\n";
		stringToSign += "MessageId\n";
		stringToSign += msg.getMessageId() + "\n";
		stringToSign += "SubscribeURL\n";
		stringToSign += msg.getSubscribeURL() + "\n";
		stringToSign += "Timestamp\n";
		stringToSign += msg.getTimestamp() + "\n";
		stringToSign += "Token\n";
		stringToSign += msg.getToken() + "\n";
		stringToSign += "TopicArn\n";
		stringToSign += msg.getTopicArn() + "\n";
		stringToSign += "Type\n";
		stringToSign += msg.getType() + "\n";
		return stringToSign;
	}


	private SNSMessage readMessageFromJson(String string) {
		ObjectMapper mapper = new ObjectMapper(); 
		SNSMessage message = null;
		try {
			message = mapper.readValue(string, SNSMessage.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return message;
	}
	

}
