package com.example.farooqi.notificationscheduler;

/**
 * Created by Farooqi on 9/19/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final int NOTIFICATION_ID = 111;
    private Context context;
    private NotificationManager manager;
    private NotificationCompat.Builder notify;

    public MyAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(1000 * 5);
            showNotification();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
            manager.notify(NOTIFICATION_ID, notify.build());
    }

    private void showNotification() {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        manager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);

        notify = new NotificationCompat.Builder(context)
                .setContentTitle("Job Service")
                .setContentText("Job is running")
                .setSmallIcon(R.drawable.ic_job_running)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
    }
}
