package tothecrunch.com.androidscp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.BasicListCardProvider;
import com.dexafree.materialList.view.MaterialListView;

import java.util.Arrays;

//TODO
//  • if user has no recent connections, change color of "send file" icon to dark grey
//  • change icon of "send file icon" to something darker, more visible

public class MainActivity extends Activity {
    Boolean sharedPrefWipe = false;
    String[][] connectionsPresent = new String[3][4];
    Card noCard;
    MaterialListView mListView;
    Card card0, card1, card2;
    Card[] cardsAlive = {null,null,null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton addCon = (FloatingActionButton) findViewById(R.id.addConnection);
        FloatingActionButton confCon = (FloatingActionButton) findViewById(R.id.confirmConnection);

        mListView = (MaterialListView) findViewById(R.id.material_listview);
        String[][] connections = getRecentConnections();

        for (int i = 0; i < connections.length; i++) {
            System.out.println(Arrays.toString(connections[i]));
        }
        if (!haveConnections(connections)) {
            noCard = addCard(mListView, "No Connections!", "add a connection");
            cardsAlive[0] = noCard;
        } else {
            try {
                mListView.remove(cardsAlive[0]); // removes the "No Card" card
            } catch (Exception e) {
                System.out.println("Tried removing the noCard Card -- it wasnt there!");
            }
            if (connections[0][0] != null) {
                card0 = addCard(mListView, connections[0][3], connections[0][1] + "@" + connections[0][0]);
                cardsAlive[0] = card0;
            }
            if (connections[1][0] != null) {
                card1 = addCard(mListView, connections[1][3], connections[1][1] + "@" + connections[1][0]);
                cardsAlive[1] = card1;
            }
            if (connections[2][0] != null) {
                card2 = addCard(mListView, connections[2][3], connections[2][1] + "@" + connections[2][0]);
                cardsAlive[2] = card2;
            }
        }
        //final String[] FileParamsTest = {"192.168.0.14", "Leebs", "beentothecrunch", "/storage/emulated/0/test.txt", "/Users/Leebs/Desktop/"};
        //Log.d("attempted Connection", FileParams[0] + "," + FileParams[1] + "," + FileParams[2] + "," + FileParams[3] + "," + FileParams[4] + ",");

    }

    public Boolean haveConnections(String[][] connections) {
        Boolean connectionsPresent = false;
        for (int i = 0; i < connections.length; i++) {
            if (connections[i][0] != null && connections[i][0] != "") {
                connectionsPresent = true;
            }
        }
        return connectionsPresent;
    }

    public Card addCard(MaterialListView listview, String title, String description) {
        Card card = new Card.Builder(this)
                .withProvider(BasicListCardProvider.class)
                .setTitle(title)
                .setDescription(description)
                .setBackgroundColor(getResources().getColor(R.color.cardview_light_background))
                .setDescriptionColor(getResources().getColor(R.color.description_color))
                .setTitleColor(getResources().getColor(R.color.grey_title))
                .endConfig()
                .build();
        listview.add(card);
        return card;
    }

    public void addConnect(View v) {
        Intent intent = new Intent(v.getContext(), addConnection.class);
        startActivityForResult(intent, 100);
    }

    public void confConnect(View v) {
        System.out.println("Confirmed Selection");
    }

    public String[][] getRecentConnections() {
        SharedPreferences prefs = new ObscuredSharedPreferences(this, this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE));
        String[][] connections = new String[3][4];
        for (int i = 0; i < connectionsPresent.length; i++) {
            connections[i][0] = prefs.getString(i + "_IP", null);
            connections[i][1] = prefs.getString(i + "_USER", null);
            connections[i][2] = prefs.getString(i + "_PWD", null);
            connections[i][3] = prefs.getString(i + "_NICK", null);
        }
        return connections;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) { //if valid entries given, add a card
            SharedPreferences prefs = new ObscuredSharedPreferences(this, this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_APPEND));
            for (int i = 0; i < connectionsPresent.length; i++) {
                if (connectionsPresent[i][0] == null) { //only tests if empty spot, doesnt clear when full
                    System.out.println("adding info to prefs location " + i);
                    prefs.edit().putString(i + "_IP", data.getExtras().get("IP").toString()).commit();
                    prefs.edit().putString(i + "_USER", data.getExtras().get("USER").toString()).commit();
                    prefs.edit().putString(i + "_PWD", data.getExtras().get("PWD").toString()).commit();
                    prefs.edit().putString(i + "_NICK", data.getExtras().get("NICK").toString()).commit();
                    break;
                }
            }

            connectionsPresent = getRecentConnections();
            updateCards();

        }
    }
    public void updateCards(){
        for (int i = 0; i < 3; i++) {
            System.out.println(Arrays.toString(connectionsPresent[i]));
        }
        if (!haveConnections(connectionsPresent)) {
            noCard = addCard(mListView, "No Connections!", "add a connection");
            cardsAlive[0] = noCard;
            System.out.println("Added a noCard on return");
        } else {
            mListView.clearAll();
            if (connectionsPresent[0][0] != null) {
                card0 = addCard(mListView, connectionsPresent[0][3], connectionsPresent[0][1] + "@" + connectionsPresent[0][0]);
                cardsAlive[0] = card0;
            }
            if (connectionsPresent[1][0] != null) {
                card1 = addCard(mListView, connectionsPresent[1][3], connectionsPresent[1][1] + "@" + connectionsPresent[1][0]);
                cardsAlive[1] = card1;
            }
            if (connectionsPresent[2][0] != null) {
                card2 = addCard(mListView, connectionsPresent[2][3], connectionsPresent[2][1] + "@" + connectionsPresent[2][0]);
                cardsAlive[2] = card2;
            }
        }
    }
    public void

}

