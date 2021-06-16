package com.arty.Qna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.arty.Main.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class QnaDelete extends QnaCommon {
    static final String COLLECTION_NAME = "QNA_BOARD";

    private FirebaseFirestore   db;
    private FirebaseStorage     storage;
    private StorageReference    storageReference;
    private Qna                 qna;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.qna_delete);

        qna = getIntent().getParcelableExtra("qna");
        deleteQna();
    }

    private void deleteQna() {
        db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .document(qna.getUuId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        deleteImage();
                    }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"미안...고칠게",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void deleteImage() {
        storage                     = FirebaseStorage.getInstance();
        storageReference            = storage.getReference();

        StorageReference imageUrlRef = storage.getReferenceFromUrl(qna.getImage1());
        String fileName = imageUrlRef.getName();

        StorageReference deleteRef  = storageReference.child(COLLECTION_NAME + "/" + fileName);

        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                goToHome();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"미안...고칠게",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}