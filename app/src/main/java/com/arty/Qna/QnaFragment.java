package com.arty.Qna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arty.Common.CommonFragment;
import com.arty.Main.MainActivity;
import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QnaFragment extends CommonFragment {
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
        ((MainActivity)getActivity()).navigation = "ai";
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
                Log.d(TAG,"[QNA 리스트->상세] 유저가 선택한 질문 문서 ID [" + uuId + "]");
                intent.putExtra("uuId",uuId);
                startActivity(intent);
            }
        });

        getActivity().findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"QNA 추가",Toast.LENGTH_SHORT).show();
                writeQna(v);
            }
        });
    }

    public void writeQna(View v) {
        Intent intent = new Intent(getActivity(), QnaPopup.class);
        startActivity(intent);
    }

    private void drawingRecyclerView() {
        qnaList = new ArrayList<>();
        cRef = mDB.collection(COLLECTION_NAME);
        cRef.orderBy("uploadTime", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            Qna qna = document.toObject(Qna.class);
                            qna.setUuId(document.getId());
                            String time = ((MainActivity)getActivity()).timeComponent.switchTime(qna.getUploadTime());
                            qna.setUploadTime(time);
                            qnaList.add(qna);


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