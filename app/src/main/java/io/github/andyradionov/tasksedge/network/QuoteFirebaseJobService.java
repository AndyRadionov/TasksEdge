package io.github.andyradionov.tasksedge.network;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * @author Andrey Radionov
 */

public class QuoteFirebaseJobService extends JobService {
    private static final String TAG = QuoteFirebaseJobService.class.getSimpleName();
    private AsyncTask<Void, Void, Void> mQuoteBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.d(TAG, "onStartJob");
        mQuoteBackgroundTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(TAG, "doInBackground");
                QuoteFetcherUtils.updateQuote(QuoteFirebaseJobService.this);
                jobFinished(job, true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, true);
            }
        };
        mQuoteBackgroundTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "onStopJob");
        if (mQuoteBackgroundTask != null) {
            mQuoteBackgroundTask.cancel(true);
        }
        return true;
    }
}
