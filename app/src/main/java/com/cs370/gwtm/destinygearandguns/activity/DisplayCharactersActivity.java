package com.cs370.gwtm.destinygearandguns.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.cs370.gwtm.destinygearandguns.R;
import com.cs370.gwtm.destinygearandguns.controller.PlayerCharacters;
import com.cs370.gwtm.destinygearandguns.interfaces.CharacterArrayAdapter;
import com.cs370.gwtm.destinygearandguns.interfaces.IPlayerCharacterListener;
import com.cs370.gwtm.destinygearandguns.model.CharacterClass;
import com.cs370.gwtm.destinygearandguns.model.DestinyCharacterInfo;
import com.cs370.gwtm.destinygearandguns.model.DestinyCharacters;
import com.cs370.gwtm.destinygearandguns.model.DestinyMembership;
import com.cs370.gwtm.destinygearandguns.utility.VolleySingleton;

import java.util.ArrayList;

public class DisplayCharactersActivity extends ActionBarActivity implements IPlayerCharacterListener {

    private PlayerCharacters pc;

    // Variables used to test how many characters are pulled at a time
    private String info[] = new String[6];
    private int count = 0;
    private int previousCount = 0;
    private int total = 0;

    private ArrayList<DestinyCharacterInfo> characterInfo = new ArrayList<>();
    private DestinyCharacterInfo destinyCharacterInfo = new DestinyCharacterInfo();

    // Array to store character info for pulled characters so they can be passed to controller
    private DestinyCharacters[] destinyCharacters= new DestinyCharacters[3];

    // Variables used to pass correct characterId to the next activity
    private String[] stringClass = new String[3];
    private int classCount = 0;

    @Override
    public void playerMembershipCallback(DestinyMembership dm) {
        if ( !dm.getMembershipId().isEmpty() )
            pc.pullCharacters(dm.getMembershipType(), dm.getMembershipId());
        //textView.setText("MembershipID is blank");
    }

    @Override
    public void playerCharacterCallback(DestinyCharacters[] dc) {

        // Player's can have up to 3 characters.
        for(int i = 0; i < 3; i++) {
            destinyCharacters[i] = dc[i];
            pc.pullCharacterInfo(dc[i].getMembershipType(), dc[i].getMembershipId(), dc[i].getCharacterId());

        }

    }

    @Override
    public void playerCharacterInfoCallback(DestinyCharacterInfo dcInfo) {

        destinyCharacterInfo = dcInfo;

        pc.pullCharacterClass(dcInfo.getClassHash(), destinyCharacterInfo.getCharacterId());

        // String array to store character level and background path
        info[count] = Integer.toString(destinyCharacterInfo.getCharacterLevel());
        count++;
        info[count] = destinyCharacterInfo.getBackgroundPath();
        count++;

        //Total number of characters pulled
        total++;
    }

    public void playerCharacterClassCallback(final CharacterClass classType) {

        // Store characterId in an array so it can be passed to onItemClick
        stringClass[classCount] = classType.getCharacterId();

        ImageLoader imageLoader;

        imageLoader = VolleySingleton.getInstance(this).getImageLoader();

        if( imageLoader == null ) {
            Log.v("imageLoader: ", "Not getting assigned");
        }

        // Keep track of how many times playerCharacterClassCallback has been called
        previousCount++;

        // Checks amount of pulled characters
        checkAmountOfPulledCharacters(imageLoader, classType);

        CharacterArrayAdapter adapter = new CharacterArrayAdapter(this, characterInfo);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent inventoryIntent = new Intent(DisplayCharactersActivity.this, DisplayInventoryActivity.class);

                // Pass info needed to pull correct character inventory
                inventoryIntent.putExtra("CID", stringClass[position]);
                inventoryIntent.putExtra("MID", destinyCharacters[position].getMembershipId());
                inventoryIntent.putExtra("MT", Integer.toString(destinyCharacters[position].getMembershipType()));

                startActivity(inventoryIntent);
            }
        });
        classCount++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_character_list);

        pc = new PlayerCharacters(this);

        // Get the message from intent
        Intent intent = getIntent();

        // serviceMemberName, Username/Account name
        String serviceMemberName = intent.getStringExtra(MainActivity.ACCOUNT_NAME);

        // service checked either Xbox Live or PSN
        int serviceChecked = intent.getIntExtra("XBLChecked", 0);

        pc.pullMembership(serviceChecked, serviceMemberName);

    }

    public void checkAmountOfPulledCharacters(ImageLoader imageLoader, CharacterClass characterClass) {
        // Test to see if more than one character has been pulled before calling playerCharacterClassCallback
        if(total != previousCount) {
            // Test to see if all characters were pulled before calling playerCharacterClassCallback
            if(count == 6 && previousCount == 1) {
                //Log.v("Character Class: ", characterClass.getCharacterClass());
                characterInfo.add(new DestinyCharacterInfo(characterClass.getCharacterClass(),
                        Integer.parseInt(info[count - 6]),
                        destinyCharacterInfo.getEmblemPath(),
                        info[count-5],
                        imageLoader));
            }

            // Test to see if two characters were pulled before calling playerCharacterClassCallback
            else if(count == 6 && previousCount > 1) {
                //Log.v("Character Class: ", characterClass.getCharacterClass());
                characterInfo.add(new DestinyCharacterInfo(characterClass.getCharacterClass(),
                        Integer.parseInt(info[count - 4]),
                        destinyCharacterInfo.getEmblemPath(),
                        info[count-3],
                        imageLoader));
            }

            else {
                //Log.v("Character Class: ", characterClass.getCharacterClass());
                characterInfo.add(new DestinyCharacterInfo(characterClass.getCharacterClass(),
                        Integer.parseInt(info[count - 4]),
                        destinyCharacterInfo.getEmblemPath(),
                        info[count-3],
                        imageLoader));
            }
        }

        // If only one character was pulled since calling playerCharacterClassCallback
        else {
            //Log.v("Character Class: ", characterClass.getCharacterClass());
            characterInfo.add(new DestinyCharacterInfo(characterClass.getCharacterClass(),
                    Integer.parseInt(info[count - 2]),
                    info[count - 1],
                    destinyCharacterInfo.getBackgroundPath(),
                    imageLoader));
        }
    }
}