package com.arty.Qna;

import com.arty.Common.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QnaMainActivity extends AppCompatActivity implements Serializable {
    static final String TO_DAY          = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));
    static final String TAG             = "QnaMainActivity";
    static final String COLLECTION_NAME = "QNA_BOARD";

    private RecyclerView                recyclerView;

    private QnaAdapter                  qnaAdapter;
    private RecyclerView.LayoutManager  layoutManager;
    private ArrayList<Qna>              qnaList;

    private FirebaseFirestore           db;
    private CollectionReference         collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        recyclerView = findViewById(R.id.qnaRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        drawingRecyclerView();

        qnaAdapter.setQnaClickListener(new QnaClickListener() {
            @Override
            public void onQnaClick(QnaAdapter.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getApplicationContext(), QnaDetailActivity.class);
                Qna qna = qnaAdapter.getItems(position);
                intent.putExtra("qna",qna);
                startActivity(intent);
            }
        });
    }

    public void drawingRecyclerView() {
        qnaList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection(COLLECTION_NAME);
        collectionReference.orderBy("uploadDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                Qna qna = document.toObject(Qna.class);
                                qna.setUuId(document.getId());
                                qnaList.add(qna);

                                Log.d(TAG, "Call QNA_BOARD--->"+qna.toString());
                            }
                            qnaAdapter.notifyDataSetChanged();
                        }
                    }
                });


        qnaAdapter = new QnaAdapter(qnaList, this);
        recyclerView.setAdapter(qnaAdapter); // 리싸이클러뷰에 어댑터 연결
    }

    public void writeQna(View v) {
        Intent intent = new Intent(this, QnaPopup.class);
        startActivity(intent);
    }
}
