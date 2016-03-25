import java.net.*;
import java.util.regex.*;

/**
 * Stores URLs and the depth at which they were explored
 */
public class URLDepthPair {
    // Regular expression for the URL
    public static final String URL_REGEX = "(https?:\\/\\/)((\\w+\\.)+\\.(\\w)+[~:\\S\\/]*)";
    public static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX,  Pattern.CASE_INSENSITIVE);
    
    private URL URL;
    
    private int depth;
    
    public URLDepthPair(URL url, int d) throws MalformedURLException {
	// Assumes input url is an absolute URL
	URL = new URL(url.toString());

	depth = d;
    }
    
    @Override public String toString() {
	return "URL: " + URL.toString() + ", Depth: " + depth;
    }

    /**
     * Returns URL of the pair
     */
    public URL getURL() {
	return URL;
    }
    
    /**
     * Returns the search depth of the URL
     */
    public int getDepth() {
	return depth;
    } 

    /**
     * Returns the server's hostname specified in URL
     */ 
    public String getHost() {
	return URL.getHost();
    }
    
    /**
     * Returns the resource on server
     */
    public String getDocPath() {
	return URL.getPath();
    }
    
    /**
     * Checks if the URL is an absolute URL, not a relative
     */
    public static boolean isAbsolute(String url) {
	Matcher URLChecker = URL_PATTERN.matcher(url);
	if (!URLChecker.find()) {
	    return false;
	}
	return true;
    }
}
