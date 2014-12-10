import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;

public class Sentiment {
	private AlchemyAPI alchemy;
	private String key="e66423bd7298f9707fe85f8f26bf0a633c192be1";
    public Sentiment(){
    	alchemy=AlchemyAPI.GetInstanceFromString(key);     
    	
    }
    
    public static void main(String arg[]) throws IOException{
    	Sentiment sm=new Sentiment();
    	String line=null;
		try {
			File myFile=new File("/Users/youhanwang/Desktop/mycontent.txt");
	    	FileReader fr;
			fr = new FileReader(myFile);
			BufferedReader bf=new BufferedReader(fr);
			StringBuilder  stringBuilder = new StringBuilder();
		    String  ls = System.getProperty("line.separator");

		    while((line = bf.readLine() ) != null ) {
		        stringBuilder.append(line);
		        stringBuilder.append(ls);
		    }
		    String text= stringBuilder.toString();
		    System.out.print("What do you concern about?");
		    Scanner scan=new Scanner(System.in);
		    String about=scan.next();
		    sm.judge(text,about);
		   // sm.judgeText(text);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    public String[] judge(String text, String about){
    	String sentiment = null;
    	String score=null;
    	try {
			Document doc=alchemy.TextGetTargetedSentiment(text,about);
			sentiment=doc.getElementsByTagName("type").item(0).getTextContent();
			score=doc.getElementsByTagName("score").item(0).getTextContent();
			/*if(sentiment==null){
				sentiment="neutral";
			}
			if(score==null){
				score="0";
			}
			*/
			System.out.println("The Sentiment about "+about+" is:"+sentiment+"  The score is "+score);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
		System.out.println("No such reference.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
		System.out.println("No such reference.");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
		System.out.println("No such reference.");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
		System.out.println("No such reference.");
		}    	
    	
    	String[] senti=new String[3];
    	senti[0]=about;
    	senti[1]=sentiment;
    	senti[2]=score;
    	/*for(int i=0;i<senti.length;i++){
    		System.out.println(senti[i]);
    	}*/
    	return senti;
    }
}


/*public String judgeText(String text){
	String sentiment = null;
	String score=null;
	String keyword=null;
	try {
		Document doc=alchemy.TextGetRankedKeywords(text);
		keyword=doc.getElementsByTagName("keyword")
		//Document doc=alchemy.TextGetTargetedSentiment(text.);
		//sentiment=doc.getElementsByTagName("type").item(0).getTextContent();
		//score=doc.getElementsByTagName("score").item(0).getTextContent();
		//System.out.println("The Sentiment about"+about+" is:"+sentiment+"  The score is"+score);
		System.out.println(keyword);
	} catch (XPathExpressionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}    	
	
	//return "The Sentiment about"+" is:"+sentiment+"  The score is"+score;
	System.out.println(keyword);
	return keyword;
} 
}
    */