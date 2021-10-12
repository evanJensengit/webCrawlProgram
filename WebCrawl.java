import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList; // import just the List interface
import java.util.List; // import just the List interface
import java.util.*;

public class WebCrawl {
    public static HttpURLConnection createConnection (String strURL) {
        URL url = new URL(strURL); //make url object from string got from console input
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        return connection;
    }

    //checks url connection returns false if status code is >= 300 true otherwise
    public static String check ( HttpURLConnection connection) {
       int statusCode = connection.getResponseCode();

       if (statusCode >= 300) {
           return false;
       }
       return true;
   }
   //finds the href in html body of url passed to method by creating BufferedReader
    //if href is found then create connection, checks code and calls itself
   public static int findHrefInURL ( HttpURLConnection connection, int urlHopsLeft,
                                     ArrayList <string> visitedURLs) {
       if (urlHopsLeft == 0) {
           return 0;
       }
       BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

       String line;
       String substring;
       int locationOfHref;
       String nextURL;
       boolean matchFound;
       String delims = "\"";
       int statusCode;
       boolean badURL = false;
       while ((line = reader.readLine()) != null) {
           Pattern pattern = Pattern.compile("a href", Pattern.CASE_INSENSITIVE);
           Matcher matcher = pattern.matcher(line);
           matchFound = matcher.find();
           if (matchFound) {
               locationOfHref = line.indexOf("href");
               String substring = line.substring(locationOfHref);
               String[] tokens = substring.split(delims, 0);

               String nextURL = tokens[1];
               System.out.println(newURL);
               String firstPartOfNextURL = nextURL.substring(0, 4);
               if (firstPartOfNextURL.equalsIgnoreCase("http")) {
                   //check if string starts with http
                   HttpURLConnection nextURLConnection = createConnection(nextURL);
                   statusCode = nextURLConnection.getResponseCode();

                   if (statusCode != HttpURLConnection.HTTP_OK) {
                       if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                               || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                               || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
                           nextURL = nextURLConnection.getHeaderField("Location");
                           firstPartOfNextURL = nextURL.substring(0, 4);
                           if (!firstPartOfNextURL.equalsIgnoreCase("http")) {
                               badURL = true;
                           } else {
                               nextURLConnection = createConnection(nextURL, numberURLHops)
                           }
                       }

                   } else if (statusCode >= 400) {
                       badURL = true;

                   }

               }
               //check if url is http and not >400
               if (!badURL) {
                   visitedURLs.add(nextURL)
                   urlHopsLeft = findHrefInURL(nextURLConnection, --urlHopsLeft);

                   if (urlHopsLeft == 0) {
                       return 0;
                   }
               }
               //reset for next line of html text
               badURL = false;
           }
       }
   }

    public static void main(String[] args) throws IOException  {
        if (args.length < 2) {
            System.out.println("Please enter url and number of hops");
            System.exit(0);
        }
        String strURL = args[0];
        int numberURLHops = Integer.parseInt(args[1]);
        HttpURLConnection connection = createConnection(strURL, numberURLHops);

        String firstPartOfURL = strURL.substring(0, 4);
        if (!firstPartOfURL.equalsIgnoreCase("http")) {
            System.out.println("Please enter valid url starting with \"http\"");
            System.exit(0);
        }
        int statusCode = connection.getResponseCode();

        if (statusCode >= 300 && statusCode < 400) {
            strURL = connection.getHeaderField("Location");
            firstPartOfURL = strURL.substring(0, 4);
            if (!firstPartOfURL.equalsIgnoreCase("http")) {
                System.out.println("Please enter valid url starting with \"http\"");
                System.exit(0);
            }
            connection = createConnection(strURL, numberURLHops)
        }
        else if (statusCode > 400 ) {
            System.out.println("Please enter valid url");
            System.exit(0);
        }

        ArrayList <String> visitedURLs = new ArrayList<String>();
        visitedURLs.add(strURL)

        findHrefInURL(connection, --numberURLHops, visitedURLs);

    }
}