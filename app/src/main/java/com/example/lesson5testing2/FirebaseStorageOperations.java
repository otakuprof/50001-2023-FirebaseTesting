package com.example.lesson5testing2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class FirebaseStorageOperations {

    StorageReference mainStorageReference;
    StorageReference backgroundStorageReference;

    /** Similarly, plan your Storage Database, then instantiate References to it */
    // TODO 14.1 Instantiate References to the Storage database
    FirebaseStorageOperations(){
        mainStorageReference = FirebaseStorage.getInstance().getReference();
        backgroundStorageReference = mainStorageReference.child("/background");
    }

    //TODO 14.2b Take background2.jpg and display it on the ImageView
    void displayBackground2(ImageView imageView, Context context){
        StorageReference bg2storageReference = backgroundStorageReference
                .child("background2.jpg");
        FireBaseUtils.downloadToImageView(context, bg2storageReference, imageView);

    }

    // TODO 14.3b Given a list of images in Storage, pick a random Image uri and display it on the imageView
    void getRandomImageFromBackground(ImageView imageView, Context context){

        Task<ListResult> listResultTask = backgroundStorageReference.listAll();
        listResultTask.addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                ListResult listResult1 = listResultTask.getResult();
                List<StorageReference> refs = listResult1.getItems();
                Random random = new Random();
                int choice = random.nextInt(refs.size());
                StorageReference randomBackgroundImageReference = refs.get(choice);
                FireBaseUtils.downloadToImageView(context,
                        randomBackgroundImageReference, imageView);

            }
        });

    }

    // TODO 14.4b takes a URI and uploads it to Firestore
    void uploadUriToStorage(Context context, Uri photoUri) throws IOException {

        String filename = FireBaseUtils.getFileName(context, photoUri);
        StorageReference imageStorageReference = backgroundStorageReference.child(filename);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(  context.getContentResolver(), photoUri);
        FireBaseUtils.uploadImageToStorage(context, imageStorageReference, bitmap);

    }


}
