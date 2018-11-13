import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.util.regex.*;

public class WebCrawler {

    final ExecutorService pool;
    ConcurrentLinkedQueue<String> toVisit;
    ConcurrentSkipListSet<String> visited;

    public static String getContentOfWebPage(URL url) {
        final StringBuilder content = new StringBuilder();

        try (InputStream is = url.openConnection().getInputStream();
             InputStreamReader in = new InputStreamReader(is, "UTF-8");
             BufferedReader br = new BufferedReader(in); ) {
            String inputLine;
            while ((inputLine = br.readLine()) != null)
                content.append(inputLine);
        } catch (IOException e) {
            System.out.println("Failed to retrieve content of " + url.toString());
            e.printStackTrace();
        }

        return content.toString();
    }

    WebCrawler(ArrayList<String> urls, final int threads) {
        toVisit = new ConcurrentLinkedQueue<>();
        visited = new ConcurrentSkipListSet<>();
        pool = Executors.newFixedThreadPool(threads);
        for (String url: urls)
            toVisit.add(url);
    }

    public class Crawler implements Runnable {

        @Override
        public void run() {
            URL url;
            String urlStr;
            synchronized (toVisit) {
                if (toVisit.isEmpty())
                    return;
                try {
                    urlStr = toVisit.remove();
                    url = new URL(urlStr);
                    System.out.println("Visiting " + url);
                    synchronized (visited) {
                        visited.add(urlStr);
                    }
                } catch (Exception e) { return; }
            }
            String str = getContentOfWebPage(url);
            Pattern linkPattern = Pattern.compile(
                    "/^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$/",  Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
            Matcher pageMatcher = linkPattern.matcher(str);
            ArrayList<String> links = new ArrayList<String>();
            while (pageMatcher.find()){
                synchronized (toVisit) {
                    String linkStr = pageMatcher.group();

                    synchronized (visited) {
                        if (!visited.contains(linkStr)) {
                            System.out.println("-- " + linkStr);
                            toVisit.add(linkStr);
                        }
                    }

                }
            }
            System.out.println("Visited: " + visited.toString());
        }
    }

    public void crawl() {
        while (!toVisit.isEmpty()) {
            pool.execute(new Thread(new Crawler()));
        }
    }

    public static void main(String... args) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add("http://google.com");
        WebCrawler webCrawler = new WebCrawler(urls, 4);
        webCrawler.crawl();
    }
}
