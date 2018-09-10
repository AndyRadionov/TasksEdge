package io.github.andyradionov.tasksedge.data.network;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.lang.ref.WeakReference;

/**
 * @author Andrey Radionov
 */

public class QuoteFirebaseJobService extends JobService {
    private static final String TAG = QuoteFirebaseJobService.class.getSimpleName();
    private AsyncTask<Void, Void, Void> mQuoteBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG, "onStartJob");
        mQuoteBackgroundTask = new QuoteAsyncTask(this, jobParameters);
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

    private static class QuoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<QuoteFirebaseJobService> firebaseJobService;
        private final JobParameters jobParameters;

        private QuoteAsyncTask(QuoteFirebaseJobService jobService, JobParameters jobParameters) {
            this.firebaseJobService = new WeakReference<>(jobService);
            this.jobParameters = jobParameters;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground");
            if (firebaseJobService.get() != null) {
                JobService jobService = firebaseJobService.get();
                QuoteFetcherUtils.updateQuote(jobService);
                jobService.jobFinished(jobParameters, true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (firebaseJobService.get() != null) {
                JobService jobService = firebaseJobService.get();
                jobService.jobFinished(jobParameters, true);
            }
        }
    }
}
