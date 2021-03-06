package io.github.andyradionov.tasksedge.data.network;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.github.andyradionov.tasksedge.R;
import io.github.andyradionov.tasksedge.utils.PreferenceUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Andrey Radionov
 */

public class QuoteFetcherUtils {
    private static final String TAG = QuoteFetcherUtils.class.getSimpleName();
    private static final String API_URL = "https://api.forismatic.com/api/1.0/?method=getQuote&lang=en&format=json";
    private static final String QUOTE_UPDATE_TAG = "quote_update";

    private static final int UPDATE_INTERVAL_HOURS = 24;
    private static final int UPDATE_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(UPDATE_INTERVAL_HOURS);
    private static final int UPDATE_FLEXTIME_SECONDS = UPDATE_INTERVAL_SECONDS / 24;

    private static boolean sInitialized;

    private QuoteFetcherUtils() {
    }

    public static synchronized void scheduleUpdate(Context context) {
        if (sInitialized) return;
        Log.d(TAG, "scheduleUpdate");

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job updateQuoteJob = dispatcher.newJobBuilder()
                .setService(QuoteFirebaseJobService.class)
                .setTag(QUOTE_UPDATE_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        UPDATE_INTERVAL_SECONDS,
                        UPDATE_INTERVAL_SECONDS + UPDATE_FLEXTIME_SECONDS))
                .setReplaceCurrent(false)
                .build();

        dispatcher.schedule(updateQuoteJob);
        sInitialized = true;
    }

    public static void updateQuote(Context context) {
        Log.d(TAG, "updateQuote");
        String quote = fetchQuote(context);

        PreferenceUtils.saveQuote(context, quote);
    }

    private static String fetchQuote(Context context) {
        Log.d(TAG, "fetchQuote");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            JSONObject responseJson = new JSONObject(responseString);
            return responseJson.getString(context.getString(R.string.quote_key));
        } catch (Exception e) {
            Log.d(TAG, context.getString(R.string.network_error));
            return null;
        }
    }
}
