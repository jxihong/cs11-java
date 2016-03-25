import java.util.*;

/**
 * Keeps track of URL that need to be processed, as well as URL
 * that have already been seen
 */ 
public class URLPool {
    private int maxDepth;

    // The current number of threads in wait() call
    private int waitCount = 0;

    private LinkedList<URLDepthPair> pendingURLs;

    private LinkedList<URLDepthPair> processedURLs;
    
    private HashSet<String> seenURLs; // Only visit new URLs

    /**
     * Creates a URLPool with given maximum depth
     */
    public URLPool(int max) {
	pendingURLs = new LinkedList<URLDepthPair>();
	processedURLs = new LinkedList<URLDepthPair>();
	seenURLs = new HashSet<String>();
	
	maxDepth = max;
    }

    public synchronized int getWaitCount() {
	return waitCount;
    }

    public synchronized void add(URLDepthPair nextPair) {
	String newURL = nextPair.getURL().toString();

	// Trims any handing "/" from the URL for uniformity
	String trimURL = (newURL.endsWith("/")) ? newURL.substring(0, newURL.length() -1) : newURL;
	if (seenURLs.contains(trimURL)){
	    return;
	}
	seenURLs.add(trimURL);
	
	if (nextPair.getDepth() < maxDepth) {
	    pendingURLs.add(nextPair);
	    notify(); // Notify suspended thread of new URL
	}
	processedURLs.add(nextPair);
    }

    public synchronized URLDepthPair get() {
	// Suspend thread until new URL is added
	while (pendingURLs.size() == 0) {
	    waitCount++;
	    try {
		wait();
	    }
	    catch (InterruptedException e) {
		System.out.println("Ignoring unexpected InterruptedException - " + 
				   e.getMessage());
	    }
	    waitCount--;
	}

	return pendingURLs.removeFirst();
    }

    /**
     * Prints out all processed URLs
     */
    public synchronized void printURLs() {
	System.out.println("\nUnique URLs Found: " + processedURLs.size());
	while (!processedURLs.isEmpty()) {
	    System.out.println(processedURLs.removeFirst());
	}
    }
}