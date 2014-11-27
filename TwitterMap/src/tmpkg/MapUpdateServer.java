package tmpkg;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
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
    	try {
            // Scan request into a string
            Scanner scanner = new Scanner(request.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            // Parse the JSON message
            InputStream stream = new ByteArrayInputStream(builder.toString().getBytes());
            Map<String, String> message = new ObjectMapper().readValue(stream, Map.class);

            // Confirm the subscription
            if (message.get("Type").equals("SubscriptionConfirmation")) {
                
                new URL(message.get("SubscribeURL")).openStream();
                log.info("Confirmed: " + message.get("TopicArn"));

            } else if (message.get("Type").equals("Notification")) {
                log.info("Received: " + message.get("Message"));
            }
            log.info(builder.toString());

        } catch (Exception e) {
            e.printStackTrace();
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

}
