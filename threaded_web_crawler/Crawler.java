import java.net.*;

/**
 * Makes multiple threads to process URLs rooted at a specified URL
 */

public class Crawler {
    private URLPool pool;
    
    public int numThreads = 4; // Number of threads operating with

    /**
     * Root URL should include protocol (makes checking visited URLs easier)
     */
    public Crawler(String root, int max) throws MalformedURLException {
	pool = new URLPool(max);

	URL rootURL = new URL(root);
	pool.add(new URLDepthPair(rootURL, 0));
    }

    /**
     * Spawns CrawlerTask threads to process URLs
     */
    public void crawl() {
	for (int i = 0; i < numThreads; i++) {
	    CrawlerTask crawler = new CrawlerTask(pool);
	    Thread thread = new Thread(crawler);
	    thread.start();
	}
	// If all URLs are waiting, then crawl() is done
	while (pool.getWaitCount() != numThreads) {
	    try {
		Thread.sleep(500);
	    }
	    catch (InterruptedException e) {
		System.out.println("Ignoring unexpected InterruptedException - " +
				   e.getMessage());
	    }
	}

	pool.printURLs();
    }

    /**
     * Creates a crawler that crawls through links starting from root URL
     */
    public static void main(String[] args) {
	// Print usage if user syntax is wrong
	if (args.length < 2 || args.length > 5) {
	    System.err.println("Usage: java Crawler <URL> <depth> " +
			       "<patience> -t <threads>"); // Optional parameters
	    System.exit(1);
	}

	// Call crawl
	try { 
	    Crawler crawler = new Crawler(args[0], Integer.parseInt(args[1]));
	        
	    switch (args.length) {
	    case 3: 
		CrawlerTask.maxPatience = Integer.parseInt(args[2]);
		break;
	    case 4: 
		crawler.numThreads = Integer.parseInt(args[3]);
		break;
	    case 5: 
		CrawlerTask.maxPatience = Integer.parseInt(args[2]);
		crawler.numThreads = Integer.parseInt(args[4]);
		break;
	    }
	    crawler.crawl();
	}
	catch (MalformedURLException e) {
	    System.err.println("Error: The URL " + args[0] + " is not valid");
	    System.exit(1);
	}
	System.exit(0);
    }
}
