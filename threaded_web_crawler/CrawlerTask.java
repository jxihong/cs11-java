import java.io.*;
import java.net.*;
import java.util.regex.*;

/**
 * This class crawls through websites, and adds new links to the pool for 
 * other threads to process
 */

public class CrawlerTask implements Runnable {
    // Regular expression for whole href HTML tag
    public static final String LINK_REGEX = "href\\s*=\\s*\"([^$^\"]*)\"";
    public static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);

    public static int maxPatience = 5; // Length in seconds that socket will wait for server

    private URLPool pool; 

    public CrawlerTask(URLPool p) {
	pool = p;
    }

    /**
     * Creates socket to sent HTTP request to webpage "nextPair"
     */
    public Socket sendRequest(URLDepthPair nextPair) 
	throws UnknownHostException, SocketException, IOException {
	//Create new HTTP socket
	Socket socket = new Socket(nextPair.getHost(), 80);
	socket.setSoTimeout(maxPatience * 1000);

	OutputStream os = socket.getOutputStream();
	PrintWriter writer = new PrintWriter(os, true);

	// Request resource from page's host
	writer.println("GET " + nextPair.getDocPath() + " HTTP/1.1");
	writer.println("Host: " + nextPair.getHost());
	writer.println("Connection: close");
	writer.println(); 

	return socket;
    }

    /**
     * Processed a URL by finding all links, and adding the links to the shared 
     * pool of URLs
     */
    public void processURL(URLDepthPair url) throws IOException { 
	Socket socket;
	try {
	    socket = sendRequest(url);
	}
	catch (UnknownHostException e) {
	    System.err.println("Host "+ url.getHost() + " couldn't be determined"); 
	    return;
	}
	catch (SocketException e) {
	    System.err.println("Error with socket connection: " + url.getURL() + 
			       " - " + e.getMessage());
	    return;
	}
	catch (IOException e) {
	    System.err.println("Couldn't retrieve page at " + url.getURL() +
			       " - " + e.getMessage());
	    return;
	}

	InputStream input = socket.getInputStream();
	BufferedReader reader = new BufferedReader(new InputStreamReader(input)); 
	
	/*
	  URLConnection connection;
	  try {
	      connection = url.getURL().openConnection();
	         
	      }
	      catch (IOException e) {
	          System.err.println("Couldn't open connection: " + url.getURL() +
		         " - " + e.getMessage());
			     return;
			     }
			     
			     InputStream input = connection.getInputStream();
			     BufferedReader reader = new BufferedReader(new InputStreamReader(input)); */
	
	String line;
	while ((line = reader.readLine()) != null) {
	    //System.out.println(line);  // For debugging
	    Matcher LinkFinder = LINK_PATTERN.matcher(line);
	    while (LinkFinder.find()) {
		String newURL = LinkFinder.group(1);
		//System.out.println(newURL); // For debugging

		URL newSite;
		try { 
		    if (URLDepthPair.isAbsolute(newURL)) {
			newSite = new URL(newURL);
		    }
		    else {
			newSite = new URL(url.getURL(), newURL);
		    }
		    //System.out.println(newSite); // For debugging
		    pool.add(new URLDepthPair(newSite, url.getDepth() + 1));
		}
		catch (MalformedURLException e) {
		    System.err.println("Error with URL - " + e.getMessage());
		}
	    }
	}
	reader.close();

	// Close the socket
	try {
	    socket.close();
	}
	catch (IOException e) {
	    System.err.println("Couldn't close connection to " + url.getHost() +
			       " - " + e.getMessage());
        }
    }
    
    /**
     * Process the first URL in the pool
     */ 
    public void run() {
	URLDepthPair nextPair;
	while (true) {
	    nextPair = pool.get();
	    try {
		processURL(nextPair);
	    }
	    catch (IOException e) {
		System.err.println("Error reading the page at " + nextPair.getURL() +
				   " - " + e.getMessage());
	    }
	}
    }
}
