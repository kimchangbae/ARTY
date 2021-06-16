package com.arty.Qna;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QnaFragment extends Fragment {
    static final String COLLECTION_NAME = "QNA_BOARD";
    static final String TAG             = "QnaFragment";

    private SwipeRefreshLayout              swipeRefreshLayout = null;
    private QnaAdapter                      qnaAdapter;
    private RecyclerView.LayoutManager      layoutManager;
    private ArrayList<Qna>                  qnaList;
    private RecyclerView                    recyclerView;

    private FirebaseFirestore               mDB;
    private CollectionReference             cRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_qna, container, false);

        mDB = FirebaseFirestore.getInstance();

        swipeRefreshLayout = rootView.findViewById(R.id.refreshQnaFragment);

        recyclerView = rootView.findViewById(R.id.freeBoardView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        drawingRecyclerView();

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG,"큐앤에이 onStart");
        super.onStart();
        
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG,"QNA 게시판 새로고침");
                drawingRecyclerView();

            }
        });

        qnaAdapter.setQnaClickListener(new QnaClickListener() {
            @Override
            public void onQnaClick(QnaAdapter.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getActivity(), QnaDetail.class);
                String uuId = qnaAdapter.getUuid(position);
                Log.d(TAG,"[리스트->상세] 유저가 선택한 질문 문서 ID [" + uuId + "]");
                intent.putExtra("uuId",uuId);
                startActivity(intent);
            }
        });

        Button btn_add = getActivity().findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"QNA 추가",Toast.LENGTH_SHORT).show();
                writeQna(v);
            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG,"큐앤에이 onResume");
        super.onResume();

        // resumeDB();
    }

    private void resumeDB() {
        cRef = mDB.collection(COLLECTION_NAME);
        cRef.orderBy("uploadDate", Query.Direction.DESCENDING)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot document : value) {
                    Qna qna = document.toObject(Qna.class);
                    qna.setUuId(document.getId());
                    qnaList.add(qna);
                }
                qnaAdapter.notifyDataSetChanged();
            }
        });

        // qnaAdapter = new QnaAdapter(qnaList);
        recyclerView.setAdapter(qnaAdapter); // 리싸이클러뷰에 어댑터 연결
    }

    @Override
    public void onDetach() {
        Log.d(TAG,"큐앤에이 onDetach");
        super.onDetach();
    }


    public void writeQna(View v) {
        Intent intent = new Intent(getActivity(), QnaPopup.class);
        startActivity(intent);
    }

    public void drawingRecyclerView() {
        qnaList = new ArrayList<>();

        cRef = mDB.collection(COLLECTION_NAME);
        cRef.orderBy("uploadDate", Query.Direction.DESCENDING)
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
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });

        qnaAdapter = new QnaAdapter(qnaList);
        recyclerView.setAdapter(qnaAdapter); // 리싸이클러뷰에 어댑터 연결
        swipeRefreshLayout.setRefreshing(false);
    }
}