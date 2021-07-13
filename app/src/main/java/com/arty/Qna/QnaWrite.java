package com.arty.Qna;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QnaWrite extends QnaCommon {
    private static final String TAG = "QnaWrite";
    private QnaViewModel                    qnaViewModel;

    private StorageReference    storageReference;

    private TextView            contentType, content, upload_maximum;
    private ImageView           image1, image2, image3;
    private Button              takePhoto, callPhoto, insert;
    private Uri                 imgUri;

    private ProgressDialog      progressDialog;
    private Uri[]               uris;

    private int                 writeStart, writeFinish, imgCnt;
    private Qna                 qna;
    private String              uuidKey;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qna_write);

        inputType(); // QnaPopup 액티비티에서 식물이아파요 or 식물이 궁금해요 선택한 정보를 화면에 뿌려준다.

        initViewData();

        searchUserId(qnaViewModel);
        qnaViewModel.getUserId().observeForever(userId -> {
            Log.d(TAG,"userId --> " + userId);
            if(userId != null) this.userId = userId;
        });

        // 사진 촬영
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTakePicture();
            }
        });

        // 사진 불러오기
        callPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGetPicture();
            }
        });

        // 질문글 등록
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertQuestion();
            }
        });

        qnaViewModel.getImageUploadCount().observeForever(integer -> {
            Log.d(TAG,"이미지 업로드 카운트 ---> [" +integer+"]");
            if(integer == writeFinish) {
                Log.d(TAG,"질문 등록 완료");
                progressDialog.dismiss();
                goToDetailActivity(uuidKey);
            }
        });

