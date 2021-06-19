package com.arty.Qna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class QnaDetail extends QnaCommon implements Serializable {
    static final String TAG = "QnaDetail";

    private TextView    contentType, content, uploadDate;
    private ImageView   image1, image2, image3;
    private Button      btn_update, btn_delete;

    private String              uuId;
    private Qna                 qna;
    private boolean[]           haveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"QnaDetail--ON CREATE");
        setContentView(R.layout.qna_detail);

        mDB     = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth   = FirebaseAuth.getInstance();

        contentType     = findViewById(R.id.tv_fb_dt_userId);
        content         = findViewById(R.id.tv_fb_dt_content);
        uploadDate      = findViewById(R.id.tv_fb_dt_uploadTime);
        image1          = findViewById(R.id.imageDetail_1);
        image2          = findViewById(R.id.imageDetail_2);
        image3          = findViewById(R.id.imageDetail_3);
        btn_update      = findViewById(R.id.btn_update);
        btn_delete      = findViewById(R.id.btn_delete);

        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);

        haveImage = new boolean[UPLOAD_MAXIMUM_SIZE];
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
        uploadDate.setText(qna.getUploadTime());

        if(qna.getImage1() != null) {
            haveImage[0]=true;
            image1.setVisibility(View.VISIBLE);
            Glide.with(this).load(qna.getImage1()).into(image1);
        }

        if(qna.getImage2() != null) {
            image2.setVisibility(View.VISIBLE);
            haveImage[1]=true;
            Glide.with(this).load(qna.getImage2()).into(image2);
        }

        if(qna.getImage3() != null) {
            image3.setVisibility(View.VISIBLE);
            haveImage[2]=true;
            Glide.with(this).load(qna.getImage3()).into(image3);
        }
    }

    public void backToHome(View view) {
        onBackPressed();
        finish();
    }

    public void updateQna(View view) {
        if(presentUserId.equals(qna.getUserId())) {
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
        if(presentUserId.equals(qna.getUserId())) {
            deleteDocument(qna.getUuId());
            for(int i = 0; i<haveImage.length;i++) {
                if(haveImage[i]) {
                    deleteImage(qna.getFilePath(),i);
                }
            }
            goToMainActivity();
        } else{
            Toast.makeText(getApplicationContext(),"권한없음",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"권한없음");
        }
    }

    private void deleteDocument(String uuId) {
        mDB.collection(COLLECTION_NAME)
        .document(uuId)
        .delete()
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"문서 삭제 실패 [문서번호 : " + uuId+ "]");
                Log.d(TAG, e.getMessage());
            }
        });
    }

    private void deleteImage(String filePath, int index) {
        String fullPath = filePath + "/" + index;
        StorageReference ref = storage.getReference().child(fullPath);
        ref.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"이미지 삭제 실패 [이미지 경로 : " + fullPath+ "]");
            }
        });
    }
}