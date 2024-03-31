package com.example.lesson5testing2;

import android.content.Context;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;

import androidx.annotation.NonNull;

public class FirebaseDbOpsSubject implements Subject {

    DatabaseReference mRootDatabaseRef;
    DatabaseReference mNodeRefSatisfied;
    DatabaseReference mNodeRefSatisfiedNumber;
    final String SATISFIED = "satisfied";
    final String NO_SATISFIED = "number_satisfied";

    Observer observer;

    FirebaseDbOpsSubject(){
        // TODO 13.2b Instantiate References to the Database
        /** (1) Plan your database in advance
         *  (2) Then instantiate references to the nodes here
         */
        mRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mNodeRefSatisfied = mRootDatabaseRef.child(SATISFIED);
        mNodeRefSatisfiedNumber = mNodeRefSatisfied.child(NO_SATISFIED);
    }

    // TODO 13.3b, When the satisfied button is clicked, push the timestamp to the database
    /** executing mNodeRefSatisfied.push() creates child nodes with random ID
     *  mNoteRefTally.child("data") creates a child node if it didn't exist
     *  mNoteRefTally.child("data").setValue( ) assigns a value to the node
     *  explore what happens if you did this subsequently:
     *  mNoteRefTally.child("data").child("data1").setValue() */
    void pushTimestampToSatisfied(int satisfiedTallyValue){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        mNodeRefSatisfied.push().setValue(timestamp.toString());
        mNodeRefSatisfiedNumber.setValue(satisfiedTallyValue+1);
    }

    @Override
    public void registerActivity(Observer observer) {
        this.observer = observer;
    }

    @Override
    public void downloadToObserver() {

        mNodeRefSatisfied.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                observer.update(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
