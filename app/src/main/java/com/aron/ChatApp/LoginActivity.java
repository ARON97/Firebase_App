package com.aron.ChatApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private FirebaseAuth mAuth; // 1.
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // TODO: Grab an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance(); // 2. storing a FirebaseAuth object in the mAuth variable
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // 5. TODO: Call attemptLogin() here
        attemptLogin();
    }

    /**
     * Executed when Register button pressed. Implement checking logic for the data entered
     * by the user as well as create the new user in the firebase
    */
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.aron.ChatApp.RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    // TODO: Complete the attemptLogin() method. 3. Triggered when the user presses the login button
    private void attemptLogin() {

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // if the user left these fields blank we do not want to execute anymore code. By using return
        if (email.equals("") || password.equals("")) return;
        Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();

        // TODO: Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // monitor results of signing in
                Log.d("FlashChat", "signInWithEmail() onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d("FlashChat", "Problem signing in: " + task.getException());
                    showErrorDialog("There was a problem signing in");
                } else {
                    // Navigate user to the main chat screen
                    Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

    }

    // TODO: 4. Show error on screen with an alert dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}

/*
 * FIREBASE
 * Settings up firebase - Go to firebase.google.com, sign in, console, add new project 
 * give project a name and change the country
 * ADD FIREBASE TO OUR APP 
 * Click Add Firebase to your Android app
 * Android package name - identifies our app. It can be found in our AndroidManifest.xml
 * After entering our package name click register. 
 * Download google-services.json on the next page(Download config file)
 * Next switch to the Project view in Android Studio to see your project root directory.
 * Move the google-services.json file you just downloaded into your Android app module root directory
 * Add firebase SDK to gradle
 * Add this classpath 'com.google.gms:google-services:3.2.0' under classpath 'com.android.tools.build:gradle:3.1.0'
 * Go to gradle.build(Module:app) add compile 'com.google.firebase:firebase-core:12.0.0' under dependencies
 * Add to the bottom of the file apply plugin: 'com.google.gms.google-services'
 * google-services.json adds android resources when gradle builds the project. The resources are added under build,
 * generated, res, google-services, values.xml
 *
 * After adding firebase to our project. We need to tell firebase how to handle user registration
 * Click on Develop, Authentication(this is where we enable email sign-ups), go to sign-in method tab, enable email/password 
 *
*/