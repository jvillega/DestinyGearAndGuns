package com.cs370.gwtm.destinygearandguns.activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import com.cs370.gwtm.destinygearandguns.R;


public class MainActivity extends ActionBarActivity {

    public final static String ACCOUNT_NAME = "com.cs370.gwtm.destinygearandguns.ACCOUNT_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get handle for username
        View usernameView = findViewById(R.id.username);

        // Find the root view
        View root = usernameView.getRootView();

        // Set the color for the background the set color for username box
        root.setBackgroundColor(getResources().getColor(android.R.color.black));
        usernameView.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    public void searchUser(View view) {
        // Make an intent to essentially throw data so DisplayCharactersActivity
        // can catch with it's own intent
        //Intent myIntent = new Intent(this, DisplayCharactersActivity.class);
        Intent myIntent = new Intent(this, DisplayCharactersActivity.class);

        // Search account text box
        EditText editText = (EditText) findViewById( R.id.username );

        // Store user input from text box
        String findUser = editText.getText().toString().replace(" ", "");

        // Is the button now checked?
        //boolean XBLRadioChecked = ( (RadioButton) findViewById( R.id.xboxlive )).isChecked();
        boolean PSNRadioChecked = ( (RadioButton) findViewById( R.id.PSN )).isChecked();

        int PSNChecked = 2;
        int XBLChecked = 1;

        // Decide which radio button is selected
        if ( PSNRadioChecked )
            myIntent.putExtra("PSNChecked", PSNChecked);
        else
            myIntent.putExtra("XBLChecked", XBLChecked);

        myIntent.putExtra(ACCOUNT_NAME, findUser);
        startActivity(myIntent);
    }

    public void clearUserSearch(View view) {
        EditText editText = (EditText) findViewById( R.id.username  );
        editText.getText().clear();
    }

    // Override Enter/Carriage Return to call searchUser
    // As if clicking the actual Search Button
    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                searchUser(this.findViewById( R.id.username ));
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
