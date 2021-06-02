package com.arty.Qna;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QnaMainActivity extends AppCompatActivity {
    static final String TAG = "QnaMainActivity";
    static final String TODAY = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));

    private RecyclerView                recyclerView;
    //private RecyclerView.Adapter        adapter;
    private QnaAdapter                  qnaAdapter;
    private RecyclerView.LayoutManager  layoutManager;
    private ArrayList<Qna>              qnaList;
    private FirebaseDatabase            database;
    private DatabaseReference           databaseReference;

    private FirebaseFirestore           db;
    private DocumentReference           docuRef;
    private CollectionReference         collRef;

    QnaAdapter ad;

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
                Qna qna = qnaAdapter.getItems(position);
                Log.d(TAG,"선택된 ID-->["+qna.toString()+"]");
            }
        });
    }

    public void qnaClick(Qna qna) {
        Intent intent = new Intent(this, QnaDetailActivity.class);

        startActivity(intent);
    }

    public void drawingRecyclerView() {
        qnaList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        collRef = db.collection("QNA")
                .document("admin")
                .collection(TODAY);
        collRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot qDocSnapshots) {
                for (QueryDocumentSnapshot snapshot : qDocSnapshots) {
                    Log.d(TAG,"List.ID-->["+snapshot.getId()+"]");
                    Qna qna = snapshot.toObject(Qna.class);
                    qna.setSeqId(snapshot.getId());
                    qnaList.add(qna);
                }

                qnaAdapter.notifyDataSetChanged();
            }
        });

        qnaAdapter = new QnaAdapter(qnaList, this);
        recyclerView.setAdapter(qnaAdapter); // 리싸이클러뷰에 어댑터 연결
    }


/*
    //  RealTime Database 버전
    public void oldDrawingRecyclerView() {

        qnaList = new ArrayList<>();
        database = FirebaseDatabase.getInstance(); // 데이터베이스 연동
        databaseReference = database.getReference("QNA"); //DB테이블 연동

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터베이스의 정보를 받아오는 곳
                qnaList.clear(); // ArrayList 초기화

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) { // 반복문을 통해 데이터 리스트 추출
                    Qna qna = dataSnapshot.getValue(Qna.class); // qna 객체에 데이터를 담는다
                    qnaList.add(qna);
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e(TAG,String.valueOf(error.toException()));
            }
        });

        adapter = new QnaAdapter(qnaList, this);
        recyclerView.setAdapter(adapter); // 리싸이클러뷰에 어댑터 연결
    }
*/

    public void writeQna(View v) {
        Intent intent = new Intent(this, QnaPopup.class);
        startActivity(intent);
    }
}
