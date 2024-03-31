package com.example.lesson5testing2;

import com.google.firebase.database.DataSnapshot;

public interface Observer {
    void update(DataSnapshot dataSnapshot);
}
