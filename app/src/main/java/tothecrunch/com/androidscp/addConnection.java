package tothecrunch.com.androidscp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

//TODO
// • checkbox to show/hide password

public class addConnection extends AppCompatActivity {
    EditText mIPEntry;
    EditText mUsernameEntry;
    EditText mPasswordEntry;
    EditText mNickname;
    EditText mTargetPath;


    android.support.design.widget.FloatingActionButton submitNewConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatingActionButton submitNewConnection = (FloatingActionButton) findViewById(R.id.submitNewConnection);
        setContentView(R.layout.activity_add_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.connectionsToolbar);
        setSupportActionBar(toolbar);

        String IP = getIntent().getStringExtra("IP");
        String USER = getIntent().getStringExtra("USER");
        String PASS = getIntent().getStringExtra("PASS");
        String NICK = getIntent().getStringExtra("NICK");
        String TARG = getIntent().getStringExtra("TARG");

        mIPEntry = (EditText) findViewById(R.id.ipEntry);
        mUsernameEntry = (EditText) findViewById(R.id.usernameEntry);
        mPasswordEntry = (EditText) findViewById(R.id.passwordEntry);
        mNickname = (EditText) findViewById(R.id.nickname);
        mTargetPath = (EditText) findViewById(R.id.targetPath);

        // initializes listeners to detect when focus is changed out of the
        // textbox --> used to revalidate input after usage.
        setOnFocusChangeListeners(mNickname,mIPEntry,mUsernameEntry);

        mIPEntry.setText(IP);
        mUsernameEntry.setText(USER);
        mPasswordEntry.setText(PASS);
        mNickname.setText(NICK);
        mTargetPath.setText(TARG);

    }
    public void submitNewCon_Clicked(View v) {
        mIPEntry = (EditText) findViewById(R.id.ipEntry);
        mUsernameEntry = (EditText) findViewById(R.id.usernameEntry);
        mPasswordEntry = (EditText) findViewById(R.id.passwordEntry);
        mNickname = (EditText) findViewById(R.id.nickname);
        mTargetPath = (EditText) findViewById(R.id.targetPath);

        String[] connections = {mNickname.getText().toString(), mIPEntry.getText().toString(), mUsernameEntry.getText().toString(),
                mPasswordEntry.getText().toString(), mTargetPath.getText().toString()};

        System.out.println("Submit New Con");

        Boolean[] validFields = checkResults(connections);
        Boolean errorFree = true;
        for (int i =0;i<validFields.length;i++){
            if (!validFields[i]){
                errorFree = false;
            }
        }
        if (!errorFree){
            showErrors(validFields);
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }else{
            Intent i = getIntent();
            i.putExtra("IP", mIPEntry.getText().toString());
            i.putExtra("USER", mUsernameEntry.getText().toString());
            i.putExtra("PWD", mPasswordEntry.getText().toString());
            i.putExtra("NICK", mNickname.getText().toString());
            i.putExtra("TARG", mTargetPath.getText().toString());
            setResult(100, i);
            finish();
        }

    }
    public void showErrors(Boolean[] validFields){
        TextInputLayout ipEntry = (TextInputLayout) findViewById(R.id.til1);
        TextInputLayout UsernameEntry = (TextInputLayout) findViewById(R.id.til2);
        TextInputLayout PassEntry = (TextInputLayout) findViewById(R.id.til3);
        TextInputLayout TargetPath = (TextInputLayout) findViewById(R.id.til4);
        TextInputLayout NickEntry = (TextInputLayout) findViewById(R.id.til5);

        String errorMessage = getString(R.string.connectionInputErrorMessage);

        if (!validFields[0]){
            NickEntry.setErrorEnabled(true);
            NickEntry.setError(errorMessage);
        }else{
            NickEntry.setErrorEnabled(false);
        }
        if (!validFields[1]){
            ipEntry.setErrorEnabled(true);
            ipEntry.setError(errorMessage);
        }else{
            ipEntry.setErrorEnabled(false);
        }

        if (!validFields[2]){
            UsernameEntry.setErrorEnabled(true);
            UsernameEntry.setError(errorMessage);
        }else{
            UsernameEntry.setErrorEnabled(false);
        }

        //disabled because passwords and remote paths can be optional, yo
        /*
        if (!validFields[2]){
            PassEntry.setErrorEnabled(true);
            PassEntry.setError(errorMessage);
        }else{
            PassEntry.setErrorEnabled(false);
        }


        if (!validFields[4]){
            TargetPath.setErrorEnabled(true);
            TargetPath.setError(errorMessage);
        }else{
            TargetPath.setErrorEnabled(false);
        }

        */
    }

    public Boolean[] checkResults(String[] entries){
        // if false, then it's a required field and we should check it
        //                      nick,       ip,     user,   pass,           targetpath
        Boolean[] validFields = {false,     false,  false,   true,           true};
        for (int i=0; i<entries.length;i++) {
            System.out.println("entry at " + i + " : " + entries[i]);
            if (!enteredValue(entries[i])) {
                System.out.println("No Input at " + i);
                continue;
            }else{
                validFields[i] = true;
            }
        }
        return validFields;
    }

    public Boolean enteredValue(String entry) {
        if (entry == null || entry.trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        if (checked){
           // mPasswordEntry.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mPasswordEntry.setTransformationMethod(null);
        }else{
            mPasswordEntry.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
    public void setOnFocusChangeListeners(final EditText nick, final EditText ip, final EditText user){
        final TextInputLayout ipEntry = (TextInputLayout) findViewById(R.id.til1);
        final TextInputLayout UsernameEntry = (TextInputLayout) findViewById(R.id.til2);
        final TextInputLayout NickEntry = (TextInputLayout) findViewById(R.id.til5);
        final String errorMessage = getString(R.string.connectionInputErrorMessage);

        nick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    /* When focus is lost check that the text field
                    * has valid values.
                    */
                if (!hasFocus) {
                    if (!enteredValue(nick.getText().toString())) {
                        NickEntry.setErrorEnabled(true);
                        NickEntry.setError(errorMessage);
                    } else {
                        NickEntry.setErrorEnabled(false);
                    }
                }
            }
        });

        ip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    /* When focus is lost check that the text field
                    * has valid values.
                    */
                if (!hasFocus) {
                    if (!enteredValue(ip.getText().toString())) {
                        ipEntry.setErrorEnabled(true);
                        ipEntry.setError(errorMessage);
                    } else {
                        ipEntry.setErrorEnabled(false);
                    }
                }
            }
        });
        user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    /* When focus is lost check that the text field
                    * has valid values.
                    */
                if (!hasFocus) {
                    if (!enteredValue(user.getText().toString())) {
                        UsernameEntry.setErrorEnabled(true);
                        UsernameEntry.setError(errorMessage);
                    } else {
                        UsernameEntry.setErrorEnabled(false);
                    }
                }
            }
        });

    }
}

