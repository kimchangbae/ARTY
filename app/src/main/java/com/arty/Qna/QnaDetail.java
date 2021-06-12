package com.arty.Qna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arty.R;
import com.bumptech.glide.Glide;

public class QnaDetail extends AppCompatActivity {
    Qna qna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qna_detail);

        qna = (Qna) getIntent().getParcelableExtra("qna");
        Log.d("QnaDetail","상세페이지 정보 ---> "+qna.toString());

        TextView tv_title       = findViewById(R.id.textView);
        TextView tv_contentType = findViewById(R.id.textView5);
        TextView tv_content     = findViewById(R.id.textView7);
        TextView tv_uploadDate  = findViewById(R.id.textView8);
        ImageView imageView     = findViewById(R.id.img_detail_1);

        tv_title.setText(qna.getTitle());
        tv_contentType.setText(qna.getContentType());
        tv_content.setText(qna.getContent());
        tv_uploadDate.setText(qna.getUploadDate());
        Glide.with(this).load(qna.getImage1()).into(imageView);
    }

    public void backToHome(View view) {
        onBackPressed();
        //Intent intent = new Intent(this, QnaMain.class);
        //startActivity(intent);
        //finish();
    }

    public void updateQna(View view) {
        Intent intent = new Intent(this,QnaUpdate.class);
        intent.putExtra("qna",qna);
        startActivity(intent);
    }

    public void deleteQna(View view) {
        Intent intent = new Intent(this,QnaDelete.class);
        intent.putExtra("qna",qna);
        startActivity(intent);
    }
}