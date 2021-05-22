package com.arty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QnaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

    }

    public void qnaWrite(View v) {
        Intent intent = new Intent(getApplicationContext(), QnaPopupActivity.class);
        startActivityForResult(intent, 1);
    }

}