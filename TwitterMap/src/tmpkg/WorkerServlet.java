package tmpkg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
/**
 * An example Amazon Elastic Beanstalk Worker Tier application. This example
 * requires a Java 7 (or higher) compiler.
 */
public class WorkerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    /**
     * A client to use to access Amazon S3. Pulls credentials from the
     * {@code AwsCredentials.properties} file if found on the classpath,
     * otherwise will attempt to obtain credentials based on the IAM
     * Instance Profile associated with the EC2 instance on which it is
     * run.
     */
    private final AmazonS3Client s3 = new AmazonS3Client(
        new AWSCredentialsProviderChain(
            new InstanceProfileCredentialsProvider(),
            new ClasspathPropertiesFileCredentialsProvider()));
   

	/**
     * This method is invoked to handle POST requests from the local
     * SQS daemon when a work item is pulled off of the queue. The
     * body of the request contains the message pulled off the queue.
     */
    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {

        
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print("Hello");
	}
    
}
