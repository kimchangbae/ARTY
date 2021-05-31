package com.arty;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class QnaActivity extends AppCompatActivity {
    static final String TAG = "QnaActivity.Method";

    QnaListAdapter qnaListAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        // 이부분 오류 뜨자나여...
        recyclerView = findViewById(R.id.qnaRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        qnaListAdapter = new QnaListAdapter();

        qnaListAdapter.addItem(new QnaList("이것은 테스트용 게시글 입니다.", "식물이 아파요", "김창배"));

        recyclerView.setAdapter(qnaListAdapter);
    }

    public void qnaWrite(View v) {
        Intent intent = new Intent(this, QnaPopupActivity.class);
        startActivity(intent);
    }

}