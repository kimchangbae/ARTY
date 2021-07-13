package com.arty.FreeBoard;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class FreeBoardDetail extends FreeBoardCommon implements Serializable {
    static final String TAG = "FreeBoardDetail";

    private TextView    userId, content, uploadTime;
    private ImageView   image1, image2, image3;
    private Button      btn_update, btn_delete;

    private String              uuId;
    private FreeBoard           board;
    private boolean[]           haveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"FreeBoardDetail--ON CREATE");
        setContentView(R.layout.freeboard_detail);


        userId          = findViewById(R.id.tv_qna_detail_userId);
        content         = findViewById(R.id.tv_qna_detail_content);
        uploadTime      = findViewById(R.id.tv_qna_detail_uploadTime);

        image1          = findViewById(R.id.imageDetail_1);
        image2          = findViewById(R.id.imageDetail_2);
        image3          = findViewById(R.id.imageDetail_3);
        btn_update      = findViewById(R.id.btn_detail_update);
        btn_delete      = findViewById(R.id.btn_detail_delete);

        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);

        haveImage = new boolean[UPLOAD_MAXIMUM_SIZE];
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"FreeBoardDetail--ON START");
        super.onStart();

        Intent intent = getIntent();
        if(intent.getStringExtra("uuId") != null) {
            uuId = intent.getStringExtra("uuId");
            Log.d(TAG,"조회할 문서 ID [" + uuId + "]");
            getFreeBoardData(uuId);
        } else {
            Log.d(TAG,"조회할 문서 ID가 존재하지 않습니다.");
        }
    }

    private void getFreeBoardData(String uuId) {
        mDB
        .collection(COLLECTION_NAME)
        .document(uuId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                board = task.getResult().toObject(FreeBoard.class);
                board.setUploadTime(timeComponent.switchTime(board.getUploadTime()));
                drawingIntentData(board);
            }
        }).addOnFailureListener(e -> Log.d(TAG,"데이터 조회 실패 ... " + e.getMessage()));
    }

    private void drawingIntentData(FreeBoard board) {
        content.setText(board.getContent());
        userId.setText(board.getUserId());
        uploadTime.setText(board.getUploadTime());

        if(board.getImage1() != null) {
            haveImage[0]=true;
            image1.setVisibility(View.VISIBLE);
            Glide.with(this).load(board.getImage1()).into(image1);
        }

        if(board.getImage2() != null) {
            image2.setVisibility(View.VISIBLE);
            haveImage[1]=true;
            Glide.with(this).load(board.getImage2()).into(image2);
        }

        if(board.getImage3() != null) {
            image3.setVisibility(View.VISIBLE);
            haveImage[2]=true;
            Glide.with(this).load(board.getImage3()).into(image3);
        }
    }

    public void backToHome(View view) {
        onBackPressed();
        finish();
    }

    public void update(View view) {
        if(presentUserId.equals(board.getUserId())) {
            Intent intent = new Intent(FreeBoardDetail.this, FreeBoardUpdate.class);
            intent.putExtra("board",board);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(),"권한없음",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"권한없음");
        }
    }

    public void delete(View view) {
        if(presentUserId.equals(board.getUserId())) {
            deleteDocument(board.getUuId());
            for(int i = 0; i<haveImage.length;i++) {
                if(haveImage[i]) {
                    deleteImage(board.getFilePath(),i);
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