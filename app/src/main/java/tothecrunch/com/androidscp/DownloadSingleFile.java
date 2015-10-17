package tothecrunch.com.androidscp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Leebs on 5/18/15.
 */


public class DownloadSingleFile extends AsyncTask<String, Integer, Integer>{
    long totalFileSize;
    String fileSizeWithFormat;

    private NotificationCompat.Builder mBuilder;
    private final Context mContext;
    private final int mId;
    private NotificationManager mNotifyManager;
    private final String mTitle;
    private String Nickname;

    String[] parameters = new String[7];

    public DownloadSingleFile(Context context, String title, int id, String IP, String USER, String PASS, String PATH, String REMPATH, String NICK) {
        mContext = context;
        mTitle = mContext.getResources().getString(R.string.transferringNotificationTitle) + " to " + NICK;
        mId = id;

        parameters[0] = IP;
        parameters[1] = USER;
        parameters[2] = PASS;
        parameters[3] = PATH;
        parameters[4] = REMPATH;
        parameters[5] = NICK;
        String[] relpath = parameters[3].split("/");
        parameters[6] = relpath[relpath.length-1];


    }
    @Override
    protected void onPreExecute() {
        Log.d("DownloadSingleFile", "onPreExecute");
        super.onPreExecute();

        calculateFileTransferSize();


        initNotification();
        setStartedNotification();

    }

    /*
    params:
        0 = IP
        1 = Username
        2 = Password
        3 = LocalPath
        4 = RemotePath
        5 = NicknameForConnection
     */
    @Override
    protected Integer doInBackground(String... params) {


        Nickname = parameters[5];
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());

        File f = new File(parameters[4]);
        try {
            ssh.connect(parameters[0]);
        } catch (IOException e) {
            Log.d("IO", "cannot establish connection");
            return 1;
        }
        try {
            //ssh.authPassword
            ssh.authPassword(parameters[1], parameters[2]);
        } catch (UserAuthException e) {
            Log.d("Auth", "bad user authorization");
            return 2;

        } catch (TransportException e) {
            Log.d("Transport", "transport exception (?)");
            return 3;
        }
        try {
            publishProgress(1);
            ssh.newSCPFileTransfer().upload(parameters[3], parameters[4]);
            //TODO --> download files from remote to local -- ssh.newSCPFileTransfer().download(params[4], params[3]);
            //                              (local,remote)
        }catch (IOException e) {
            Log.d("IO", "IO exception --  unable to upload -- bad file paths?");
            return 4;
        }
        try{
            ssh.disconnect();
        }catch (IOException e) {
            Log.d("IO", "IO exception --  unable to disconnect");
            return 5;
        }
        Log.v("success", "Successful SSH connection");
        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] == 1) {
            setStartedNotification();
        }

        // Do things like update the progress bar
    }

    private void initNotification() {
        mNotifyManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
    }

    private void setStartedNotification() {
        mBuilder.setSmallIcon(R.drawable.ic_send_24dp).setContentTitle(mTitle)
                .setContentText("Uploading " + parameters[6]+"(" + fileSizeWithFormat +")")
                .setProgress(0,0,true);
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

    private void setErrorNotification(String result) {
        mBuilder.setSmallIcon(R.drawable.ic_report_24dp).setContentTitle("Could not send file")
                .setContentText(result)
                .setProgress(0, 0, false);
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

    private void setCompletedNotification() {
        mBuilder.setSmallIcon(R.drawable.ic_check).setContentTitle("Sent to " + Nickname)
                .setContentText(parameters[6] + "(" + fileSizeWithFormat +")" + " uploaded into " + parameters[4])
                .setProgress(0,0,false);
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

    protected void onPostExecute(Integer result) {
        // 0, Success
        // 1, Bad IP Address
        // 2, Wrong Login Info
        // 3, Transport Exception?
        // 4, Bad File Paths
        // 5, Unable to Disconnect
        String[] resultVector = {"Success","Cannot connect - Invalid IP address or SSH is disabled on target machine", "Incorrect Username or Password", "Transport Exception",
                                    "Incorrect File Path(s)", "Unable to Disconnect from Server"};
        System.out.println("After execution our result is " + result + " with resultVector " + resultVector[result]);
        if (result == 0){
            setCompletedNotification();
        }else{
            setErrorNotification(resultVector[result]);
        }

    }

    public static long folderSize(File dir) {
        if (dir == null) return 0;
        if (dir.isFile()) return dir.length();
        if (!dir.isDirectory()) return 0;

        File[] files = dir.listFiles();
        if (files == null) return 0;
        long size = 0;
        for (File file : files) {
            if (file == null) continue;
            if (file.isFile() || file.isHidden())
                size += file.length();
            else
                size += folderSize(file);
        }
        return size;
    }
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    public void calculateFileTransferSize(){
        totalFileSize = 0;
        File m = new File(parameters[3]);
        totalFileSize = folderSize(m);
        fileSizeWithFormat = readableFileSize(totalFileSize);
        String[] extractSpace = fileSizeWithFormat.split(" ");
        fileSizeWithFormat = extractSpace[0] + extractSpace[1];
        System.out.println("Hey, total file size is " + fileSizeWithFormat);
    }

}

