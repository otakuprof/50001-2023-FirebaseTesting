package com.example.lesson5testing2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Observer {

    TextView textViewSampleNodeValue;
    ImageView imageViewSatisfied;
    TextView textViewTally;
    final static int REQUEST_IMAGE_GET = 2000;

    //TODO 13 IMPORTANT Get your own google-services.json file
    /* Create your own firebase instance at console.firebase.google.com */

    //TODO 13.0 Plan your database design in advance and create the necessary instance variables
    /* Here's an example. You can see more examples in class FirebaseDatabaseOperations */
    final String SAMPLE_NODE = "pokemon";
    DatabaseReference mRootDatabaseRef;
    DatabaseReference mNodeRefPokemon;


    /** Declared for you to use in TODO 13.6 */
    final String sharedPrefFile = "sharedPref";
    SharedPreferences sharedPreferences;
    final String SATISFIED_KEY = "key_satisfied";
    int satisfiedTallyValue;

    //TODO 13.0 Create your firebase realtime database
    /** FOLLOW THE INSTRUCTIONS IN FIREBASE
     * put the google-services.json in the necessary folder
     * update the project-level and module-level gradle file
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 13.1 Get references to the widgets
        // Already done for you
        textViewSampleNodeValue = findViewById(R.id.textViewSampleNodeValue);
        textViewTally = findViewById(R.id.textViewTally);
        imageViewSatisfied = findViewById(R.id.imageViewSatisfied);

        //TODO 13.2 Get references to the nodes in the database:
        //TODO 13.2a We encapsulate all database operations in FirebaseStorageOperations
        //TODO 13.2b Go to FirebaseStorageOperations

        // TODO 13.5a Listen out for changes in the "satisfied" node and update the TextView
        // See TODO 13.5b in FirebaseDatabaseOperations
        FirebaseDbOpsSubject firebaseDbOpsSubject = new FirebaseDbOpsSubject();
        firebaseDbOpsSubject.registerActivity(this);
        firebaseDbOpsSubject.downloadToObserver();

        //TODO 13.3a When the satisfied button is clicked, push the info to the database.
        // See TODO 13.3b in FirebaseDatabaseOperations
        imageViewSatisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //firebaseDatabaseOperations.pushTimestampToSatisfied();
                firebaseDbOpsSubject.pushTimestampToSatisfied(satisfiedTallyValue);
                Toast.makeText(MainActivity.this, "Thank you",Toast.LENGTH_LONG)
                        .show();
            }
        });

        /*** ------ This section is to give you an example of how the database references work,
         * thus the code is placed here for your convenience ---- */
        //TODO 13.2 Get references to the nodes in the database
        // Already done for you
        /** This is to the root node
         * The rest are to child nodes
         */
        mRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        Log.i("DEBUG", "After 3");

        mNodeRefPokemon = mRootDatabaseRef.child(SAMPLE_NODE);
        Log.i("DEBUG", "After 4");

        //TODO 13.4 Listen out for changes in the "pokemon" node and display a Toast
        /** this is code to update the node */
        mNodeRefPokemon.setValue("Psyduck");
        /** this is code to listen for changes in the value of any particular node */
        mNodeRefPokemon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_LONG).show();
                Log.d("Pokemon",value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("Pokemon","" + error.toString());
            }
        });


        // TODO 14.1a Get a reference to the firebase storage
        final ImageView imageViewBackground = findViewById(R.id.imageViewBackground);
        FirebaseStorageOperations firebaseStorageOperations = new FirebaseStorageOperations();
        Log.i("DEBUG", "After 5");

        /** TODO 14.2 add a onClickListener to imageViewBackground,
         *  so that when the image is click,
         *  download the image "background/background2.jpg" to the imageViewBackground
         */

        /*** TODO 14.3 modify the onClick method definition in 14.2 such that
         * it will first list all the images stored under the "background" folder in the Firebase Storage
         * given the list result, pick one image randomly from the list and download it to imageViewBackround
         */
        imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseStorageOperations.getRandomImageFromBackground(imageViewBackground,
                        MainActivity.this);
            }
        });

        FloatingActionButton uploadButton = findViewById(R.id.uploadButton);

        /*** TODO 14.4a Add a launcher to the imageGallery,
         *  such that the Uri of the photo is retrieved
         *  and uploaded to Storage
         *  ==> TODO 14.4b in FirebaseStorageOperations
         */

        final ActivityResultLauncher<Intent> launcherGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if( result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null){

                            Uri uri= result.getData().getData();
                            try {
                                firebaseStorageOperations.uploadUriToStorage(MainActivity.this, uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
        );


        // TODO 14.4c Add an onClickListener to the uploadButton to create an intent and use the launcher
        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcherGallery.launch(intent);
            }
        });

    }


    //TODO 13.6 If you lose internet access, the data is lost upon the app starting up
    //  Hence, implementing sharedPreferences could be useful

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    // TODO 13.5

    @Override
    public void update(DataSnapshot dataSnapshot) {
        //satisfiedTallyValue = Integer.parseInt(dataSnapshot.getValue().toString());
        satisfiedTallyValue = (int) dataSnapshot.getChildrenCount();
        textViewTally.setText( "" + satisfiedTallyValue);
    }
}