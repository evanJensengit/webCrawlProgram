import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WebCrawl {

    public static void main(String[] args) throws IOException  {

        if (args.length == 0 || args.length < 2) {
            System.out.println("Please enter url and number of hops");
            System.exit(0);
        }
        String strURL = args[0];
        int numberURLHops = Integer.parseInt(args[1]);
        URL url = new URL(strURL); //make url object from string got from console input
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //open connection to url that was input
        //in console
        int statusCode = connection.getResponseCode();
        System.out.println(statusCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null){
            System.out.println(line);
        }
    }
}