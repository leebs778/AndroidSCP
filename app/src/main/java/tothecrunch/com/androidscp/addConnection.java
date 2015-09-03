package tothecrunch.com.androidscp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;

//TODO
// • Do not allow "," or ";" in any of the field input
// • make each field required -- (except nickname field)

public class addConnection extends Activity {
    EditText mIPEntry;
    EditText mUsernameEntry;
    EditText mPasswordEntry;
    EditText mNickname;
    android.support.design.widget.FloatingActionButton submitNewConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatingActionButton submitNewConnection = (FloatingActionButton) findViewById(R.id.submitNewConnection);
        setContentView(R.layout.activity_add_connection);
    }
    public void submitNewCon_Clicked(View v) {
        mIPEntry = (EditText) findViewById(R.id.ipEntry);
        mUsernameEntry = (EditText) findViewById(R.id.usernameEntry);
        mPasswordEntry = (EditText) findViewById(R.id.passwordEntry);
        mNickname = (EditText) findViewById(R.id.nickname);

        Intent i = getIntent();
        i.putExtra("IP", mIPEntry.getText().toString());
        i.putExtra("USER", mUsernameEntry.getText().toString());
        i.putExtra("PWD", mPasswordEntry.getText().toString());
        i.putExtra("NICK", mNickname.getText().toString());
        setResult(100, i);
        finish();
    }
}

