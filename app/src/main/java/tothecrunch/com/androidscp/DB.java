package tothecrunch.com.androidscp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leebs on 9/13/15.
 */
public class DB extends SQLiteOpenHelper {

    public static final int DB_VERSION =  1 ;
    public static final String DB_NAME =  "connections.db" ;
    public static final String TABLE_NAME = "connections";

    private static final String KEY_ID = "_id";
    private static final String KEY_IP = "ip";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NICKNAME = "nickname";

    Context context;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ip TEXT NOT NULL, username TEXT NOT NULL, "
                + "password TEXT NOT NULL, nickname INTEGER NOT NULL)");
    }
    public void onUpgrade ( SQLiteDatabase db ,  int oldVersion ,  int newVersion )  {
        // This is only a cache for database Online Data, so ITS upgrade policy is
        // to simply to discard the Data and Start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void onDowngrade ( SQLiteDatabase db ,  int oldVersion ,  int newVersion )  {
        onUpgrade(db, oldVersion, newVersion);
    }
    // Adding new connection
    public void addConnection(Connection connection) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP, connection.getIP()); // Contact Name
        values.put(KEY_USERNAME, connection.getUsername()); // Contact Phone Number
        values.put(KEY_PASSWORD, connection.getPassword());
        values.put(KEY_NICKNAME, connection.getNickname());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting single connection
    public Connection getConnection(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID,
                        KEY_IP, KEY_USERNAME, KEY_PASSWORD, KEY_NICKNAME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Connection connection = new Connection(cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
        connection.setID(Integer.parseInt(cursor.getString(0)));
        //returns the found connection
        return connection;
    }

    // Getting All connection
    public List<Connection> getAllConnections() {
        List<Connection> connectionList = new ArrayList<Connection>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        try{
            cursor = db.rawQuery(selectQuery, null);
        }catch(SQLiteException e){
            Log.v("DB", "error in DB query -- no such table? - getAllConnections");
            return null;
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Connection connection = new Connection(cursor.getString(1),
                        cursor.getString(2),cursor.getString(3),cursor.getString(4));
                connection.setID(Integer.parseInt(cursor.getString(0)));
                // Adding connection to list
                connectionList.add(connection);
            } while (cursor.moveToNext());
        }
        // return connection list
        return connectionList;
    }

    // Getting connection Count
    public int getConnectionCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery(countQuery, null);
        }catch(SQLiteException e) {
            Log.v("DB", "error in DB query -- no such table? - getConnectionCount");
            return -1;
        }
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // Updating single connection
    public int updateConnection(Connection connection) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP, connection.getIP());
        values.put(KEY_USERNAME, connection.getUsername());
        values.put(KEY_PASSWORD, connection.getPassword());
        values.put(KEY_NICKNAME, connection.getNickname());

        // updating row
        return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(connection.getID()) });
    }

    // Deleting single connection
    public void deleteConnection(Connection connection) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(connection.getID())});
        db.close();
    }

}