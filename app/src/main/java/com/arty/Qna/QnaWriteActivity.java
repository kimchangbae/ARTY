package com.arty.Qna;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QnaWriteActivity extends AppCompatActivity {
    static final String TODAY = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));

    private TextView title, qnaType, content;
    private ImageView imgView1;
    Bitmap bitmap;
    private static String userId = "admin";

    private DatabaseReference databaseReference;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qna_write);
        inputType(); // QnaPopup 액티비티에서 식물이아파요 or 식물이 궁금해요 선택한 정보를 화면에 뿌려준다.

        title       = findViewById(R.id.insertTitle);
        qnaType     = findViewById(R.id.insertQnaType);
        content     = findViewById(R.id.insertContent);
        imgView1    = findViewById(R.id.imageView1);

        Button btn_new_qna = findViewById(R.id.btn_new_qna);
        btn_new_qna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 제목 정보를 가져와 입력이 없으면 리턴.
                String str_title = title.getText().toString();
                if(TextUtils.isEmpty(str_title)) {
                    Toast.makeText(QnaWriteActivity.this,"제목을 입력하세요.",Toast.LENGTH_SHORT).show();
                } else {
                    newWriteQna(view);
                    // writeNewQna(view);
                }
            }
        });
    }

    public void newWriteQna(View view) {
        try {
            db = FirebaseFirestore.getInstance();
            //db.collection("QNA").document("admin").collection(getDate);

            Qna qna = new Qna();

            String str_title = title.getText().toString();
            String str_qnaType = qnaType.getText().toString();
            String str_content = content.getText().toString();

            qna.setTitle(str_title);
            qna.setQnaType(str_qnaType);
            qna.setContent(str_content);

            db.collection("QNA")
                    .document("admin")
                    .collection(TODAY)
                    .add(qna)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    toast("글ID : [" +documentReference.getId()+ "]");

                    goToQnaMainActivity();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeNewQna(View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference("QNA");
        Qna qna = new Qna();

        try {
            String str_title    = title.getText().toString();
            String str_qnaType = qnaType.getText().toString();
            String str_content  = content.getText().toString();

            qna.setTitle(str_title);
            qna.setQnaType(str_qnaType);
            qna.setContent(str_content);

            Log.d("writeNewQna", "writeNewQna ---> qna.toString" + qna.toString());

            databaseReference.child(userId).setValue(qna);

            goToQnaMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToQnaMainActivity() {
        Intent intent = new Intent(this, QnaMainActivity.class);
        startActivity(intent);
    }

    public boolean isImgEmpty() {
        boolean result = true;

        // Not Null 은 섬네일 존재함을 의미.
        if(imgView1.getDrawable() != null)
            result = false;

        return result;
    }

    // 사진 촬영 버튼 클릭 이벤트
    public void takingAPicture(View v) {
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
    public void bringAPicture(View v) {
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

    public void inputType() {
        qnaType = findViewById(R.id.insertQnaType);
        Intent intent = getIntent();
        String data = intent.getStringExtra("type");

        if(data.equals("1")) {
            qnaType.setText("식물이 아파요");
        } else {
            qnaType.setText("식물이 궁금해요");
        }
    }

    public void inputImg(Bitmap bitmap) {
        if(imgView1.getDrawable() == null) imgView1.setImageBitmap(bitmap);
    }

    public void deleteImg_1 (View view) {
        imgView1.setImageResource(0);
        Toast.makeText(this,"사진1 삭제",Toast.LENGTH_SHORT).show();
    }

    public void toast(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }
}
