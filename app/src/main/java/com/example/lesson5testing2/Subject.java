package com.example.lesson5testing2;

import android.content.Context;

public interface Subject {

    void registerActivity(Observer observer);

    void downloadToObserver();
}
