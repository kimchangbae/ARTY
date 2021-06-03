package com.arty.Qna;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.arty.R;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class QnaDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna_detail);

        Qna qna = (Qna) getIntent().getParcelableExtra("qna");
        Log.d("QnaDetailActivity","출력 ---> "+qna.toString());

        TextView tv_title       = findViewById(R.id.textView);
        TextView tv_contentType = findViewById(R.id.textView5);
        TextView tv_content     = findViewById(R.id.textView7);
        ImageView imageView     = findViewById(R.id.imageView2);

        tv_title.setText(qna.getTitle());
        tv_contentType.setText(qna.getContentType());
        tv_content.setText(qna.getContent());
        Glide.with(this).load(qna.getImage1()).into(imageView);

    }
}