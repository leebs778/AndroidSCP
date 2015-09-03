package tothecrunch.com.androidscp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

//TODO
//  • if user has no recent connections, change color of "send file" icon to dark grey
//  • change icon of "send file icon" to something darker, more visible

public class MainActivity extends Activity {
    Boolean fileWipe = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton addCon = (FloatingActionButton) findViewById(R.id.addConnection);
        FloatingActionButton confCon = (FloatingActionButton) findViewById(R.id.confirmConnection);



        //final String[] FileParamsTest = {"192.168.0.14", "Leebs", "beentothecrunch", "/storage/emulated/0/test.txt", "/Users/Leebs/Desktop/"};
        //Log.d("attempted Connection", FileParams[0] + "," + FileParams[1] + "," + FileParams[2] + "," + FileParams[3] + "," + FileParams[4] + ",");

    }
    public void checkRecentConnections(){

    }

    public void addConnect(View v) {
        Intent intent = new Intent(v.getContext(), addConnection.class);
        startActivityForResult(intent, 100);
    }

    public void confConnect(View v) {
        System.out.println("Confirmed Selection");
    }

    public String readFile() {

        FileInputStream fis = null;
        String line = "";
        try{
            fis = this.openFileInput("saved_Connections");

        }catch(FileNotFoundException e){
            Log.d("File IO", "File not found on read");
            return "nothing";
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        try{
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        }catch(IOException e) {
            Log.d("File IO", "line read exception");
        }
        return sb.toString();
    }

    public void writeFile(String stringToWrite) {
        if (fileWipe){
            File dir = getFilesDir();
            File file = new File(dir, "saved_Connections");
            boolean deleted = file.delete();
        }
        String FILENAME = "saved_Connections";
        try {
            //TODO PRIVATE REWRITES FILE CONTENTS, DOESNT SAVE --  CHANGE TO MODE_APPEND
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fos);
            outputWriter.write(stringToWrite);
            outputWriter.close();
            System.out.println("file exists now");
        } catch (IOException e) {
            System.out.println("IO Exception in file create/writing");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            String connection = data.getExtras().get("IP").toString() + "," + data.getExtras().get("USER").toString() + "," +
                                data.getExtras().get("PWD").toString() + "," + data.getExtras().get("NICK").toString() + ";";
            writeFile(connection + System.getProperty("line.separator"));
            System.out.println(readFile());
        }

    }
}

