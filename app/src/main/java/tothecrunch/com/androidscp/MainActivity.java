package tothecrunch.com.androidscp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText mLocalFile = (EditText) findViewById(R.id.localfileEntry);
        final EditText mUsername = (EditText) findViewById(R.id.usernameEntry);
        final EditText mHostname = (EditText) findViewById(R.id.ipEntry);
        final EditText mPassword = (EditText) findViewById(R.id.passwordEntry);
        final EditText mTargetEntry = (EditText) findViewById(R.id.targetEntry);

        // Extract field data entered from the user of the form:
        // [hostname,username,password,localfile,targetfile]



        //final String[] FileParamsTest = {"192.168.0.14", "Leebs", "beentothecrunch", "/storage/emulated/0/test.txt", "/Users/Leebs/Desktop/"};
        //Log.d("attempted Connection", FileParams[0] + "," + FileParams[1] + "," + FileParams[2] + "," + FileParams[3] + "," + FileParams[4] + ",");

                Button mButton = (Button) findViewById(R.id.SubmitCredentials);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //TODO check for null values, etc
                if (true) { //TODO check if proper fields are filled/correct

                    String[] FileParams = new String[5];
                    FileParams[0] = mHostname.getText().toString();
                    FileParams[1] = mUsername.getText().toString();
                    FileParams[2] = mPassword.getText().toString();
                    FileParams[3] = mLocalFile.getText().toString();
                    FileParams[4] = mTargetEntry.getText().toString();
                    final String[] FileParameters = FileParams;
                    new DownloadSingleFile().execute(FileParameters);


                }
            }

        });
    }
}

