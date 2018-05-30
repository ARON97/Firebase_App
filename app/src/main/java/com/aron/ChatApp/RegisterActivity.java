package com.aron.ChatApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    // TODO: Add member variables here:
    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    // Firebase instance variables
    private FirebaseAuth mAuth; // we going to give mAuth a value when the onCreate is called for the register activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirm_password);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.register_username);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        // TODO: Get hold of an instance or an actual object of FirebaseAuth 
        mAuth = FirebaseAuth.getInstance(); // we creating the instance by calling  the static getInstance() from FirebaseAuth class

    }

    // Executed when Sign Up button is pressed.
    public void signUp(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // TODO: Call create FirebaseUser() here
            createFirebaseUser();
        }
    }

    // This method takes string as an input(String email)
    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@"); // checks if the email contains the @ symbol
    }

    private boolean isPasswordValid(String password) {
        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        // check if the confirm field matches the input to the isPasswordValid() method
        String confirmPassword = mConfirmPasswordView.getText().toString();
        /**
         * Suppose both fields are blank and a password consisting of one or two values is not 
         * secured. We should stipulate that the password should have a minimum length in
         * addition to being equal to the confirm password field. The logical AND is used to check
         * if another condition holds true, namely if the password length is over 4
         */
        return confirmPassword.equals(password) && password.length() > 4;
    }

    // TODO: Create a Firebase user
    private void createFirebaseUser() {
        // grabing the email and password. Remember the editText does not return text, so convert it to string
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        /** 
         * using the firebase auth to create a new user
         * createUserWithEmailAndPassword() - this method returns an object of type task. We going to use this method to listen for an event
         * namely if creating a user on firebase was successfully. If the user was created successfully,
         * an event is triggered, we gonna use task to listen to that event
         *
        */
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            // this method will report back to us if creating a user was successfully or not
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
    
                Log.d("FlashChat", "createUser onComplete" + task.isSuccessful());

                if(!task.isSuccessful()) {
                    Log.d("FlashChat", "user creation ");
                    showErrorDialog("Registration attempt failed");
                } else {
                    // we only saving the username after the username has been successfully created on Firebase
                    saveDisplayName();
                    // leave the current screen and head back to the login activity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    finish(); // to close the activity
                    startActivity(intent); // starting the intent
                }
            }
        }); 
    }

    // TODO: Save the display name to Shared Preferences. Storing data locally or on the device
    /**
     * SharedPrefences are a way of saving simple pieces of information as a key value pair
     * A key value pair is a dictionary. A term is a key and a definition is a value
    */
    private void saveDisplayName() {
        // retieve text entered into the mUsernameView and save it to the device
        String displayName = mUsernameView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS, 0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply(); // To make use of our sharedPrefences object we have to inform it that it is going to edited
        

    }

    // TODO: Create an alert dialog to show in case registration failed
    private void showErrorDialog(String message) {
        // the alertDialog.Builder class provides APIs that allow you to create an ALertDialog
        // We creating a slight variation of our own AlertDialog.Builder, compared to the one in Google docs
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



}

