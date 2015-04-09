package com.partysearch;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ListActivity implements OnClickListener {

	private static final String FIREBASE_URL = "https://blistering-heat-2311.firebaseio.com";

	private String mUsername;
	private Firebase firebaseRef;
	private Firebase firebaseChild;
	private ValueEventListener mConnectedListener;
	private ChatListAdapter mChatListAdapter;

	private Room newRoom;
	private EditText psnName;
	private EditText level;
	private EditText note;
	private ListView listView;
	private Spinner spinner;
	private Button removeButton;
	private Button postButton;
	
	private Timer timer;
	private TimerTask timerTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Firebase.setAndroidContext(this);

		//setTitle("Chatting as " + mUsername);
		psnName = (EditText) findViewById(R.id.psnInput);
		level = (EditText) findViewById(R.id.level);
		note = (EditText) findViewById(R.id.note);
		listView = (ListView) findViewById(android.R.id.list);
		spinner = (Spinner) findViewById(R.id.spinner);

		removeButton = (Button) findViewById(R.id.remove);
		removeButton.setBackgroundColor(Color.RED);
		postButton = (Button) findViewById(R.id.createRoom);
		postButton.setOnClickListener(this);
		removeButton.setOnClickListener(this);

		// Setup our Firebase mFirebaseRef
		firebaseRef = new Firebase(FIREBASE_URL).child("chat");
		firebaseChild = new Firebase(FIREBASE_URL);
		
		populateSpinner();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createRoom:
			makeRoom();
			removeButton.setVisibility(View.VISIBLE);
			postButton.setVisibility(View.INVISIBLE);
			startTimer();
			break;
		case R.id.remove:
			removeRoom();
			removeButton.setVisibility(View.INVISIBLE);
			postButton.setVisibility(View.VISIBLE);
			stopTimer();
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// Setup our view and list adapter. Ensure it scrolls to the bottom as
		// data changes
		// final ListView listView = getListView();
		// Tell our list adapter that we only want 50 messages at a time
		mChatListAdapter = new ChatListAdapter(firebaseRef.limit(40), this,
				R.layout.room_info, psnName.getText().toString());
		listView.setAdapter(mChatListAdapter);
		mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				listView.setSelection(mChatListAdapter.getCount() - 1);
			}
		});

		// Finally, a little indication of connection status
		mConnectedListener = firebaseRef.getRoot().child(".info/connected")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						boolean connected = (Boolean) dataSnapshot.getValue();
						if (connected) {
							Toast.makeText(MainActivity.this,
									"Connected to Firebase", Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(MainActivity.this,
									"Disconnected from Firebase",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onCancelled(FirebaseError firebaseError) {
						// No-op
					}
				});
	}

	
	@Override
	public void onStop() {
		super.onStop();
		firebaseRef.getRoot().child(".info/connected")
				.removeEventListener(mConnectedListener);
		mChatListAdapter.cleanup();
		removeRoom();
	}

	private void populateSpinner() {
		spinner = (Spinner) findViewById(R.id.spinner);
		List<String> list = new ArrayList<String>();
		list.add("Supply Raid");
		list.add("Interrogation");
		list.add("Survivors");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}

	private void makeRoom() {
		
		// Firebase(FIREBASE_URL).child(psnName.getText().toString());
		newRoom = new Room(psnName.getText().toString(), Integer.parseInt(level
				.getText().toString()), note.getText().toString(), spinner
				.getSelectedItem().toString());
		firebaseChild = firebaseRef.push();
		//firebaseRef.push().setValue(newRoom);
		firebaseChild.setValue(newRoom);
		
		// firebaseRef.child("userName").setValue(newRoom.getUserName());
		// firebaseRef.child("level").setValue(newRoom.getLevel());
		// firebaseRef.child("note").setValue(newRoom.getNote());
		// /firebaseRef.child("gametype").setValue(newRoom.getGametype());

	}

	private void removeRoom() {
		if(!firebaseChild.toString().equals(FIREBASE_URL)){
			String childPath = splitUrl(firebaseChild.toString());
			System.out.println("path: " + childPath);
			firebaseRef.child(childPath).removeValue();
			stopTimer();
			removeButton.setVisibility(View.INVISIBLE);
			postButton.setVisibility(View.VISIBLE);
		}
	}
	
	private String splitUrl(String url){
		String[] parts = url.split("\\/");
		return parts[parts.length-1];
	}
	
	private void startTimer(){
		timer = new Timer();
		initializeTimerTask();
		timer.schedule(timerTask,1020000); //every 17 minutes.
	}
	
	private void stopTimer(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}
	
	private void initializeTimerTask(){
		timerTask = new TimerTask(){
			public void run(){
				removeRoom();
			}
		};
	}

}
