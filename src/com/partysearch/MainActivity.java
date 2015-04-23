package com.partysearch;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends LicenseCheck implements OnClickListener {
	//Extends licensecheck to run license check. If want to bypass license check, extend ListActivit instead.
	private static final String FIREBASE_URL = "https://blistering-heat-2311.firebaseio.com";

	private boolean roomRemoved;
	private Firebase firebaseRef;
	private Firebase firebaseChild;
	private ValueEventListener mConnectedListener;
	private ChatListAdapter mChatListAdapter;

	private Room newRoom;
	private EditText psnName;
	private EditText level;
	private EditText note;
	private ListView listView;
	private Spinner gametypeSpinner;
	private Spinner consoleSpinner;
	private Button removeButton;
	private Button postButton;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		roomRemoved = false;
		psnName = (EditText) findViewById(R.id.psnInput);
		level = (EditText) findViewById(R.id.level);
		note = (EditText) findViewById(R.id.note);
		listView = (ListView) findViewById(android.R.id.list);
		gametypeSpinner = (Spinner) findViewById(R.id.spinner);

		removeButton = (Button) findViewById(R.id.remove);
		removeButton.setBackgroundColor(Color.RED);
		postButton = (Button) findViewById(R.id.createRoom);
		postButton.setOnClickListener(this);
		removeButton.setOnClickListener(this);

		handler = new Handler();
		populateSpinner();
		loadAd();

		// Setup our Firebase mFirebaseRef
		firebaseRef = new Firebase(FIREBASE_URL).child("chat");
		firebaseChild = new Firebase(FIREBASE_URL);
		
		if(savedInstanceState != null){
			//When screen orientation changes, activity runs again. I want to 
			//keep some states of my buttons and url of the room that was created (if any)
			removeButton.setVisibility(savedInstanceState.getInt("remove"));
			postButton.setVisibility(savedInstanceState.getInt("post"));
			firebaseChild = new Firebase(savedInstanceState.getString("url"));
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.createRoom:
			if (psnName.getText().toString().equals("")
					|| level.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"Missing Gamertag/level", Toast.LENGTH_SHORT).show();
			} else {
				makeRoom();
				removeButton.setVisibility(View.VISIBLE);
				postButton.setVisibility(View.INVISIBLE);
				handler.postDelayed(runnable, 1020000); // remove room after 17
														// mins 
			}
			break;
		case R.id.remove:
			removeRoom();
			removeButton.setVisibility(View.INVISIBLE);
			postButton.setVisibility(View.VISIBLE);
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

		//not my code
		// Finally, a little indication of connection status. 
		mConnectedListener = firebaseRef.getRoot().child(".info/connected")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						boolean connected = (Boolean) dataSnapshot.getValue();
						if (connected) {
							Toast.makeText(MainActivity.this, "Connected",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "Disconnected",
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
		Firebase.goOffline(); //I only have 50 max connections. Gotta make use of every one of it.
		//removeRoom();
	}

	@Override
	public void onResume(){
		super.onResume();
		Firebase.goOnline();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//when screen orientation changes, save the visibility of these buttons 
		//and url of room (if created) 
		//if I don't do this, firebaseChild will lose the URL string of the room (if created).
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putInt("remove", removeButton.getVisibility());
	    savedInstanceState.putInt("post", postButton.getVisibility());
	    savedInstanceState.putString("url", firebaseChild.toString());
	}
	
	private void loadAd() {
		//Ads purposes
		boolean smallSize = getResources().getBoolean(R.bool.isSmall);
		if (!smallSize) {
			AdView mAdView = (AdView) findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
		}
	}

	private void populateSpinner() {
		gametypeSpinner = (Spinner) findViewById(R.id.spinner);
		consoleSpinner = (Spinner) findViewById(R.id.spinner02);
		List<String> list = new ArrayList<String>();
		list.add("Supply Raid");
		list.add("Interrogation");
		list.add("Survivors");

		List<String> consoleList = new ArrayList<String>();
		consoleList.add("PS4");
		consoleList.add("PS3");

		ArrayAdapter<String> gametypeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		gametypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<String> consoleAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, consoleList);
		gametypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		gametypeSpinner.setAdapter(gametypeAdapter);
		consoleSpinner.setAdapter(consoleAdapter);
	}

	private void makeRoom() {
		newRoom = new Room(psnName.getText().toString(), Integer.parseInt(level
				.getText().toString()), note.getText().toString(),
				gametypeSpinner.getSelectedItem().toString(), consoleSpinner
						.getSelectedItem().toString());
		//firebaseChild now contains the url of the room created so we can remove it 
		//when user hits "remove" button
		firebaseChild = firebaseRef.push();
		// firebaseRef.push().setValue(newRoom);
		firebaseChild.setValue(newRoom);

		roomRemoved = false;
		//listView.setSelectionAfterHeaderView(); // scroll to top
	}

	private void removeRoom() {
		//if firebaseChild is not the same as FIREBASE_URL,
		//it means a create has been created by this user
		if (!firebaseChild.toString().equals(FIREBASE_URL)) {
			String childPath = splitUrl(firebaseChild.toString());
			firebaseRef.child(childPath).removeValue();
			removeButton.setVisibility(View.INVISIBLE);
			postButton.setVisibility(View.VISIBLE);
			roomRemoved = true;
		}
	}
	
	//Here's a sample URL after a room is created:
	//https://blistering-heat-2311.firebaseio.com/chat/-JnDVEJ5IHj-zo1Z9rDh
	//I need the string after chat/ when user wants to remove room
	private String splitUrl(String url) {
		String[] parts = url.split("\\/");
		return parts[parts.length - 1];
	}

	//background thread for removing room every 17 mins. don't really need this
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			//no need to call removeRoom if room has been removed by user
			if(!roomRemoved)
				removeRoom();
		}
	};
}
