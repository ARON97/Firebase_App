package com.aron.ChatApp;
/**
 * This class provides the data to the ListView
*/
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
	// the BaseAdapter acts as template that we going to build our chat app list adapter on top of
	// Acts as a bridge between the chat message data from firebase and the listview that needs to display the messages

	// member variables
	private Activity mActivity;
	private DatabaseReference mDatabaseReference;
	private String mDisplayName;
	/**
	 * DataSnapshot is a type used by Firebase for passing data back to our app
	 * Everytime we read data from Firebase we receive data as a snapshot
	 * An arraylist is used to store a collection of items. An arraylist can grow or
	 * shrink in size. By use DataSnapshot we specify what type of data the array should hold
	*/
	private ArrayList<DataSnapshot> mSnapshotList;

	/**
	 * Retrieve our list of chat messages from Firebase and display them to the user. To detect if there
	 * is a new message we have to use a listener, ChildEventListener. This is the listener that will be notified
	 * if there has been any changes to the database
	*/
	// creating the childListener as a member variable
	private ChildEventListener mListener = new ChildEventListener() {
        // this method gets executed when a new message is added to the database
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        	// when onChildAdded is triggered we will receive a dataSnapshot from Firebase. dataSnapshot comes in a form of JSON
        	mSnapshotList.add(dataSnapshot); // add the dataSnapshot that we received through the callback to our collection of snapshots in the arraylist
        	// calling the add() appends a new item to the array
        	notifyDataSetChanged(); // notify the arraylist that a new item has been added so refresh
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

	// The constructor is a piece of code that creates and configures a chat listAdapter object
	public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {
		// we going to need an activity, database reference and the current users username
		// Populating the member variables using the inputs from the constructor
		mActivity = activity;
		mDisplayName = name;
		mDatabaseReference = ref.child("messages"); // setting it to the message location in our database, thats where an individual chat message will come from
		mDatabaseReference.addChildEventListener(mListener); // attaching our listener to our database reference

		mSnapshotList = new ArrayList<>(); // calling the constructor of the arraylist

	}
	// helper class that acts as a package for an individual row
	static class ViewHolder {
		// the ViewHolder will hold all the views in a single chat row
		TextView authorName;
		TextView body;
		LinearLayout.LayoutParams params;  // we will also want to style our row messages programatically at some point
	}

	// our listView will ask of our chat ListAdapter how many items there are in the list by calling the getCount()
    @Override
    public int getCount() {
        // returning the correct number of items in the ArrayList
        return mSnapshotList.size(); // calling the size() will return the number of items in the arrayList
    }

    // change Object to InstantMessage
    @Override
    public InstantMessage getItem(int position) {
    	// make sure that our adapter can provide the correct message data to the listview
    	DataSnapshot snapshot = mSnapshotList.get(position);
    	// extracting the instant message object out of it. InstantMessage.class converts the JSON from the snapshot into an instant message object
        return snapshot.getValue(InstantMessage.class); 
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
    	// check if there is an existing row that can be reused
    	if (convertView == null) {
    		// we create a new view from an xml file
    		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		convertView = inflater.inflate(R.layout.chat_msg_row, parent, false); // the layoutInflater will create a new view with the inflate method
    		// inflate means pass the XML
    		final ViewHolder holder = new ViewHolder(); // this is the inner helper class that will hold on to all the things that make up an individual chat message row
    		
    		// link up the fields of the ViewHolder to the views of the chat messaging row
    		holder.authorName = (TextView) convertView.findViewById(R.id.author); 
    		holder.authorName = (TextView) convertView.findViewById(R.id.author); 
    		holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams(); // getting the layout parameters
    		/**
    		 * giving the adapter a way of storing our ViewHolder for a short period of time so that we can reuse it later
    		 * reusing the view will allow us to avoid calling that findViewById() again
    		*/
            convertView.setTag(holder); // temporarily storing our ViewHolder in the convertView
    	}
    	
    	// making sure we are showing the correct message text and author in our list item
    	final InstantMessage message = getItem(position); // getting the current instant message in the list

    	// reuses the ViewHolder
    	final ViewHolder holder = (ViewHolder) convertView.getTag(); // retrieving the ViewHolder that was temporarily saved in the convertView

    	// the setTag() and getTag() allows us to recycle our ViewHolders for each row

    	// (ViewHolder) convertView.getTag(); is still going to have the old data in it, so now we going to replace it by replacing the stale data(cache)
    	String author = message.getAuthor();
    	holder.authorName.setText(author); // set the text of the author name in the ViewHolder with the new information
    
    	// determining if the author of the chat message matches the display name
    	boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe, holder); // passing isMe together with the ViewHolder


    	String msg = message.getMessage();
    	holder.body.setText(msg);

    	/**
    	 * The above code is implemented when the user scrolls up and down the list no new row layouts will have to be created
    	 * unnecessarily. The code in our getView updates the contents in the list as the row scrolls into view
    	 */
    	// the convertView represents a list item
        return convertView;
    }

    /**
     * Styling all our chat messages
     * This method will take two inputs- a boolean that shows if the chat message is from the user 
     * or from a chat partner and it will also take a ViewHolder that we need to style as an input
    */
    private void setChatRowAppearance(boolean isItMe, ViewHolder holder) {
    	/*
    	 * Both bubble images end in .9.png - this is the file naming convention for a type of image called a Nine-patch
    	 * A NinePatch image defines a set of pixels which can be stretched in any direction. This is what will allow us to create
    	 * a speech bubble effect and cover the entire chat message regardless if the message text is long or short
    	 * You can create your own NinePatch images by changing the extention to our graphics file and then editing the NinePatch image
    	 * in Android Studio
    	*/

    	// if the message belongs to the user
    	if (isItMe) {
    		holder.params.gravity = Gravity.END; // changing the layout of the entire row to align to the right
    		holder.authorName.setTextColor(Color.GREEN); // setting the text of the authorName to green
			// holder.body.setBackgroundResource(R.drawable.bubble2); // changing the background image resource. Adding the bubbles
    	} else {
    		// if the chat message belongs to someone else
    		holder.params.gravity = Gravity.START; // align the gravity to the left
    		holder.authorName.setTextColor(Color.BLUE); // setting the text of the authorName to blue
			// holder.body.setBackgroundResource(R.drawable.bubble1); // changing the background image resource. Adding the bubbles
    	}

    	// calling setLayoutParams on both the authorName and body
    	holder.authorName.setLayoutParams(holder.params);
    	holder.body.setLayoutParams(holder.params);

    }

    /**
     * Stop checking for new events on the database. The reason we creating this method is to free up resources
     * when we don't need them anymore. This method removes the Firebase Listener method. When the app leaves the
     * foreground we now have a method we can call to stop the Adapter from checking for events from the Firebase Database events
     */
    public void cleanup() {

    	mDatabaseReference.removeEventListener(mListener);
    }

    // Implement the ListAdapter in the MainChatActivity
   
}

/**
 * How the ListView and the Adapter interacts with one another 
 * For example when we click a button our onClickListener sends a message to our android activity
 * In the ListView, the first message is sent to the adapter. The ListVeiw asks the Adapter how many items
 * there are in the List that it needs to display. The listview poses this question by calling the adapters
 * getCount(). The Adapter will then respond with number of items in the list, if there are 17 the getCount() will
 * return 17 items. Next, the ListVeiw will ask for the data for the first item in the list and to get that data the
 * the listView will call the Adapters getView() for the first item. The Adapter will then respond by providing the 
 * listview with data for the first row. After that the listView request the data for the second item on the list, this
 * is the listview calling the adapters getView() again, the Adapter then responds by providing the data for the second
 * row
 *
 *
*/
