package com.example.matt.tapd_bar;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;


public class DispatchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
          //  Intent intent = new Intent(this, FindActivity.class);
           // startActivity(intent);
            startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }

}
