package tothecrunch.com.androidscp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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

    RecyclerView mRecyclerView;
    ConnectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton addCon = (FloatingActionButton) findViewById(R.id.addConnection);
        FloatingActionButton confCon = (FloatingActionButton) findViewById(R.id.confirmConnection);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        db = new DB(this);
        connectionsList = updateConnectionRecyclerList();
        adapter = new ConnectionAdapter(connectionsList);


        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);


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
        if (resultCode == 100) { //if valid entries given, add a card
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

        }
    }
    //button click method for +
    public void addConnect(View v) {
        Intent intent = new Intent(v.getContext(), addConnection.class);
        startActivityForResult(intent, 100);
    }
    //button click method for ->
    public void confConnect(View v) {
        System.out.println("Confirmed Selection");
    }


}

