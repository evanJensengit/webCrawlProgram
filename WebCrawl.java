//package com.company;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList; // import just the List interface

public class WebCrawl {
    public static HttpURLConnection createConnection (String strURL) throws IOException {
        URL url = new URL(strURL); //make url object from string got from console input
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        return connection;
    }

    //finds the href in html body of url passed to method by creating BufferedReader
    //if href is found then create connection, checks code and calls itself
    public static int findHrefInURL ( HttpURLConnection connection, int urlHopsLeft,
                                      ArrayList <String> visitedURLs) throws IOException{
        if (urlHopsLeft == 0) {
            return 0;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        String substring;
        int locationOfHref;
        String nextURL = "";
        boolean matchFound;
        String delims = "\"";
        int statusCode;
        boolean badURL = false;
        HttpURLConnection nextURLConnection = null;
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

                String[] tokens = substring.split(delims, 0);
                if (tokens.length > 1) {
                    nextURL = tokens[1];
                } else {
                    continue;
                }

                if (!(nextURL.length() > 8)) {
                    continue;
                }
                if (checkURLHTTPCorrect(nextURL)) {
                    //check if string starts with http
                    nextURLConnection = createConnection(nextURL);
                    statusCode = nextURLConnection.getResponseCode();
                    if (statusCode >= 400) {
                        continue;
                    }
                    //follow redirect until status code <300 or >400
                    while (statusCode >= 300) {
                        if (statusCode >= 300 && statusCode < 400) {
                            nextURL = nextURLConnection.getHeaderField("Location");
                            if (!checkURLHTTPCorrect(nextURL)) {
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

                //check if url is in the visitedURLS
                boolean visitedBefore = checkVariationsOfURL(nextURL, visitedURLs);


                if (!visitedBefore) {
                    visitedURLs.add(nextURL);
                    System.out.println(nextURL + " Hops left: " + urlHopsLeft);
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

    //returns true if has been visited before false otherwise
    public static boolean checkVariationsOfURL(String currentURL, ArrayList<String> visited) {
        boolean hasBackSlash = false;
        boolean hasHTTPS = false;
        String http = "http";
        String https = "https";
        char lastCharOfURL = currentURL.charAt(currentURL.length()-1);
        String firstFive = currentURL.substring(0, 5);
        String HTTPURLWithBackSlash;
        String HTTPSURLWithBackSlash;
        String HTTPURLWithoutBackSlash;
        String HTTPSURLWithoutBackSlash;
        if (firstFive.equalsIgnoreCase(https)) {
            hasHTTPS = true;
        }
        if (lastCharOfURL =='/') {
            hasBackSlash = true;
        }
        if (hasHTTPS && hasBackSlash) {
            HTTPSURLWithBackSlash = currentURL;
            HTTPSURLWithoutBackSlash = currentURL.substring(0, currentURL.length()-1);
            HTTPURLWithBackSlash = http + currentURL.substring(5);
            HTTPURLWithoutBackSlash = http + HTTPSURLWithoutBackSlash.substring(5);
        } else if  (!hasHTTPS && !hasBackSlash) {
            HTTPURLWithoutBackSlash = currentURL;
            HTTPURLWithBackSlash = currentURL + "/";
            HTTPSURLWithoutBackSlash = https + currentURL.substring(4);
            HTTPSURLWithBackSlash = https + HTTPURLWithBackSlash.substring(4);
        }
        else if (!hasHTTPS && hasBackSlash) {
            HTTPURLWithBackSlash = currentURL;
            HTTPURLWithoutBackSlash = currentURL.substring(0, currentURL.length()-1);
            HTTPSURLWithoutBackSlash = https + HTTPURLWithoutBackSlash.substring(4);
            HTTPSURLWithBackSlash = https + currentURL.substring(4);
        }
        else if (hasHTTPS && !hasBackSlash) {
            HTTPSURLWithoutBackSlash = currentURL;
            HTTPSURLWithBackSlash = currentURL + "/";
            HTTPURLWithBackSlash = http + HTTPSURLWithBackSlash.substring(5);
            HTTPURLWithoutBackSlash = http + currentURL.substring(5);
        }
        else {
            return false;
        }

        for (String s : visited) {
            if (HTTPSURLWithBackSlash.equalsIgnoreCase(s)) {
                return true;
            }
            else if (HTTPSURLWithoutBackSlash.equalsIgnoreCase(s)) {
                return true;
            }
            else if (HTTPURLWithBackSlash.equalsIgnoreCase(s)) {
                return true;
            }
            else if (HTTPURLWithoutBackSlash.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    //checks url if it is in the format of https:// or http://
    public static boolean checkURLHTTPCorrect( String url) {
        String http = "http";
        String https = "https";
        String httpsStart = "https://";
        String httpStart = "http://";

        String firstFourCharsOfURL = url.substring(0, 4);
        String firstFiveCharsOfURL = url.substring(0, 5);
        String firstSevenCharsOfURL = url.substring(0, 7);
        String firstEightCharsOfURL = url.substring(0, 8);

        if (firstFiveCharsOfURL.equalsIgnoreCase(https)) {
            return firstEightCharsOfURL.equalsIgnoreCase(httpsStart);
        }
        if (firstFourCharsOfURL.equalsIgnoreCase(http)) {
            return firstSevenCharsOfURL.equalsIgnoreCase(httpStart);
        }
        return false;
    }

    public static void main(String[] args) throws IOException  {
        if (args.length < 2) {
            System.out.println("Please enter url and number of hops");
            System.exit(0);
        }
        String strURL = args[0];
        int numberURLHops = Integer.parseInt(args[1]);
        if (numberURLHops <= 0) {
            System.exit(0);
        }
        if (!checkURLHTTPCorrect(strURL)) {
            System.out.println("Please enter valid url");
            System.exit(0);
        }

        HttpURLConnection connection = createConnection(strURL);
        int statusCode = connection.getResponseCode();

        if (statusCode >= 400) {
            System.out.println("Please enter valid url");
            System.exit(0);
        }

        while ( statusCode >= 300) {
            strURL = connection.getHeaderField("Location");

            if (!checkURLHTTPCorrect(strURL)) {
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

        ArrayList <String> visitedURLs = new ArrayList<String>();
        visitedURLs.add(strURL);
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



