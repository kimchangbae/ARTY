package com.arty.FreeBoard;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class FreeBoardWrite extends FreeBoardCommon {
    static final String TAG = "FreeBoardWrite";

    private StorageReference    storageReference;

    private TextView            contentType, content, upload_maximum;
    private ImageView           image1, image2, image3;
    private Uri                 imgUri;

    private ProgressDialog      progressDialog;
    private Uri[]               uris;

    private int                 writeStart, writeFinish;
    private FreeBoard board;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.freeboard_write);

        contentType     = findViewById(R.id.edit_contentType);
        content         = findViewById(R.id.edit_content);
        image1          = findViewById(R.id.insertImage1);
        image2          = findViewById(R.id.insertImage2);
        image3          = findViewById(R.id.insertImage3);
        upload_maximum  = findViewById(R.id.tv_upload_maximum);

        setImageCacheSetting(image1, image2, image3);

        uris = new Uri[UPLOAD_MAXIMUM_SIZE];

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        imageCount = 0;
        upload_maximum.setText(imageCount + " / " + UPLOAD_MAXIMUM_SIZE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 사진 촬영
        findViewById(R.id.btn_qna_insert_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onClickTakePicture();
            }
        });

        // 사진 불러오기
        findViewById(R.id.btn_qna_insert_call_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGetPicture();
            }
        });
    }

    public void onClickTakePicture() {
        if(imageCount >= UPLOAD_MAXIMUM_SIZE) {
            Toast.makeText(getApplicationContext(),"업로드 제한 갯수를 초과하였습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        imgUri = takingPicture();
    }

    public void onClickGetPicture() {
        if(imageCount >= UPLOAD_MAXIMUM_SIZE) {
            Toast.makeText(getApplicationContext(),"업로드 제한 갯수를 초과하였습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        choosePicture();
    }

    public void onClickWriteQna(View view) {
        if(isImageEmpty(image1,image2,image3)) {
            Toast.makeText(FreeBoardWrite.this,"사진은 반드시 1장이상 등록해야 합니다.",Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressDialog.show();
            progressDialog.setCancelable(false);

            board = new FreeBoard();
            writeStart = 0;
            writeFinish=getUriCount(uris);
            // distributeImage(); // uris 에 저장된 uri 값을 string 형태로 qna 객체에 저장.
            progressDialog.setMessage("저장중");
            String uuidKey = UUID.randomUUID().toString().substring(0,16);

            writeFreeBoard(uuidKey);
            try{
                for (int i = 0; i < uris.length; i++) {
                    if(uris[i] != null) {
                        uploadImage(uuidKey, i);
                    }
                }
            } catch(Exception e) {
                progressDialog.dismiss();
                goToMainActivity();
                e.printStackTrace();
            }
        }
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

    public void writeFreeBoard(String uuId) {
        String filePath = IMAGE_FILE_PRE_PATH + uuId;

        try {
            board.setUuId(uuId);
            board.setContent(content.getText().toString());
            board.setUserId(presentUserId);
            board.setUploadTime(String.valueOf(System.currentTimeMillis()));
            board.setFilePath(filePath);

            mDB.collection(COLLECTION_NAME)
            .document(uuId)
            .set(board, SetOptions.merge())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    Log.d(TAG,"Qna 게시글 작성 완료------------");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(String uuId, int index) {
        String filePath = IMAGE_FILE_PRE_PATH + uuId;
        StorageReference childRef = storage.getReference().child(filePath+ "/" + index);
        UploadTask uploadTask = childRef.putFile(uris[index]);
        uploadTask
        .addOnSuccessListener(taskSnapshot -> {
            Task<Uri> t = uploadTask.continueWithTask(task -> {
                return childRef.getDownloadUrl();
            })
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("image"+(index+1), task.getResult().toString());

                    mDB.collection(COLLECTION_NAME)
                    .document(uuId)
                    .set(data,SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            writeStart++;
                            Log.d(TAG,"Qna "+index+"번 이미지 등록 완료------------");

                            if(writeStart == writeFinish) {
                                progressDialog.dismiss();
                                //goToDetailActivity(uuId);
                                goToMainActivity();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG,e.getMessage());
                }
            });
        });
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
}
