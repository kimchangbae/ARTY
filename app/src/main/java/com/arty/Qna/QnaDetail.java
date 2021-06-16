package com.arty.Qna;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.Main.MainActivity;
import com.arty.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class QnaDetail extends QnaCommon implements Serializable {
    static final String TAG = "QnaDetail";

    private TextView    contentType, content, uploadDate;
    private ImageView   image1, image2, image3;
    private Button      btn_update, btn_delete;

    private FirebaseFirestore   mDB;
    private FirebaseAuth        mAuth;
    private FirebaseStorage     storage;
    private String              uuId;
    private Qna                 qna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"QnaDetail--ON CREATE");
        setContentView(R.layout.qna_detail);

        mDB     = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth   = FirebaseAuth.getInstance();

        contentType     = findViewById(R.id.textView5);
        content         = findViewById(R.id.textView7);
        uploadDate      = findViewById(R.id.textView8);
        image1          = findViewById(R.id.imageDetail_1);
        image2          = findViewById(R.id.imageDetail_2);
        image3          = findViewById(R.id.imageDetail_3);
        btn_update      = findViewById(R.id.btn_update);
        btn_delete      = findViewById(R.id.btn_delete);

        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"QnaDetail--ON START");
        super.onStart();

        Intent intent = getIntent();
        if(intent.getStringExtra("uuId") != null) {
            uuId = intent.getStringExtra("uuId");
            Log.d(TAG,"조회할 문서 ID [" + uuId + "]");
            getQnaDataFromDB(uuId);
        } else {
            Log.d(TAG,"조회할 문서 ID가 존재하지 않습니다.");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"QnaDetail--ON RESUME");
        // TODO QnaInsert 또는 Update 실행 시 완료 되는 시점에 Detail 데이터 재호출이 필요
    }

    private void getQnaDataFromDB(String uuId) {
        mDB
        .collection(COLLECTION_NAME)
        .document(uuId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                qna = task.getResult().toObject(Qna.class);
                drawingIntentData(qna);
            }
        }).addOnFailureListener(e -> Log.d(TAG,"데이터 조회 실패 ... " + e.getMessage()));
    }

    private void drawingIntentData(Qna qna) {
        content.setText(qna.getContent());
        contentType.setText(qna.getContentType());
        uploadDate.setText(qna.getUploadDate());

        if(qna.getImage1() != null) {
            image1.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage1()).into(image1);
        }

        if(qna.getImage2() != null) {
            image2.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage2()).into(image2);
        }

        if(qna.getImage3() != null) {
            image3.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage3()).into(image3);
        }
    }

    public void backToHome(View view) {
        onBackPressed();
        finish();
    }

    public void updateQna(View view) {
        if(mAuth.getTenantId().equals(qna.getUserId())) {
            Intent intent = new Intent(QnaDetail.this, QnaUpdate.class);
            intent.putExtra("qna",qna);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(),"권한없음",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"권한없음");
        }

    }

    public void deleteQna(View view) {
        if(mAuth.getTenantId().equals(qna.getUserId())) {
            String deleteDocument = qna.getUuId();
            String deleteFilePath = qna.getFilePath();

            mDB.collection(COLLECTION_NAME)
            .document(deleteDocument)
            .delete()
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG,"문서 삭제 실패 [문서번호 : " + deleteDocument+ "]");
                    Log.d(TAG, e.getMessage());
                }
            });

            StorageReference ref = storage.getReference().child(deleteFilePath);
            ref.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG,"이미지 삭제 실패 [이미지 경로 : " + deleteFilePath+ "]");
                }
            });

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(getApplicationContext(),"권한없음",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"권한없음");
        }

    }
}