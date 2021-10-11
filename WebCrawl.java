import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WebCrawl {
    //checks url connection returns false if status code is >= 300 true otherwise
    public static boolean checkURLCode ( HttpURLConnection connection) {
       int statusCode = connection.getResponseCode();

       if (statusCode >= 300) {
           return false;
       }
       return true;
   }
   //finds the href in html body of url passed to method by creating BufferedReader
    //if href is found then create connection, checks code and calls itself
   public static int findHrefInURL ( HttpURLConnection connection, int urlHopsLeft) {
       if (urlHopsLeft == 0) {
           return 0;
       }
       BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

       String line;
       String substring;
       int locationOfHref;
       String nextURL;
       String delims = " \" ";
       while ((line = reader.readLine()) != null){
           locationOfHref = line.indexOf("href");
           substring = line.substring(locationOfHref);
           List <String> tokens = substring.split(delims);
           nextURL = tokens[0];
           //check if string starts with http
           HttpURLConnection nextURLConnection = createConnection(nextURL);
           boolean codeOk = checkURLCode(nextURLConnection);

           if (codeOk) {
               urlHopsLeft = findHrefInURL(nextURLConnection, urlHopsLeft-1 );

               if (urlHopsLeft == 0) {
                   return 0;
               }
           }

       }
   }

   public static HttpURLConnection createConnection (String strURL) {
       URL url = new URL(strURL); //make url object from string got from console input
       HttpURLConnection connection = (HttpURLConnection) url.openConnection();

       return connection;
   }
   public static
    public static void main(String[] args) throws IOException  {

        if (args.length == 0 || args.length < 2) {
            System.out.println("Please enter url and number of hops");
            System.exit(0);
        }
        String strURL = args[0];
        int numberURLHops = Integer.parseInt(args[1]);
        HttpURLConnection connection = createConnection(strURL, numberURLHops)

        boolean codeOk = checkURLCode(connection);
        System.out.println(statusCode);

        if (!codeOk) {
            System.out.println("Please enter valid url");
            System.exit(0);
        }
        findHrefInURL(connection, numberURLHops);
        
    }
}