package com.arty.Qna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arty.Common.CommonFragment;
import com.arty.Main.MainActivity;
import com.arty.R;

public class QnaFragment extends CommonFragment {
    private static final String TAG             = "QnaFragment";

    private SwipeRefreshLayout              swipeRefreshLayout = null;
    private QnaAdapter                      qnaAdapter;
    private RecyclerView.LayoutManager      layoutManager;
    private RecyclerView                    recyclerView;
    private QnaViewModel                    qnaViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"QNA onCreateView-----------------------");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_qna, container, false);
        ((MainActivity)getActivity()).navigation = "ai";

        swipeRefreshLayout = rootView.findViewById(R.id.refreshQnaFragment);

        recyclerView = rootView.findViewById(R.id.freeBoardView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(layoutManager);

        qnaViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(QnaViewModel.class);

        qnaAdapter = new QnaAdapter(null);

        qnaViewModel.selectQnaList();

        qnaViewModel.getQnaLists().observeForever(listData -> {
            Log.d(TAG,"listData --> " + listData);

                qnaAdapter.notifyDataSetChanged();
                qnaAdapter.setQnaList(listData);
                recyclerView.setAdapter(qnaAdapter); // 리싸이클러뷰에 어댑터 연결
                swipeRefreshLayout.setRefreshing(false);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"QNA onViewCreated--------------------");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG,"QNA 게시판 새로고침");
                qnaViewModel.selectQnaList();
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
}