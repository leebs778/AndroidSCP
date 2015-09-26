package tothecrunch.com.androidscp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//TODO
//  • if user has no recent connections, change color of "send file" icon to dark grey
//  • change icon of "send file icon" to something darker, more visible

public class MainActivity extends Activity {
    Boolean sharedPrefWipe = false;
    List<Connection> connectionsList;
    DB db;
    List<Connection> blankList;
    int position_Last_Clicked;

    RecyclerView mRecyclerView;
    ConnectionAdapter adapter;

    static final int ADD_CONNECTION_REQUEST = 1;
    static final int PICK_FILE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton addCon = (FloatingActionButton) findViewById(R.id.addConnection);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        db = new DB(this);
        connectionsList = updateConnectionRecyclerList();
        Collections.reverse(connectionsList);
        adapter = new ConnectionAdapter(connectionsList);


        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), "Item at "+ position + " clicked", Toast.LENGTH_SHORT).show();
                        position_Last_Clicked = position;
                        System.out.println(connectionsList.get(position).toString());
                        Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
                        // This works if you defined the intent filter
                        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        // Set these depending on your use case. These are the defaults.
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                        // Configure initial directory by specifying a String.
                        // You could specify a String like "/storage/emulated/0/", but that can
                        // dangerous. Always use Android's API calls to get paths to the SD-card or
                        // internal memory.
                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                        startActivityForResult(i, 200);
                    }
                })
        );

        //final String[] FileParamsTest = {"192.168.0.14", "Leebs", "beentothecrunch", "/storage/emulated/0/test.txt", "/Users/Leebs/Desktop/"};
        //Log.d("attempted Connection", FileParams[0] + "," + FileParams[1] + "," + FileParams[2] + "," + FileParams[3] + "," + FileParams[4] + ",");
    }
    // update our current connection list
    public List<Connection> updateConnectionRecyclerList(){
        int connectionCount = db.getConnectionCount();
        if (connectionCount > 0){
            // build up our connections saved from SharedPreferences
            return db.getAllConnections();
        }else{
            blankList = Connection.createBlankConnectionList();
            return blankList;
        }
    }

    public List<Connection> getSavedConnections() {
        List<Connection> connections = db.getAllConnections();
        System.out.println("Saved Connections are as follows");
        for (Connection cn : connections){
            System.out.println(cn.toString());
        }
        return connections;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("requestCode " + requestCode);
        if (requestCode== 100) { //if valid entries given, add a card
            Connection connection = new Connection( data.getExtras().get("IP").toString(),
                    data.getExtras().get("USER").toString(), data.getExtras().get("PWD").toString(),
                    data.getExtras().get("NICK").toString());
            db.addConnection(connection);
            connectionsList = getSavedConnections();
            System.out.println(Arrays.toString(connectionsList.toArray()));
            System.out.println("Last item is :" + connectionsList.get(connectionsList.size() - 1).toString());

            Collections.reverse(connectionsList);
            adapter = new ConnectionAdapter(connectionsList);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.ItemDecoration itemDecoration = new
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
            mRecyclerView.addItemDecoration(itemDecoration);
        }else if (requestCode == 200){
            System.out.println("Return from file pick activity");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip = data.getClipData();

                if (clip != null) {
                    for (int i = 0; i < clip.getItemCount(); i++) {
                        Uri uri = clip.getItemAt(i).getUri();
                        sendFile(uri);
                    }
                }
            }
        }
    }
    //button click method for +
    public void addConnect(View v) {
        Intent intent = new Intent(v.getContext(), addConnection.class);
        startActivityForResult(intent, 100);
    }
    public void sendFile(Uri uri){
        Connection con = connectionsList.get(position_Last_Clicked);
        /*
        params:
            0 = IP
            1 = Username
            2 = Password
            3 = RemotePath
            4 = LocalPath
         */
        String[] connection = {con.getIP(),con.getUsername(),con.getPassword(), uri.getPath(), "/tmp/"};
        System.out.println("old connection list :\t" + Arrays.toString(connection));
        String[] connection2 = {"192.168.0.13","Leebs","beentothecrunch", "/storage/emulated/0/Download/chapter0.pdf", "/Users/Leebs/Desktop/"};
       // System.out.println("attemping to send file to :\t" + Arrays.toString(connection2));
       // new DownloadSingleFile().execute(connection2[0], connection2[1], connection2[2], connection2[3],connection2[4]);
        AsyncTask fileUpload = new DownloadSingleFile().execute(con.getIP(), con.getUsername(), con.getPassword(), uri.getPath(), "/temp/");
        
    }

}

