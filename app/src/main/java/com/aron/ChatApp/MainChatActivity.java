package com.aron.ChatApp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference; // 8. to talk the Firebase we need a reference called the data days reference
    private ChatListAdapter mAdapter; // holds on to the adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // 7. TODO: Set up the display name and get the Firebase reference
        setupDisplayName();
        /**
         * 9. calling a static getInstance() to get a Firebase database object 
         * and then we will call the getReference() on the Firebase reference to obtain a database reference object
         * The reason we need a database reference object is because a database reference object represents a particular location in your database
         * and be used for reading or writing data to that Database location
        */
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        // TODO: 10. Send the message when the "enter" button is pressed on the soft keyboard
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true; // indicates that the event has been handled, the mSendButton is not necessary after the execution of this code
            }
        });

        // TODO: 11. Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    // TODO: 6. Retrieve the display name from the Shared Preferences. Retrieve the Locally Stored Data
    private void setupDisplayName() {
        // the locally stored data on the device is used to autocomplete text fields
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);

        // we'll store the information that we going to pull out of the shared preferences in our mDisplayName
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        // if there is no display name saved under the shared preferences
        if (mDisplayName == null) mDisplayName = "Anonymous";
        /**
         * The mDisplayName is now anonymous even though the user has logged in with same credentials
         * Because the data saved in the sharedPreferences does not survive a fresh install of the app
        */
    }


    private void sendMessage() {

        Log.d("FlashChat", "I sent something!"); // 12. monitoring when the sendMessage() has been executed in mInputText or mSendButton
        // 18. TODO: Grab the text the user typed in and push the message to Firebase
        // get hold off what the user typed in and store it in a string variable
        String input = mInputText.getText().toString();
        // if the users chat message is not empty
        if (!input.equals("")) {
            InstantMessage chat = new InstantMessage(input, mDisplayName); // an instant message object and supplying the users input and display name to the constructor
            /*
             * saving the messaging to the cloud
             * mDatabaseReference is a particular reference in our database
             * the database reference child method specifies that all our chat messages are to be stored in a place called messages
             * push() is to get a reference to this child reference
             * setValue() is to write the data in our chat object to the database at this location. The instruction that
             * commits the data to the database
            */
            mDatabaseReference.child("messages").push().setValue(chat);
            mInputText.setText(""); // clear the message box when the user has pressed sent
        }
    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart() {
        // onStart() gets called after onCreate()
        super.onStart();
        mAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName); //creating a new ChatListAdapter and apply three inputs to the constructor
        mChatListView.setAdapter(mAdapter); // hooking up our adapter to the listView. The listView now knows which adapter it should talk to
    }

    // gets called when the app is no more visible to the user
    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.
        mAdapter.cleanup(); // Best practice- freeing up resources in the Android Lifecycle when the app is no longer needed
    }

}
