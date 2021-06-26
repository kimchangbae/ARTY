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
import com.bumptech.glide.Glide;

public class QnaUpdate extends QnaCommon {
    private static final String     TAG = "QnaUpdate";
    private QnaViewModel                    qnaViewModel;

    private TextView        contentType, content, update_maximum;
    private ImageView       image1, image2, image3;
    private Button          takePhoto, callPhoto, update;

    private Uri             imgUri;
    private boolean[]       isImageSwitch;
    private String filePath;
    private Uri[] uris, beforeUris;
    private int  writeStart, writeFinish;

    private ProgressDialog progressDialog;
    private Qna qna;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qna_update);

        qnaViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(QnaViewModel.class);

        contentType     = findViewById(R.id.edit_contentType);
        content         = findViewById(R.id.in_content);
        image1          = findViewById(R.id.updateImage1);
        image2          = findViewById(R.id.updateImage2);
        image3          = findViewById(R.id.updateImage3);
        update_maximum  = findViewById(R.id.tv_update_maximum);
        takePhoto       = findViewById(R.id.btn_qna_update_take_photo);
        callPhoto       = findViewById(R.id.btn_qna_update_call_photo);
        update          = findViewById(R.id.btn_update_question);

        isImageSwitch   = new boolean[UPLOAD_MAXIMUM_SIZE];
        uris            = new Uri[UPLOAD_MAXIMUM_SIZE];
        beforeUris      = new Uri[UPLOAD_MAXIMUM_SIZE];

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("수정중");

        qna             = getIntent().getParcelableExtra("qna");
        filePath        = qna.getFilePath();    // storage 경로

        imageCount = 0;
        update_maximum.setText(imageCount + " / " + UPLOAD_MAXIMUM_SIZE);


        // 기본 정보
        contentType.setText(qna.getContentType());
        content.setText(qna.getContent());

        if(qna.getImage1() != null) {
            Glide.with(this).load(qna.getImage1()).into(image1);
            changeImgUpCount(update_maximum,"up");

            beforeUris[0] = Uri.parse(qna.getImage1());
            uris[0] = Uri.parse(qna.getImage1());
        }

        if(qna.getImage2() != null) {
            Glide.with(this).load(qna.getImage2()).into(image2);
            changeImgUpCount(update_maximum,"up");

            uris[1] = Uri.parse(qna.getImage2());
            beforeUris[1] = Uri.parse(qna.getImage2());
        }

        if(qna.getImage3() != null) {
            Glide.with(this).load(qna.getImage3()).into(image3);
            changeImgUpCount(update_maximum,"up");

            uris[2] = Uri.parse(qna.getImage3());
            beforeUris[2] = Uri.parse(qna.getImage3());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        qnaViewModel.getUserId().observeForever(userId -> {
            Log.d(TAG,"userId --> " + userId);
            if(userId != null) this.userId = userId;
        });

        // 사진 촬영
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTakePicture();
            }
        });

        // 사진 불러오기
        callPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGetPicture();
            }
        });

        // 질문글 업데이트
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion();
            }
        });

        qnaViewModel.getUpdate().observeForever(aBoolean -> {
            if(aBoolean) {
                if(writeFinish == 0) {
                    progressDialog.dismiss();
                    goToDetailActivity(qna.getUuId());
                }
            }
        });

        qnaViewModel.getImgUpResult().observeForever(aBoolean -> {
            Log.d(TAG,"이미지 업로드 결과 리턴");
            if(aBoolean) writeStart++;

            progressDialog.setMessage("업데이트 진행률 [" + writeStart  + "/" + writeFinish + "]");
            Log.d(TAG,"업데이트 진행률 [" + writeStart  + "/" + writeFinish + "]");
            if(writeStart == writeFinish) {
                progressDialog.dismiss();
                goToDetailActivity(qna.getUuId());
            }
        });

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

    // 업데이트
    public void updateQuestion() {
        if(isImageEmpty(image1,image2,image3)) {
            Toast.makeText(QnaUpdate.this,"사진은 반드시 1개 이상 등록해야 합니다.",Toast.LENGTH_SHORT).show();
            return;
        } else {
            searchUserId(qnaViewModel);

            progressDialog.show();
            progressDialog.setCancelable(false);
            try{
                writeStart = 0;
                writeFinish = getUriCount(uris, beforeUris, isImageSwitch);

                updateDocument();

                for (int i = 0; i < uris.length; i++) {
                    if(uris[i] != null && !(uris[i].equals(beforeUris[i]))) {
                        qnaViewModel.insertImage(qna.getUuId(),filePath,uris[i], i);
                    } else if(uris[i] == null && isImageSwitch[i]) {
                        qnaViewModel.deleteImage(qna.getUuId(),filePath, i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                goToMainActivity();
            }
        }
    }

    private void updateDocument() {
        qnaViewModel.updateQuestion(
            qna.getUuId()
            , this.content.getText().toString()
            , String.valueOf(System.currentTimeMillis())
        );
    }

    // 사진촬영으로 가져온 이미지 파일 넣기
    public void inputImage(Bitmap bitmap, int exifDegree) {
        if(image1.getDrawable() == null) {
            image1.setImageBitmap(rotate(bitmap,exifDegree));
            changeImgUpCount(update_maximum,"up");
            uris[0] = imgUri;
        }
        else if(image2.getDrawable() == null) {
            image2.setImageBitmap(rotate(bitmap,exifDegree));
            changeImgUpCount(update_maximum,"up");
            uris[1] = imgUri;
        }
        else if(image3.getDrawable() == null) {
            image3.setImageBitmap(rotate(bitmap,exifDegree));
            changeImgUpCount(update_maximum,"up");
            uris[2] = imgUri;
        }
    }

    public void inputImage(Uri imgUri) {
        if (image1.getDrawable() == null) {
            image1.setImageURI(imgUri);
            uris[0] = imgUri;
            changeImgUpCount(update_maximum,"up");
        } else if (image2.getDrawable() == null) {
            image2.setImageURI(imgUri);
            uris[1] = imgUri;
            changeImgUpCount(update_maximum,"up");
        } else if (image3.getDrawable() == null) {
            image3.setImageURI(imgUri);
            uris[2] = imgUri;
            changeImgUpCount(update_maximum,"up");
        }
    }

    public void invisibleImage1 (View view) {
        if(image1.getDrawable() != null) {
            image1.setImageResource(0);
            isImageSwitch[0] = true;
            uris[0] = null;
            changeImgUpCount(update_maximum,"down");
        }
    }
    public void invisibleImage2 (View view) {
        if(image2.getDrawable() != null) {
            image2.setImageResource(0);
            isImageSwitch[1] = true;
            uris[1] = null;
            changeImgUpCount(update_maximum,"down");
        }
    }
    public void invisibleImage3 (View view) {
        if(image3.getDrawable() != null) {
            image3.setImageResource(0);
            isImageSwitch[2] = true;
            uris[2] = null;
            changeImgUpCount(update_maximum,"down");
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

    // 업데이트 취소 버튼 클릭 이벤트
    public void updateCancel(View view) {
        onBackPressed();
    }

    public void doNotouch(View view) {
        Log.d(TAG,"왜 꺼지는거야");
    }

}
