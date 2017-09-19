package com.example.farooqi.notificationscheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.N;

public class NotificationJobServices extends JobService {

    private MyAsyncTask task = null;

    public NotificationJobServices() {
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        task = new MyAsyncTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                jobFinished(jobParameters, false);
                Log.d("Task", "Job Finished");
            }
        };
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (task != null) {
            task.cancel(true);
            Log.d("Task", "asynctask cancelled");
        }
        Log.d("Task", "onStopJob");
        Toast.makeText(this, "Job Failed!", Toast.LENGTH_SHORT).show();
        return true;
    }

}
