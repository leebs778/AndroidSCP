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
public class DownloadSingleFile extends AsyncTask<String, Integer, String>{

        @Override
        // params passed in are of the form:[hostname,username,password,localfile,targetfile]
        protected String doInBackground(String... params) {
            SSHClient ssh = new SSHClient();
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            try {

                ssh.connect(params[0]);
                // ssh.connect()

            } catch (IOException e) {
                Log.v("IO", "cannot establish connection");
                return "not connecting";
            }

            try {
                //ssh.authPassword
                ssh.authPassword(params[1], params[2]);
            } catch (UserAuthException e) {
                Log.v("Auth", "bad user authorization");
            } catch (TransportException e) {
                Log.v("Transport", "transport exception (?)");
            }

            try {

                ssh.newSCPFileTransfer().download(params[4], params[3]);

                //todo -- UPLOAD  ssh.newSCPFileTransfer().upload("/storage/emulated/0/test.txt", "/Users/Leebs/Desktop");
            }catch (IOException e) {
                Log.v("IO", "IO exception --  unable to download");
            }

            try{
                ssh.disconnect();
            }catch (IOException e) {
                Log.v("IO", "IO exception --  unable to disconnect");
            }

            return "end";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {


            // Do things like update the progress bar
        }
        protected void onPostExecute(String result) {

            // Do things like hide the progress bar or change a TextView
        }


    }

