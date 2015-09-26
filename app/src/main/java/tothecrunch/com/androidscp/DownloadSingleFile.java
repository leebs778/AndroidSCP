package tothecrunch.com.androidscp;

import android.os.AsyncTask;
import android.util.Log;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;

import java.io.IOException;

/**
 * Created by Leebs on 5/18/15.
 */
public class DownloadSingleFile extends AsyncTask<String, Integer, Integer>{

        @Override
        /*
        params:
            0 = IP
            1 = Username
            2 = Password
            3 = RemotePath
            4 = LocalPath
         */
        protected Integer doInBackground(String... params) {
            SSHClient ssh = new SSHClient();
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            try {
                ssh.connect(params[0]);

                // ssh.connect()
            } catch (IOException e) {
                Log.d("IO", "cannot establish connection");
                return 0;
            }
            try {
                //ssh.authPassword
                ssh.authPassword(params[1], params[2]);
            } catch (UserAuthException e) {
                Log.d("Auth", "bad user authorization");

            } catch (TransportException e) {
                Log.d("Transport", "transport exception (?)");
                return 0;
            }
            try {

                ssh.newSCPFileTransfer().upload(params[3], params[4]);
                //TODO --> download files from remote to local -- ssh.newSCPFileTransfer().download(params[4], params[3]);
                //                              (remotepath, localpath)
            }catch (IOException e) {
                Log.d("IO", "IO exception --  unable to download");
                return 0;
            }

            try{
                ssh.disconnect();
            }catch (IOException e) {
                Log.d("IO", "IO exception --  unable to disconnect");
                return 0;

            }
            Log.v("success", "Successful SSH connection");
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {


            // Do things like update the progress bar
        }
        protected void onPostExecute(Integer result) {
            // 1 on success
            System.out.println("After execution our result is " + result);
            // Do things like hide the progress bar or change a TextView
        }


    }

