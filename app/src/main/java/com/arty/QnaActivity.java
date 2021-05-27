package com.arty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class QnaActivity extends AppCompatActivity {

    QnaListAdapter qnaListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        RecyclerView recyclerView = findViewById(R.id.qnaRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        qnaListAdapter = new QnaListAdapter();

        qnaListAdapter.addItem(new QnaList("제 마음도 아픈것 같습니다.", "식물이 아파요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));
        qnaListAdapter.addItem(new QnaList("연애가 하고싶어요.", "식물이 궁금해요", "김창배"));

        recyclerView.setAdapter(qnaListAdapter);
    }

    public void qnaWrite(View v) {
        Intent intent = new Intent(getApplicationContext(), QnaPopupActivity.class);
        startActivityForResult(intent, 1);
    }

}