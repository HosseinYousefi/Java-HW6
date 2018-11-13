import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class WebCrawlerTest extends TestCase {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimpleWebpageWithOneThread() {
        ArrayList<String> urls = new ArrayList<>();
        urls.add("https://raw.githubusercontent.com/HosseinYousefi/Java-HW6/master/test.html");
        try {
            WebCrawler webCrawler = new WebCrawler(urls, 1);
            webCrawler.crawl();
        } catch (Exception e) {
            fail();
        }
    }
}