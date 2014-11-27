import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MatcherHelper {
	    
	    public String iskeyword(String text) {
	    	String ret = new String(text);
	    	if(text.contains("Thanksgiving")) {
	    		ret = "Thanksgiving";
	    	} else if(text.contains("Turkey") || text.contains("turkey")) {
	    		ret = "Turkey";
	    	} else if(text.contains("Pie") || text.contains("pie")) {
	    		ret = "Pie";
	    	} else if(text.contains("is")) {
	    		ret = "is";
	    	} else if(text.contains("am")) {
	    		ret = "am";
	    	} else if(text.contains("are")) {
	    		ret = "are";
	    	} else {
	    		ret = "None";
	    	}
			return ret;
		}
}
