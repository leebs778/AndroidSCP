package tothecrunch.com.androidscp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
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

        String[] connections = {mIPEntry.getText().toString(), mUsernameEntry.getText().toString(),
                mPasswordEntry.getText().toString(), mNickname.getText().toString()};

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
        }else{
            Intent i = getIntent();
            i.putExtra("IP", mIPEntry.getText().toString());
            i.putExtra("USER", mUsernameEntry.getText().toString());
            i.putExtra("PWD", mPasswordEntry.getText().toString());
            i.putExtra("NICK", mNickname.getText().toString());
            setResult(100, i);
            finish();
        }

    }
    public void showErrors(Boolean[] validFields){
        TextInputLayout ipEntry = (TextInputLayout) findViewById(R.id.til1);
        TextInputLayout UsernameEntry = (TextInputLayout) findViewById(R.id.til2);
        TextInputLayout PassEntry = (TextInputLayout) findViewById(R.id.til3);
        TextInputLayout NickEntry = (TextInputLayout) findViewById(R.id.til4);

        String errorMessage = "Required Field -- no using \",\" or \";\"";

        if (!validFields[0]){
            ipEntry.setErrorEnabled(true);
            ipEntry.setError(errorMessage);
        }else{
            ipEntry.setErrorEnabled(false);
        }

        if (!validFields[1]){
            UsernameEntry.setErrorEnabled(true);
            UsernameEntry.setError(errorMessage);
        }else{
            UsernameEntry.setErrorEnabled(false);
        }

        if (!validFields[2]){
            PassEntry.setErrorEnabled(true);
            PassEntry.setError(errorMessage);
        }else{
            PassEntry.setErrorEnabled(false);
        }

        if (!validFields[3]){
            NickEntry.setErrorEnabled(true);
            NickEntry.setError(errorMessage);
        }else{
            NickEntry.setErrorEnabled(false);
        }
    }

    public Boolean[] checkResults(String[] entries){
        Boolean[] validFields = {false, false, false, false};
        for (int i=0; i<entries.length;i++) {
            if (!enteredValue(entries[i])) {
                System.out.println("No Input at " + i);
                continue;
            }else if(!illegalCharacters(entries[i])) {
                System.out.println("Bad input at " + i);
                continue;
            }else{
                validFields[i] = true;
            }
        }
        return validFields;
    }

    public Boolean enteredValue(String entry){
        if (entry == null || entry.trim().isEmpty()){
            return false;
        }else {
            return true;
        }
    }
    public Boolean illegalCharacters(String entry){
        if (entry.indexOf(',') != -1 || entry.indexOf(';') != -1){ //if illegal character present, will return index, otherwise -1
            return false;
        }else {
            return true;
        }
    }
}

