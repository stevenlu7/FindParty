package com.partysearch;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter extends FirebaseListAdapter<Room> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String mUsername;
    //private Handler handler;

    public ChatListAdapter(Query ref, Activity activity, int layout, String mUsername) {
        super(ref, Room.class, layout, activity);
        this.mUsername = mUsername;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Room room) {
        // Map a Chat object to an entry in our listviewsw
        String author = room.getUserName();
        TextView authorText = (TextView) view.findViewById(R.id.user);
        authorText.setText(author);

        ((TextView) view.findViewById(R.id.level)).setText(room.getLevel() + "");
        ((TextView) view.findViewById(R.id.note)).setText(room.getNote());
        ((TextView) view.findViewById(R.id.gametype)).setText(room.getGametype()); 
        ((TextView) view.findViewById(R.id.console)).setText(room.getConsole());   
        ((TextView) view.findViewById(R.id.timestamp)).setText(room.formatTime(room.getTimeLong()) + " PST");
    }
     
}
