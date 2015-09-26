package tothecrunch.com.androidscp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Leebs on 9/13/15.
 */
public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {

    private List<Connection> mConnections;
    private SparseBooleanArray selectedItems;

    /*
    // selection methods
    public void toggleSelection(int pos){
        if(selectedItems.get(pos,false)){
            selectedItems.delete(pos);
        }else{
            selectedItems.put(pos,true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections(){
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount(){
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for(int i=0;i<selectedItems.size();i++){
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
    //new selection methods
    */

    public ConnectionAdapter(List<Connection> Connection) {
        mConnections = Connection;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public TextView mNickText;
        public TextView mLoginText;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);


            mNickText = (TextView) itemView.findViewById(R.id.nickname_Text);
            mLoginText = (TextView) itemView.findViewById(R.id.login_Text);
        }

    }
    // ... constructor and member variables

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ConnectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View ConnectionView = inflater.inflate(R.layout.recycler_view, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(ConnectionView);
        return viewHolder;
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ConnectionAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Connection connection = mConnections.get(position);

        // Set item views based on the data model
        TextView nickTextView = viewHolder.mNickText;
        nickTextView.setText(connection.getNickname());

        TextView loginTextView = viewHolder.mLoginText;
        //IP will be blank for our "Please add a connection" entries, test for that
        if (connection.getIP() != "") {
            loginTextView.setText(connection.getUsername() + "@" + connection.getIP());
        }else{
            loginTextView.setText("");
        }

    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mConnections.size();
    }
}
