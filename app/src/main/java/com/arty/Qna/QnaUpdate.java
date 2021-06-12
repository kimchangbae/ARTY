package com.arty.Qna;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.arty.Main.MainActivity;
import com.arty.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class QnaUpdate extends AppCompatActivity {
    boolean imageSwitch = false;
    private static String userId = "admin";
    static final String COLLECTION_NAME = "QNA_BOARD";

    Qna qna;

    private TextView    title, contentType, content;
    private ImageView   image1;
    public  Uri         imgURI, downloadURI;

    private FirebaseFirestore   db;
    private FirebaseStorage     storage;
    private StorageReference    storageReference;

    String currentPhotoPath;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qna_update);

        title           = findViewById(R.id.edit_title);
        contentType     = findViewById(R.id.edit_contentType);
        content         = findViewById(R.id.in_content);
        image1          = findViewById(R.id.imageView1);

        image1.setDrawingCacheEnabled(true);
        image1.buildDrawingCache();

        qna = (Qna) getIntent().getParcelableExtra("qna");
        Log.d("QnaUpdate","업데이트 페이지 정보 ---> "+qna.toString());
        Log.d("QnaUpdate","업데이트 페이지 이미지 스위치 ---> "+imageSwitch);

        // 최초 정보
        title.setText(qna.getTitle());
        contentType.setText(qna.getContentType());
        content.setText(qna.getContent());
        Glide.with(this).load(qna.getImage1()).into(image1);

        findViewById(R.id.btn_write_qna).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image1.getDrawable() == null) {
                    Toast.makeText(QnaUpdate.this,"사진은 반드시 등록해야 합니다.",Toast.LENGTH_SHORT).show();
                } else if(imageSwitch) {
                    // 기존 이미지 삭제 및 신규 이미지 업로드
                    deleteImage();
                    uploadImage();
                }else {
                    // 기존 이미지 변경 없이 내용만 수정
                    updateQna();
                }
            }
        });
    }

    public void uploadImage() {
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference();
        final String randomKey = UUID.randomUUID().toString();

        StorageReference childRef = storageReference.child(COLLECTION_NAME +"/"+randomKey);
        UploadTask uploadTask;
        uploadTask = childRef.putFile(imgURI);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                Log.d("QnaWrite", "Upload is " + progress + "% done");
                Toast.makeText(getApplicationContext(),"사진을"+progress+"%업로드 중입니다.",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"미안...고칠게",Toast.LENGTH_SHORT).show();
                            throw task.getException();
                        }
                        return childRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            // 이미지 수정이 완료 되면 Cloud Firestore에 글 수정 시작
                            downloadURI = task.getResult();
                            updateQna();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(getApplicationContext(),"미안...고칠게",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    public void updateQna() {
        db = FirebaseFirestore.getInstance();
        Qna qna = new Qna();

        try {
            String str_title        = title.getText().toString();
            String str_contentType  = contentType.getText().toString();
            String str_content      = content.getText().toString();
            String uploadDate       = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

            qna.setUuId(this.qna.getUuId());
            qna.setTitle(str_title);
            qna.setContentType(str_contentType);
            qna.setContent(str_content);
            if(imageSwitch) {
                qna.setImage1(downloadURI.toString());
            } else {
                qna.setImage1(this.qna.getImage1());
            }
            qna.setUserId(userId);
            qna.setUploadDate(uploadDate);

            db.collection(COLLECTION_NAME)
                    .document(this.qna.getUuId())
                    .set(qna)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            goToQnaMainActivity();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToQnaMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 사진 촬영 버튼 클릭 이벤트
    public void takingPicture(View v) {
        if(isImageEmpty()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(intent.resolveActivity(getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }

                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,"com.arty.Qna.fileprovider",photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    imgURI = photoURI;
                    startActivityForResult(intent,101);
                }

            }
        } else {
            Toast.makeText(this,"사진은 3개까지만 등록 가능합니다.",Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp        = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName    = "JPEG_" + timeStamp + "_";
        File storageDir         = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image              = File.createTempFile(imageFileName,".jpg",storageDir);

        currentPhotoPath        = image.getAbsolutePath();
        return image;
    };

    // 사진 가져오기 버튼 클릭 이벤트
    public void choosePicture(View v) {
        // 한정된 이미지가 모두 등록 되었는지 체크
        if (isImageEmpty()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 201);
        } else {
            Toast.makeText(this, "사진은 3개까지만 등록 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 사진 촬영 완료 or 사진 가져오기 완료 후 이벤트
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 101) {
                // 사진 촬영
                try {
                    InputStream in = new FileInputStream(currentPhotoPath);
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    inputImg(bitmap);
                } catch (FileNotFoundException fileNotFoundException) {
                    Toast.makeText(getApplicationContext(),"미안...고칠게",Toast.LENGTH_SHORT).show();
                    fileNotFoundException.printStackTrace();
                }


            } else if(requestCode == 201) {
                // 사진 불러오기
                imgURI = data.getData();
                image1.setImageURI(imgURI);
            }
        }
    }

    // 이미지뷰에 가져온 이미지 파일 넣기
    public void inputImg(Bitmap bitmap) {
        if(image1.getDrawable() == null) image1.setImageBitmap(bitmap);
    }

    public void invisibleImage1 (View view) {
        image1.setImageResource(0);
        imageSwitch = true;
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
                Log.d("QanUpdate","사진 참 예뻤어요...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("QanUpdate","사진 참 예뻤어요...");
                e.printStackTrace();
            }
        });
    }

    public void toast(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    public boolean isImageEmpty() {
        boolean result = true;

        // Not Null 은 섬네일 존재함을 의미.
        if(image1.getDrawable() != null)
            result = false;

        return result;
    }

    // 업데이트 취소 버튼 클릭 이벤트
    public void updateCancel(View view) {
        onBackPressed();
        //Intent intent = new Intent(this, QnaMain.class);
        //startActivity(intent);
        //finish();
    }
}
