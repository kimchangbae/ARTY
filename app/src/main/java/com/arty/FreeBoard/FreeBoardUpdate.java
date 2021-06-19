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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FreeBoardUpdate extends FreeBoardCommon {
    static final String     TAG = "FreeBoardUpdate";

    private TextView        contentType, content, update_maximum;
    private ImageView       image1, image2, image3;
    public  Uri             imgUri;

    private boolean[]       isImageSwitch;


    private String filePath, documentPath;


    private ProgressDialog progressDialog;
    private Uri[] uris, beforeUris;

    private int  writeStart, writeFinish;

    private FreeBoard board;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeboard_update);

        contentType     = findViewById(R.id.edit_contentType);
        content         = findViewById(R.id.in_content);

        image1          = findViewById(R.id.updateImage1);
        image2          = findViewById(R.id.updateImage2);
        image3          = findViewById(R.id.updateImage3);
        update_maximum  = findViewById(R.id.tv_update_maximum);

        setImageCacheSetting(image1,image2,image3);

        isImageSwitch   = new boolean[UPLOAD_MAXIMUM_SIZE];
        uris            = new Uri[UPLOAD_MAXIMUM_SIZE];
        beforeUris      = new Uri[UPLOAD_MAXIMUM_SIZE];

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("수정중");

        board           = getIntent().getParcelableExtra("board");
        documentPath    = board.getUuId();        // Firestore 문서 경로
        filePath        = board.getFilePath();    // storage 경로

        imageCount = 0;
        update_maximum.setText(imageCount + " / " + UPLOAD_MAXIMUM_SIZE);

        // 기본 정보
        content.setText(board.getContent());

        if(board.getImage1() != null) {
            beforeUris[0] = Uri.parse(board.getImage1());
            uris[0] = Uri.parse(board.getImage1());
            Glide.with(this).load(board.getImage1()).into(image1);
            changeImgUpCount(update_maximum,"up");
        }

        if(board.getImage2() != null) {
            uris[1] = Uri.parse(board.getImage2());
            beforeUris[1] = Uri.parse(board.getImage2());
            Glide.with(this).load(board.getImage2()).into(image2);
            changeImgUpCount(update_maximum,"up");
        }

        if(board.getImage3() != null) {
            uris[2] = Uri.parse(board.getImage3());
            beforeUris[2] = Uri.parse(board.getImage3());
            Glide.with(this).load(board.getImage3()).into(image3);
            changeImgUpCount(update_maximum,"up");
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
    public void onClickUpdateQna(View view) {
        if(isImageEmpty(image1,image2,image3)) {
            Toast.makeText(FreeBoardUpdate.this,"사진은 반드시 1개 이상 등록해야 합니다.",Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressDialog.show();
            progressDialog.setCancelable(false);
            try{
                writeStart = 0;
                writeFinish = getUriCount(uris, beforeUris, isImageSwitch);

                Log.d(TAG,"수정작업횟수 : " + writeFinish);
                updateQna();

                for (int i = 0; i < uris.length; i++) {
                    if(uris[i] != null && !(uris[i].equals(beforeUris[i]))) {
                        Log.d(TAG,i+"번째 수정");
                        updateImage(filePath,documentPath,i);
                    } else if(uris[i] == null && isImageSwitch[i]) {
                        Log.d(TAG,i+"번째 삭제");
                        deleteImage(filePath,documentPath,i);
                    } else {
                        Log.d(TAG,i+"번째 변동없음");
                    }
                }
                if(writeFinish == 0) {
                    progressDialog.dismiss();
                    goToDetailActivity(board.getUuId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                goToMainActivity();
            }
        }
    }

    // 이미지 수정 작업
    private void updateImage(String filePath, String documentPath, int index) {
        StorageReference childRef = storage.getReference().child(filePath+ "/" + index);
        UploadTask uploadTask = childRef.putFile(uris[index]);
        uploadTask
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> t = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return childRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(Task<Uri> task) {
                        Map<String, String> data = new HashMap<>();
                        data.put("image"+(index+1),task.getResult().toString());

                        mDB.collection(COLLECTION_NAME)
                        .document(documentPath)
                        .set(data,SetOptions.merge())
                        .addOnCompleteListener(task1 -> {new CustomListener().finish(); });
                    }
                });
            }
        });
    }

    // 이미지 삭제 작업
    private void deleteImage(String filePath, String documentPath, int index) {
        StorageReference deleteRef  = storage.getReference().child(filePath+ "/" + index);
        deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                Map<String, String> data = new HashMap<>();
                data.put("image"+(index+1),null);

                mDB.collection(COLLECTION_NAME)
                .document(documentPath)
                .set(data,SetOptions.merge())
                .addOnCompleteListener(task1 -> {new CustomListener().finish(); });
            }
        });
    }

    private class CustomListener<Object> implements OnCompleteListener {
        @Override
        public void onComplete(Task task) { }

        public OnCompleteListener<Void> finish() {
            Log.d(TAG,"CustomListener.finish() 가동");
            writeStart++;
            progressDialog.setMessage("업데이트 진행률 [" + writeStart  + "/" + writeFinish + "]");
            Log.d(TAG,"업데이트 진행률 [" + writeStart  + "/" + writeFinish + "]");
            if(writeStart == writeFinish) {
                progressDialog.dismiss();
                goToDetailActivity(board.getUuId());
            }
            return null;
        }
    }

    private void updateQna() {
        try {
            Map<String, Object> map = new HashMap<>();
            String content = this.content.getText().toString();

            map.put("content", content);
            map.put("uploadTime",String.valueOf(System.currentTimeMillis()));

            mDB
            .collection(COLLECTION_NAME)
            .document(this.board.getUuId())
            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if(writeFinish == 0) {
                        progressDialog.dismiss();
                        goToDetailActivity(board.getUuId());
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void onClickTakePicture(View view) {
        if(imageCount >= UPLOAD_MAXIMUM_SIZE) {
            Toast.makeText(getApplicationContext(),"업로드 제한 갯수를 초과하였습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        imgUri = takingPicture();
    }

    public void onClickGetPicture(View view) {
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
