package io.github.andyradionov.tasksedge.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Andrey Radionov
 */

public class QuoterFetcher {
    private static final String TAG = QuoterFetcher.class.getSimpleName();
    private static final String API_URL = "https://api.forismatic.com/api/1.0/?method=getQuote&lang=en&format=json";

    private QuoterFetcher() {
    }

    public static String fetchQuote() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            JSONObject responseJson = new JSONObject(responseString);
            return responseJson.getString("quoteText");
        } catch (Exception e) {
            Log.d(TAG, "Error fetching quote");
            return null;
        }
    }
}
