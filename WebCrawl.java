//package com.company;
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
    public static HttpURLConnection createConnection (String strURL) throws IOException {
        URL url = new URL(strURL); //make url object from string got from console input
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        return connection;
    }

    //checks url connection returns false if status code is >= 300 true otherwise
//    public static String check ( HttpURLConnection connection) throws IOException {
//        int statusCode = connection.getResponseCode();
//
//        if (statusCode >= 300) {
//            return false;
//        }
//        return true;
//    }
    //finds the href in html body of url passed to method by creating BufferedReader
    //if href is found then create connection, checks code and calls itself
    public static int findHrefInURL ( HttpURLConnection connection, int urlHopsLeft,
                                      ArrayList <String> visitedURLs) throws IOException{
        if (urlHopsLeft == 0) {
            return 0;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        System.out.println("buffer reader");
        String line;
        String substring;
        int locationOfHref;
        String nextURL = "";
        boolean matchFound;
        String delims = "\"";
        int statusCode;
        boolean badURL = false;
        HttpURLConnection nextURLConnection = null;
        System.out.println("after next connection made null");
        String firstPartOfNextURL = "";
        while ((line = reader.readLine()) != null) {
            nextURLConnection = null;
            Pattern pattern = Pattern.compile("a href", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            matchFound = matcher.find();
            if (matchFound) {
                //have a method for this to repeat
                locationOfHref = line.indexOf("href");
                substring = line.substring(locationOfHref);
                System.out.println("found match substring is: " + substring);
                String[] tokens = substring.split(delims, 0);
                if (tokens.length > 1) {
                    nextURL = tokens[1];
                } else {
                    continue;
                }
                if (nextURL.length() > 5) {
                    firstPartOfNextURL = nextURL.substring(0, 4);
                } else {
                    continue;
                }
                if (firstPartOfNextURL.equalsIgnoreCase("http")) {
                    //check if string starts with http

                    nextURLConnection = createConnection(nextURL);
                    statusCode = nextURLConnection.getResponseCode();
                    if (statusCode >= 400) {
                        continue;
                    }
                    System.out.println("before 300 while loop");

                    while (statusCode >= 300) {
                        if (statusCode >= 300 && statusCode < 400) {
                            nextURL = nextURLConnection.getHeaderField("Location");
                            firstPartOfNextURL = nextURL.substring(0, 4);
                            if (!firstPartOfNextURL.equalsIgnoreCase("http")) {
                                badURL = true;
                                break;
                            } else {
                                nextURLConnection = createConnection(nextURL);
                                statusCode = nextURLConnection.getResponseCode();
                            }
                        } else {
                            badURL = true;
                            break;
                        }
                    }
                    if (badURL) {
                        badURL = false;
                        continue;
                    }
                } else {
                    continue;
                }
                //check if url is http and not >400

                //check if url is in the visitedURLS
                System.out.println("change url to visit, next URL: " + nextURL);

                nextURL = changeToHTTPS(nextURL);
                boolean visitedBefore = false;
                for (String s : visitedURLs) {
                    if (s.equalsIgnoreCase(nextURL)) {
                        visitedBefore = true;
                        break;
                    }
                }
                //check if url is http, if so then change to https

                if (!visitedBefore) {
                    visitedURLs.add(nextURL);
                    System.out.println(nextURL + " hops left:" + urlHopsLeft);
                    if (nextURLConnection != null) {
                        urlHopsLeft = findHrefInURL(nextURLConnection, --urlHopsLeft, visitedURLs);
                    }
                    if (urlHopsLeft == 0) {
                        return 0;
                    }
                }
            }
        }
        return urlHopsLeft;
    }

    public static String changeToHTTPS(String currentURL) {
        String firstFiveCharsOfURL = currentURL.substring(0, 5);
        String firstFourCharsOfURL = currentURL.substring(0, 4);
        String updatedURLToHTTPS = currentURL;
        String https = "https";
        if (!firstFiveCharsOfURL.equalsIgnoreCase("https")) {
            if (firstFourCharsOfURL.equalsIgnoreCase("http")) {
                String removedHTTP = currentURL.substring(4);
                updatedURLToHTTPS = https + removedHTTP;
            }
            else {
                return null;
            }
        }
//        char lastCharOfURL = updatedURLToHTTPS.charAt(updatedURLToHTTPS.length()-1);
//
//        if (lastCharOfURL != '/') {
//            updatedURLToHTTPS += "/";
//        }
        return updatedURLToHTTPS;
    }
    public static void main(String[] args) throws IOException  {
        if (args.length < 2) {
            System.out.println("Please enter url and number of hops");
            System.exit(0);
        }
        String strURL = args[0];
        System.out.println(strURL);
        int numberURLHops = Integer.parseInt(args[1]);
        HttpURLConnection connection = createConnection(strURL);
        System.out.println("after connection");
        int statusCode = connection.getResponseCode();
        if (statusCode >= 400) {
            System.out.println("Please enter valid url");
            System.exit(0);
        }
        String firstPartOfURL = strURL.substring(0, 4);
        if (!firstPartOfURL.equalsIgnoreCase("http")) {
            System.out.println("Please enter valid url starting with \"http\"");
            System.exit(0);
        }
        while ( statusCode >= 300) {
            strURL = connection.getHeaderField("Location");
            firstPartOfURL = strURL.substring(0, 4);

            if (!firstPartOfURL.equalsIgnoreCase("http")) {
                System.out.println("Please enter valid url starting with \"http\"");
                System.exit(0);
            }
            connection = createConnection(strURL);
            statusCode = connection.getResponseCode();
            if (statusCode >= 400) {
                System.out.println("Please enter valid url");
                System.exit(0);
            }
        }
        //replace strURL with https
        strURL = changeToHTTPS(strURL);
        ArrayList <String> visitedURLs = new ArrayList<String>();
        visitedURLs.add(strURL);
        System.out.println("passed connection in main");
        numberURLHops = findHrefInURL(connection, --numberURLHops, visitedURLs);
        if (numberURLHops > 0) {
            System.out.println("Ran out of websites to hop to");
        }
        System.out.println("Visted URLs: ");
        int i = 1;
        for (String s : visitedURLs) {
            System.out.println(s + " " + i);
            i++;
        }
    }
}

//https://www.tutorialspoint.com/market/prime-packs/office_productivity
//https://www.tutorialspoint.com/market/teach_with_us.jsp
//directed me to 404^

//http://andreaserio.wordpress.com this webpage



