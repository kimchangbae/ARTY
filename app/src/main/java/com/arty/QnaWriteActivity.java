package com.arty;

import androidx.annotation.Nullable;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class QnaWriteActivity extends AppCompatActivity {
    private static final String TAG = "QnaWriteActivity";

    private final int GET_GALLERY_IMAGE = 200;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private File file;
    private ImageView imgView1, imgView2, imgView3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qna_write);
        inputType();

        File sdcard = Environment.getExternalStorageDirectory();
        String imageFileName = "capture.jpg";
        file = new File(sdcard, imageFileName);

        imgView1 = findViewById(R.id.imageView1);
        imgView2 = findViewById(R.id.imageView2);
        imgView3 = findViewById(R.id.imageView3);
    }

    public void btnTakePhoto(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d(TAG, "[METHOD] btnTakePhoto 실행 여부 --> " + intent.resolveActivity(getPackageManager()));
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult --> requestCode : " + requestCode + " , resultCode : " + resultCode);

        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            /* 이미지 파일의 용량이 너무 커서 그대로 앱에 띄울 경우
             * 메모리 부족으로 비정상 종료될 수 있으므로 크기를 줄여 비트맵으로 로딩한 후 설정 */

            /*
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8; // 1/8 로 크기를 줄임
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            imgView.setImageBitmap(bitmap);
            */

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            //Log.d(TAG, "onActivityResult --> 이미지 여부" + imgView1.getDrawableState());
            imgView1.setImageBitmap(bitmap);

            Log.d(TAG, "onActivityResult --> 이미지 등록");
        }
    }



    public void btnQnaInsert(View v) {
        Intent intent = new Intent(getApplicationContext(), QnaActivity.class);
        startActivityForResult(intent, 1);
    }

    public void inputType() {
        TextView txt_qna_type = findViewById(R.id.txt_qna_type);
        Intent intent = getIntent();
        String data = intent.getStringExtra("type");

        Log.i(TAG, "큐앤에이 타입 : " + data);

        if(data.equals("1")) {
            txt_qna_type.setText("식물이 아파요");
        } else {
            txt_qna_type.setText("식물이 궁금해요");
        }
    }
}
