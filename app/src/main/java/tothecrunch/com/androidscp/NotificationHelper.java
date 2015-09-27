package tothecrunch.com.androidscp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


/**
 * Created by Leebs on 9/26/15.
 */

public class NotificationHelper extends AsyncTask<Integer, Double, Void> {
    private final static String TAG = NotificationHelper.class.getName();
    private NotificationCompat.Builder mBuilder;
    private final Context mContext;
    private final int mId;
    private NotificationManager mNotifyManager;
    private final String mTitle;

    public NotificationHelper(Context context, String title, int id) {
        mContext = context;
        mTitle = title;
        mId = id;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        Log.d(TAG, "doInBackground");
        // waiting 3 seconds so you can see the first notification
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        // the long job
        try {
            long currentTime = System.currentTimeMillis();
            long startTime = currentTime;
            long endTime = startTime + (params[0] * 1000);
            long lastTime = currentTime;
            double totalTime = endTime - startTime;
            publishProgress(0D);
            while (currentTime < endTime) {
                if (currentTime > lastTime + 100) {
                    double perc = (currentTime - startTime) / totalTime * 100D;
                    publishProgress(perc);
                    lastTime = System.currentTimeMillis();
                }
                Thread.sleep(10);
                currentTime = System.currentTimeMillis();
            }
            publishProgress(100D);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * called only once
     */
    private void initNotification() {
        mNotifyManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "onPostExecute");
        super.onPostExecute(result);
        // createNotification("completed");
        setCompletedNotification();
    }
    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        super.onPreExecute();
        initNotification();
        setStartedNotification();

    }
    @Override
    protected void onProgressUpdate(Double... values) {
        Log.d(TAG, "onProgressUpdate with argument = " + values[0]);
        super.onProgressUpdate(values);

        int incr = values[0].intValue();
        if (incr == 0)
            setProgressNotification();
        updateProgressNotification(incr);

    }

    /**
     * the last notification
     */
    private void setCompletedNotification() {
        mBuilder.setSmallIcon(R.drawable.ic_check).setContentTitle(mTitle)
                .setContentText("Upload Completed");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotifyManager.notify(mId, mBuilder.build());
    }

    /**
     * the progress notification
     * <p>
     * called only once
     */
    private void setProgressNotification() {
        mBuilder.setContentTitle(mTitle).setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_add_circle_light_blue_a700_48dp);
    }
    /**
     * the first notification
     */
    private void setStartedNotification() {
        mBuilder.setSmallIcon(R.drawable.ic_add_circle_light_blue_a700_48dp).setContentTitle(mTitle)
                .setContentText("Started");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotifyManager.notify(mId, mBuilder.build());
    }

    /**
     * the progress notification
     * <p>
     * called every 0.1 sec to update the progress bar
     *
     * @param incr
     */
    private void updateProgressNotification(int incr) {
        // Sets the progress indicator to a max value, the
        // current completion percentage, and "determinate"
        // state
        mBuilder.setProgress(100, incr, false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(mId, mBuilder.build());
        // Sleeps the thread, simulating an operation
        // that takes time
    }
}