package com.arty.FreeBoard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.Qna.Qna;
import com.arty.Qna.QnaAdapter;
import com.arty.Qna.QnaClickListener;
import com.arty.Qna.QnaDetail;
import com.arty.Qna.QnaPopup;
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

public class FreeBoardFragment extends Fragment {
    static final String COLLECTION_NAME = "QNA_BOARD";
    static final String TAG             = "FreeBoardFragment";



    private RecyclerView.LayoutManager      layoutManager;
    private ArrayList<FreeBoard>            arrayList;
    private RecyclerView                    recyclerView;
    private FreeBoardAdapter                adapter;

    private FirebaseFirestore               mDB;
    private CollectionReference             cRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_freeboard, container, false);

        mDB = FirebaseFirestore.getInstance();

        recyclerView = rootView.findViewById(R.id.freeBoardView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        drawingRecyclerView();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.setClickListener(new FreeBoardClickListener() {
            @Override
            public void onItemClick(FreeBoardAdapter.ViewHolder holder, View view, int position) {
                // Intent intent = new Intent(getActivity(), QnaDetail.class);
                FreeBoard freeBoard = adapter.getItems(position);
                // intent.putExtra("qnaFilePath",qna.getFilePath());
                // startActivity(intent);
            }
        });

        Button btn_add = getActivity().findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"자유게시글 추가",Toast.LENGTH_SHORT).show();
                writeFreeBoard(v);
            }
        });
    }

    public void writeFreeBoard(View v) {
        Intent intent = new Intent(getActivity(), QnaPopup.class);
        startActivity(intent);
    }

    public void drawingRecyclerView() {
        arrayList = new ArrayList<>();

        cRef = mDB.collection(COLLECTION_NAME);
        cRef.orderBy("uploadDate", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            // TODO 문서를 메인페이지에 뿌려줄 데이터만 추려서 read 하게 수정할 필요가 있다.
                            FreeBoard board = document.toObject(FreeBoard.class);
                            board.setUuId(document.getId());
                            arrayList.add(board);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });

        adapter = new FreeBoardAdapter(arrayList);
        recyclerView.setAdapter(adapter); // 리싸이클러뷰에 어댑터 연결
    }
}