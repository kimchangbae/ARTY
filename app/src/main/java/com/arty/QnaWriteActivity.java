package com.arty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class QnaWriteActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna_write);

        Toast.makeText(getApplicationContext(), "QnaPopupActivity Call", Toast.LENGTH_SHORT).show();



        Button button = findViewById(R.id.btn_write);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QnaActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }
}