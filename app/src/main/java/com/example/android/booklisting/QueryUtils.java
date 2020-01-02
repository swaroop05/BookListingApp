package com.example.android.booklisting;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by meets on 12/8/2017.
 */

public final class QueryUtils {

    public static String LOG_TAG = QueryUtils.class.getName();
    public static String mJsonResponse = null;
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int SUCCESS_RESPONSE_CODE = 200;
    private static final String KEY_ITEMS = "items";
    private static final String KEY_VOLUME_INFO = "volumeInfo";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_PUBLISHERS = "publisher";
    private static final String KEY_TITLE = "title";
    private static final String KEY_INFO_LINK = "infoLink";
    public QueryUtils() {

    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == SUCCESS_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                mJsonResponse = jsonResponse;
            } else {
                Log.e(LOG_TAG, "Response code was not " + SUCCESS_RESPONSE_CODE + ". It was " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException was thrown in makeHttpRequest method", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            //InputStreamReader handles the translation process from the raw data to human readable characters.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //InputStreamReader only allows to read single character at a time. This can be avoided if we wrap InputStreamReader around BufferedReader.
            //BufferedReader will accept reading in character and will read and save larger chunk of data. So when programe requies to read another character
            //BufferedReader will have it stored already which helps us to avoid reading each character from InputStreamReader.
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link BookInfo} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<BookInfo> extractBooksInfo(String jsonResponse) {
        String authors;
        // Create an empty ArrayList that we can start adding BookInfo to
        ArrayList<BookInfo> bookInfos = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            if (jsonResponse != null) {
                JSONObject reader = new JSONObject(jsonResponse);
                JSONArray featuresArray = reader.optJSONArray(KEY_ITEMS);
                if (featuresArray != null) {
                    int arrayCount = featuresArray.length();
                    for (int i = 0; i < arrayCount; i++) {
                        JSONObject volumeInfo = featuresArray.getJSONObject(i).optJSONObject(KEY_VOLUME_INFO);
                        if (volumeInfo != null) {
                            JSONArray authorsArray = volumeInfo.optJSONArray(KEY_AUTHORS);
                            if (authorsArray == null) {
                                String publisher = volumeInfo.optString(KEY_PUBLISHERS);
                                if (publisher != "") {
                                    authors = "Publisher: " + publisher;
                                } else {
                                    authors = "No Authors found" ;
                                }

                            } else {
                                authors = "Author[s]: " + authorsArray.getString(0);
                                if (authorsArray.length() > 1) {
                                    for (int j = 1; j < authorsArray.length(); j++) {
                                        authors = authors + ", ";
                                        authors = authors + authorsArray.getString(j);
                                    }
                                }
                            }

                            String title = "Title: " + volumeInfo.getString(KEY_TITLE);
                            String url = volumeInfo.getString(KEY_INFO_LINK);
                            bookInfos.add(new BookInfo(authors, title, url));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the BookInfo JSON results", e);
        }

        // Return the list of bookInfos
        return bookInfos;
    }

    /**
     * Core method used to fetch BookInfo as List which users all helper methods to make URL object
     * Http request, read from Input stream, Parse JSON
     */
    public static List<BookInfo> fetchBookInfos(String stringUrl) {

        // Create URL object
        URL url = createUrl(stringUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = QueryUtils.makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HttpRequest", e);
        }
        List<BookInfo> bookInfos = extractBooksInfo(jsonResponse);
        return bookInfos;
    }
}