/*
        qnaViewModel.getImgUpResult().observeForever(aBoolean -> {
            Log.d(TAG,"이미지 업로드 결과 리턴");
            if(aBoolean) writeStart++;

            if(writeStart == writeFinish) {
                Log.d(TAG,"질문 등록 완료");
                progressDialog.dismiss();
                goToDetailActivity(uuidKey);
            }
        });
*/
    }

    private void initViewData() {
        qnaViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(QnaViewModel.class);

        contentType     = findViewById(R.id.edit_contentType);
        content         = findViewById(R.id.edit_content);
        image1          = findViewById(R.id.insertImage1);
        image2          = findViewById(R.id.insertImage2);
        image3          = findViewById(R.id.insertImage3);
        upload_maximum  = findViewById(R.id.tv_upload_maximum);

        insert      = findViewById(R.id.btn_insert_question);
        takePhoto   = findViewById(R.id.btn_qna_insert_take_photo);
        callPhoto   = findViewById(R.id.btn_qna_insert_call_photo);

        uris = new Uri[UPLOAD_MAXIMUM_SIZE];

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        imageCount = 0;
        upload_maximum.setText(imageCount + " / " + UPLOAD_MAXIMUM_SIZE);
    }

    // 사진 촬영 완료 or 사진 가져오기 완료 후 이벤트
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            // 사진 촬영
            if(requestCode == 101) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgaeFilePath);
                ExifInterface exifInterface = null;

                try { exifInterface = new ExifInterface(imgaeFilePath); }
                catch (Exception e) { e.printStackTrace(); }

                int exifOrientation, exifDegree;

                if(exifInterface != null) {
                    exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    exifDegree = exifOrientationToDegress(exifOrientation);
                } else { exifDegree = 0; }

                inputImage(bitmap, exifDegree);
            }
            // 다중 이미지 가져오기 TODO 이미지를 최대갯수(3장) 이상 선택하면 알림을 띄우든 하는 작업이 필요하다.
            else if(requestCode == 201) {
                if(data.getClipData() != null) {
                    Log.d(TAG,"다중이미지 가져오기");
                    ClipData clipData = data.getClipData();
                    int itemCount = clipData.getItemCount();
                    if(itemCount > (UPLOAD_MAXIMUM_SIZE - imageCount)) {
                        Toast.makeText(this,"등록가능한 갯수를 초과했습니다.",Toast.LENGTH_SHORT).show();
                    } else {

                        for(int i = 0; i<itemCount; i++) {
                            inputImage(clipData.getItemAt(i).getUri());
                        }
                    }
                } else if(data != null) {
                    inputImage(data.getData());
                }
            }
        }
    }

    private void onClickTakePicture() {
        if(imageCount >= UPLOAD_MAXIMUM_SIZE) {
            Toast.makeText(getApplicationContext(),"업로드 제한 갯수를 초과하였습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        imgUri = takingPicture();
    }

    private void onClickGetPicture() {
        if(imageCount >= UPLOAD_MAXIMUM_SIZE) {
            Toast.makeText(getApplicationContext(),"업로드 제한 갯수를 초과하였습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        choosePicture();
    }

    private void insertQuestion() {
        if(isImageEmpty(image1,image2,image3)) {
            Toast.makeText(this,"사진은 반드시 1장이상 등록해야 합니다.",Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setMessage("질문 등록중...");

            writeStart = 0;
            writeFinish=getUriCount(uris);

            uuidKey = UUID.randomUUID().toString().substring(0,20);
            try{
                insertDocument(uuidKey);

                for (int i = 0; i < uris.length; i++) {
                    if(uris[i] != null) {
                        insertImage(uuidKey, uris[i], i);
                    }
                }
            } catch(Exception e) {
                progressDialog.dismiss();
                goToMainActivity();
                e.printStackTrace();
            }
        }
    }

    private void insertDocument(String uuId) {
        qnaViewModel.insertQuestion(
            uuId
            , contentType.getText().toString()
            , content.getText().toString()
            , userId
            , String.valueOf(System.currentTimeMillis())
            , IMAGE_FILE_PRE_PATH + uuId
        );
    }

    private void insertImage(String uuId, Uri uri, int index) {
        String filePath = IMAGE_FILE_PRE_PATH + "/" + uuId;
        qnaViewModel.insertImage(uuId, filePath, uri, index);
    }

    // 이미지뷰에 가져온 이미지 파일 넣기
    public void inputImage(Bitmap bitmap, int exifDegree) {
        if(image1.getDrawable() == null) {
            image1.setImageBitmap(rotate(bitmap,exifDegree));
            uris[0] = imgUri;
            changeImgUpCount(upload_maximum,"up");
        }
        else if(image2.getDrawable() == null) {
            image2.setImageBitmap(rotate(bitmap,exifDegree));
            uris[1] = imgUri;
            changeImgUpCount(upload_maximum,"up");
        }
        else if(image3.getDrawable() == null) {
            image3.setImageBitmap(rotate(bitmap,exifDegree));
            uris[2] = imgUri;
            changeImgUpCount(upload_maximum,"up");
        }
    }

    public void inputImage(Uri imgUri) {
        if (image1.getDrawable() == null) {
            image1.setImageURI(imgUri);
            uris[0] = imgUri;
            changeImgUpCount(upload_maximum,"up");
        } else if (image2.getDrawable() == null) {
            image2.setImageURI(imgUri);
            uris[1] = imgUri;
            changeImgUpCount(upload_maximum,"up");
        } else if (image3.getDrawable() == null) {
            image3.setImageURI(imgUri);
            uris[2] = imgUri;
            changeImgUpCount(upload_maximum,"up");
        }
    }

    public void deleteImage1 (View view) {
        if(image1.getDrawable() != null) {
            image1.setImageResource(0);
            changeImgUpCount(upload_maximum,"down");
            uris[0] = null;
        }

    }
    public void deleteImage2 (View view) {
        if(image2.getDrawable() != null) {
            image2.setImageResource(0);
            changeImgUpCount(upload_maximum,"down");
            uris[1] = null;
        }
    }
    public void deleteImage3 (View view) {
        if(image3.getDrawable() != null) {
            image3.setImageResource(0);
            changeImgUpCount(upload_maximum,"down");
            uris[2] = null;
        }
    }

    public void inputType() {
        contentType = findViewById(R.id.edit_contentType);
        Intent intent = getIntent();
        String data = intent.getStringExtra("type");

        if(data.equals("1")) {
            contentType.setText("식물이 아파요");
        } else {
            contentType.setText("식물이 궁금해요");
        }
    }
}
