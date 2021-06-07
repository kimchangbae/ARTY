package com.arty.Qna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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

public class QnaWriteActivity extends AppCompatActivity {
    private static String userId = "admin";
    static final String COLLECTION_NAME = "QNA_BOARD";


    private TextView    title, contentType, content;
    private ImageView   image1;
    public  Uri         imgUri, downloadURI;

    private FirebaseFirestore   db;
    private FirebaseStorage     storage;
    private StorageReference    storageReference;

    String currentPhotoPath;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qna_write);
        inputType(); // QnaPopup 액티비티에서 식물이아파요 or 식물이 궁금해요 선택한 정보를 화면에 뿌려준다.

        title           = findViewById(R.id.in_title);
        contentType     = findViewById(R.id.in_contentType);
        content         = findViewById(R.id.edit_in_content);
        image1          = findViewById(R.id.imageView1);

        image1.setDrawingCacheEnabled(true);
        image1.buildDrawingCache();

        findViewById(R.id.btn_update_qna).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image1.getDrawable() == null) {
                    Toast.makeText(QnaWriteActivity.this,"사진은 반드시 등록해야 합니다.",Toast.LENGTH_SHORT).show();
                } else {
                    // 원인은 모르지만 uploadImage() 메서드와 writeQna() 메서드를 각각 실행하면
                    // 이미지만 업로드 되고 문서는 업로드가 안되는 현상이 있다.
                    // 그래서 uploadImage()의 onComplete 내부에서 실행되도록 했다.
                    uploadImage();
                }
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    public void uploadImage() {
        UploadTask uploadTask;
        final String randomKey = UUID.randomUUID().toString();

        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference();

        StorageReference childRef = storageReference.child(COLLECTION_NAME +"/"+randomKey);
        uploadTask = childRef.putFile(imgUri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                Log.d("QnaWriteActivity", "Upload is " + progress + "% done");
                Toast.makeText(getApplicationContext(),"사진을"+progress+"%업로드 중입니다.",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return childRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            downloadURI = task.getResult();
                            writeQna();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void writeQna() {
        db = FirebaseFirestore.getInstance();
        Qna qna = new Qna();

        try {
            String str_title        = title.getText().toString();
            String str_contentType  = contentType.getText().toString();
            String str_content      = content.getText().toString();
            String uploadDate       = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

            qna.setTitle(str_title);
            qna.setContentType(str_contentType);
            qna.setContent(str_content);
            qna.setImage1(downloadURI.toString());
            qna.setUserId(userId);
            qna.setUploadDate(uploadDate);

            db.collection(COLLECTION_NAME)
                    .add(qna)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            goToQnaMainActivity();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToQnaMainActivity() {
        Intent intent = new Intent(this, QnaMainActivity.class);
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
                    imgUri = photoURI;
                    startActivityForResult(intent,101);
                }

            }
        } else {
            Toast.makeText(this,"등록불가",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "등록불가", Toast.LENGTH_SHORT).show();
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
                imgUri = data.getData();
                image1.setImageURI(imgUri);
            }
        }
    }

    // 이미지뷰에 가져온 이미지 파일 넣기
    public void inputImg(Bitmap bitmap) {
        if(image1.getDrawable() == null) image1.setImageBitmap(bitmap);
    }

    public void inputType() {
        contentType = findViewById(R.id.in_contentType);
        Intent intent = getIntent();
        String data = intent.getStringExtra("type");

        if(data.equals("1")) {
            contentType.setText("식물이 아파요");
        } else {
            contentType.setText("식물이 궁금해요");
        }
    }

    public void deleteImage1 (View view) {
        image1.setImageResource(0);
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
}
