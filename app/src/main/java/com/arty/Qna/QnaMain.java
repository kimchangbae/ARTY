package com.arty.Qna;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.arty.R;
import com.arty.User.MyPage;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QnaMain extends AppCompatActivity {
    static final String TO_DAY          = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));
    static final String TAG             = "QnaMain";
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
        setContentView(R.layout.qna_main);

        recyclerView = findViewById(R.id.qnaView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        drawingRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO 중지 또는 다른 화면에 있다 왔을 때 다시 리스트를 불러오는 동작이 필요
    }

    private long clickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 뒤로가기 버튼 클릭 시
        if(keyCode == KeyEvent.KEYCODE_BACK) {


            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                Toast.makeText(getApplicationContext(), "프로그램이 종료 되었습니다.", Toast.LENGTH_SHORT).show();
                killARTY();

                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"QNA 메인페이지 디스트로이");
    }

    protected void killARTY() {
        // 태스크를 백그라운드로 이동
        moveTaskToBack(true);

        // 액티비티 종료 + 태스크 리스트에서 지우기
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }

        // 앱 프로세스 종료
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void goToMyPage(View view) {
        Intent intent = new Intent(QnaMain.this, MyPage.class);
        startActivity(intent);
        finish();
    }

    public void drawingRecyclerView() {
        qnaList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection(COLLECTION_NAME);
        collectionReference
                .orderBy("uploadDate", Query.Direction.DESCENDING)
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
