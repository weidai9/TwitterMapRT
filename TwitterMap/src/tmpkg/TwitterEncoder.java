package tmpkg;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class TwitterEncoder implements Encoder.Text<TwitterJson> {
	@Override
    public String encode(TwitterJson twit) throws EncodeException {
        return twit.getJson().toString();
    }
 
    @Override
    public void init(EndpointConfig config) {
        System.out.println("Init");
    }
 
    @Override
    public void destroy() {
        System.out.println("destroy");
    }
}
