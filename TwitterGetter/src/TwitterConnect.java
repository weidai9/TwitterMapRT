import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConnect {
   //protected static DBhelper twitterDBhelper = new DBhelper(); 
    protected static MatcherHelper matcherHelper = new MatcherHelper();
    protected static AwsSqshelper mySQShelper = new AwsSqshelper();
    //private static long count = 0;
	public static void main(String[] args) {
	  //count = twitterDBhelper.findId("twitterTable") + 1;
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("0VvRimkYWMFp95lcCQB2glZ4a")
		  .setOAuthConsumerSecret("ncvBFIkCcWsdOJWvUSjokNSyoql47kAcu4hiFT0X83dWcv9Ogk")
		  .setOAuthAccessToken("2766934772-iPYNFAvffTQJYdo8vqXHxUMt5c4RqR034DHzqyz")
		  .setOAuthAccessTokenSecret("9zJpoLt1AOdYIOwnm7sb3yk1L4Bk1G4TEMV10QLdxfd4a");
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        //if(!twitterDBhelper.testAndCheck("twitterTable")) {
        //	System.out.println("There is no existing twitterTable,please create one first");
        //	return;
        //}
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                GeoLocation gl = status.getGeoLocation();
                if (gl!=null && status.getUser() != null) {
                      JSONObject jsonObject = new JSONObject();
                      try {
						jsonObject.put("username", status.getUser().getName());
						jsonObject.put("text", status.getText());
						jsonObject.put("timestamp", status.getCreatedAt().toString());
						jsonObject.put("latitude", gl.getLatitude());
						jsonObject.put("longtitude",gl.getLongitude());
						jsonObject.put("keyword",matcherHelper.iskeyword(status.getText()));
						jsonObject.put("url", status.getSource());
                      } catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
                      }
                      mySQShelper.SendMessageToQueue(jsonObject.toString());
                      // count++;
                      //twitterDBhelper.addItem("twitterTable", item);
				}
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();
	}
}