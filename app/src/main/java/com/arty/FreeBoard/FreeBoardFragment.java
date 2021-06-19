package com.arty.FreeBoard;

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

public class FreeBoardFragment extends CommonFragment {
    static final String COLLECTION_NAME = "FREE_BOARD";
    static final String TAG             = "FreeBoardFragment";


    private RecyclerView.LayoutManager      layoutManager;
    private ArrayList<FreeBoard>            arrayList;
    private RecyclerView                    recyclerView;
    private FreeBoardAdapter                freeBoardAdapter;

    private FirebaseFirestore               mDB;
    private CollectionReference             cRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_freeboard, container, false);
        ((MainActivity)getActivity()).navigation = "free";
        mDB = FirebaseFirestore.getInstance();

        swipeRefreshLayout = rootView.findViewById(R.id.refreshFreeFragment);

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

        freeBoardAdapter.setClickListener(new FreeBoardClickListener() {
            @Override
            public void onItemClick(FreeBoardAdapter.ViewHolder holder, View view, int position) {
                Intent intent = new Intent(getActivity(), FreeBoardDetail.class);
                String uuId = freeBoardAdapter.getUuid(position);
                Log.d(TAG,"[FreeBoard 리스트->상세] 유저가 선택한 질문 문서 ID [" + uuId + "]");
                intent.putExtra("uuId",uuId);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG,"자유게시판 새로고침");
                drawingRecyclerView();
            }
        });

        getActivity().findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"자유게시글 추가",Toast.LENGTH_SHORT).show();
                writeFreeBoard();
            }
        });
    }

    public void writeFreeBoard() {
        Intent intent = new Intent(getActivity(), FreeBoardWrite.class);
        startActivity(intent);
    }

    public void drawingRecyclerView() {
        arrayList = new ArrayList<>();

        cRef = mDB.collection(COLLECTION_NAME);
        cRef.orderBy("uploadTime", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            // TODO 문서를 메인페이지에 뿌려줄 데이터만 추려서 read 하게 수정할 필요가 있다.
                            FreeBoard board = document.toObject(FreeBoard.class);
                            board.setUuId(document.getId());

                            String time = timeComponent.switchTime(board.getUploadTime());
                            board.setUploadTime(time);
                            arrayList.add(board);
                        }
                        freeBoardAdapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });

        freeBoardAdapter = new FreeBoardAdapter(arrayList);
        recyclerView.setAdapter(freeBoardAdapter); // 리싸이클러뷰에 어댑터 연결
        swipeRefreshLayout.setRefreshing(false);
    }
}