package io.github.andyradionov.tasksedge.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import java.util.Date;

import io.github.andyradionov.tasksedge.R;

/**
 * @author Andrey Radionov
 */

public class QuoteFetcherIntentService extends IntentService {

    public QuoteFetcherIntentService() {
        super(QuoteFetcherIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String quote = QuoterFetcher.fetchQuote();
        if (quote == null) return;
        String quoteKey = getString(R.string.pref_quote_key);
        String quoteDateKey = getString(R.string.pref_quote_date_key);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(quoteKey, quote);
        editor.putLong(quoteDateKey, new Date().getTime());
        editor.apply();
    }


}
