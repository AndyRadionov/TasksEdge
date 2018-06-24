package io.github.andyradionov.tasksedge.network;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * @author Andrey Radionov
 */

public class QuoteFirebaseJobService extends JobService {

    AsyncTask<Void, Void, Void> mQuoteBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mQuoteBackgroundTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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
        if (mQuoteBackgroundTask != null) {
            mQuoteBackgroundTask.cancel(true);
        }
        return true;
    }
}
