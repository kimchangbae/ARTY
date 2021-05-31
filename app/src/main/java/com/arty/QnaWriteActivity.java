package com.arty;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.InputStream;

public class QnaWriteActivity extends AppCompatActivity {
    QnaList qnaList;
    final String TAG = "QnaWriteActivity";
    private ImageView imgView1, imgView2, imgView3;
    Bitmap bitmap;

    private DatabaseReference dbRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qna_write);
        inputType();

        imgView1 = findViewById(R.id.imageView1);
        imgView2 = findViewById(R.id.imageView2);
        imgView3 = findViewById(R.id.imageView3);
    }

    public void bntQnaInsert(View v) {
        //String title    = v.findViewById(R.id.textView3).toString();
        //String type     = v.findViewById(R.id.textView2).toString();
        //String content  = v.findViewById(R.id.textView4).toString();
        try {
            Intent intent = new Intent(this, QnaActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isImgEmpty() {
        boolean result = true;

        // Not Null 은 섬네일 존재함을 의미.
        if(imgView1.getDrawable() != null && imgView2.getDrawable() != null
                && imgView3.getDrawable() != null)
            result = false;

        return result;
    }

    // 사진 촬영 버튼 클릭 이벤트
    public void btnTakePhoto(View v) {
        if(isImgEmpty()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 101);
            }
        } else {
            Toast.makeText(this,"사진은 3개까지만 등록 가능합니다.",Toast.LENGTH_SHORT).show();
        }
    }

    // 사진 가져오기 버튼 클릭 이벤트
    public void btnCallPhoto(View v) {
        // 한정된 이미지가 모두 등록 되었는지 체크
        if(isImgEmpty()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,201);
        } else {
            Toast.makeText(this,"사진은 3개까지만 등록 가능합니다.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 101) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                inputImg(bitmap);
            } else if(requestCode == 201) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                    inputImg(bitmap);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void inputImg(Bitmap bitmap) {
        if(imgView1.getDrawable() == null) imgView1.setImageBitmap(bitmap);
        else if(imgView2.getDrawable() == null) imgView2.setImageBitmap(bitmap);
        else if(imgView3.getDrawable() == null) imgView3.setImageBitmap(bitmap);
    }

    public void inputType() {
        TextView txt_qna_type = findViewById(R.id.txt_qna_type);
        Intent intent = getIntent();
        String data = intent.getStringExtra("type");

        if(data.equals("1")) {
            txt_qna_type.setText("식물이 아파요");
        } else {
            txt_qna_type.setText("식물이 궁금해요");
        }
    }

    public void deleteImg_1 (View view) {
        imgView1.setImageResource(0);
        Toast.makeText(this,"사진1 삭제",Toast.LENGTH_SHORT).show();
    }

    public void deleteImg_2 (View view) {
        imgView2.setImageResource(0);
        Toast.makeText(this,"사진2 삭제",Toast.LENGTH_SHORT).show();
    }

    public void deleteImg_3 (View view) {
        imgView3.setImageResource(0);
        Toast.makeText(this,"사진3 삭제",Toast.LENGTH_SHORT).show();
    }
}
