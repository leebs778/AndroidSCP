package tothecrunch.com.androidscp;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;


//TODO
//  • if user has no recent connections, change color of "send file" icon to dark grey
//  • change icon of "send file icon" to something darker, more visible

public class MainActivity extends AppCompatActivity {
    List<Connection> connectionsList;
    DB db;
    List<Connection> blankList;
    int position_Last_Clicked;

    RecyclerView mRecyclerView;
    ConnectionAdapter adapter;
    AppRate rate;
    Connection ContextConnection = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connections_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.deleteAll) {
            deleteAllConnections();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.recyclerView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                System.out.println("Edit Selected");
                if (ContextConnection == null){
                    System.out.println("Invalid Selection for Context Menu");
                    return false;
                }
                Intent i = new Intent(getApplicationContext(), addConnection.class);
                i.putExtra("IP",ContextConnection.getIP());
                i.putExtra("USER", ContextConnection.getUsername());
                i.putExtra("PASS",ContextConnection.getPassword());
                i.putExtra("NICK",ContextConnection.getNickname());
                i.putExtra("TARG",ContextConnection.getTargetPath());

                startActivityForResult(i, 300);
                return true;

            case R.id.delete:
                System.out.println("Delete Selected");
                System.out.println("Removal of " + ContextConnection.toString() + " at " + connectionsList.indexOf(ContextConnection));
                return deleteItemFromConnections(ContextConnection);
                // returns true if item deleted, false if not
            default:
                return super.onContextItemSelected(item);
        }
    }
    //takes an object and deletes it from our connectionslist
    public boolean deleteItemFromConnections(Connection con){
        int index = connectionsList.indexOf(con);
        if (index == -1){
            System.out.println("Item does not exist in connectionsList -- cannot remove");
            return false;
        }else{
            connectionsList.remove(con);
            db.deleteConnection(con);
            adapter.notifyItemRemoved(index);
            if (connectionsList.isEmpty()){
                System.out.println("Empty Connections List");
                connectionsList = updateConnectionRecyclerList();
                for (Connection cn : connectionsList){
                    System.out.println(cn.toString());
                }
                adapter = new ConnectionAdapter(connectionsList);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setHasFixedSize(true);
                RecyclerView.ItemDecoration itemDecoration = new
                        DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
                mRecyclerView.addItemDecoration(itemDecoration);

            }
            return true;
        }
    }
    public void deleteAllConnections(){
        connectionsList = updateConnectionRecyclerList();
        connectionsList.clear();
        db.deleteAllConnections();
        connectionsList = updateConnectionRecyclerList();
        adapter = new ConnectionAdapter(connectionsList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar Initalize
        Toolbar toolbar = (Toolbar) findViewById(R.id.connectionsToolbar);
        setSupportActionBar(toolbar);

        //Function to initalize the app rating library
        rate = AppRatingInitalize();


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

        // allows a context menu for each recyclerview item
        registerForContextMenu(mRecyclerView);


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        position_Last_Clicked = position;
                        Connection selectedConnection = connectionsList.get(position);
                        view.playSoundEffect(SoundEffectConstants.CLICK);
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        System.out.println(selectedConnection.toString());
                        if (selectedConnection.getNickname() == "Add a Connection"){
                            addConnect(view);
                            return;
                            //jumps to the add connection activity
                        }
                        //Short vibration for the use


                        Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
                        // This works if you defined the intent filter
                        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        // Set these depending on your use case. These are the defaults.
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                        i.putExtra(FilePickerActivity.EXTRA_PATHS, true);
                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE_AND_DIR);
                        // Configure initial directory by specifying a String.
                        // You could specify a String like "/storage/emulated/0/", but that can
                        // dangerous. Always use Android's API calls to get paths to the SD-card or
                        // internal memory.
                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                        startActivityForResult(i, 200);

                    }

                    @Override
                    public void onItemLongClick(View view, int position)
                    {
                        System.out.println("Long click detected");
                        ContextConnection = connectionsList.get(position);
                        System.out.println("Long Click on " + ContextConnection.toString());
                        // disallows context menu for a Blank Connection
                        if (ContextConnection.getNickname() != "Add a Connection") {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                            openContextMenu(view);
                        }
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
        if (requestCode== 100 && resultCode !=0) { //if valid entries given, add a card
            Connection connection = new Connection( data.getExtras().get("IP").toString(),
                    data.getExtras().get("USER").toString(), data.getExtras().get("PWD").toString(),
                    data.getExtras().get("NICK").toString(), data.getExtras().get("TARG").toString());
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
        }else if (requestCode == 200 && resultCode !=0){
            System.out.println("Return from file pick activity");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip = data.getClipData();
                if (clip != null) {
                    List<Uri> uriList = new ArrayList<>();
                    for (int i = 0; i < clip.getItemCount(); i++) {
                        Uri uri = clip.getItemAt(i).getUri();
                        uriList.add(uri); // add that uri to our list of files to upload
                    }
                    for (Uri uri : uriList){
                        sendFile(uri);
                    }
                }
                //updates #transfers done
                rate.checkAndShow();
            }
        }else if(requestCode == 300 && resultCode !=0){
            //if we're returning from editing a connection
            System.out.println("Updated a connection");
            Connection connection = new Connection( data.getExtras().get("IP").toString(),
                    data.getExtras().get("USER").toString(), data.getExtras().get("PWD").toString(),
                    data.getExtras().get("NICK").toString(), data.getExtras().get("TARG").toString());


            connectionsList = getSavedConnections();
            System.out.println("Editing Connection " + ContextConnection.toString());
            connectionsList.remove(ContextConnection); //delete old entry
            db.deleteConnection(ContextConnection);

            db.addConnection(connection);
            connectionsList.add(connection);

            connectionsList = updateConnectionRecyclerList();
            Collections.reverse(connectionsList);
            adapter = new ConnectionAdapter(connectionsList);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.ItemDecoration itemDecoration = new
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
            mRecyclerView.addItemDecoration(itemDecoration);
            // allows a context menu for each recyclerview item
            registerForContextMenu(mRecyclerView);

            ContextConnection = null; //clear out the last edited connection

        }
        else{

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
         // System.out.println("attemping to send file to :\t" + Arrays.toString(connection2));
       // new DownloadSingleFile().execute(connection2[0], connection2[1], connection2[2], connection2[3],connection2[4]);
        String targetPath = con.getTargetPath();
        if (targetPath == null || targetPath == ""){    // if the user didnt enter a targetPath, default to /tmp/
            targetPath = "/tmp/";
        }
        new DownloadSingleFile(getApplicationContext(), uri.getPath(), 1,
                con.getIP(), con.getUsername(), con.getPassword(), uri.getPath(), targetPath, con.getNickname()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    public AppRate AppRatingInitalize(){
        AppRate rate = AppRate.with(MainActivity.this);
        rate.text(getString(R.string.AppRatePrompt));
        rate.theme(AppRateTheme.DARK);
        rate.initialLaunchCount(5);
        rate.retryPolicy(RetryPolicy.EXPONENTIAL);
        return rate;
    }
}

