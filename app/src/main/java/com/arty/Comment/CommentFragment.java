package com.arty.Comment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.Qna.QnaDetail;
import com.arty.R;

import java.util.ArrayList;

public class CommentFragment extends Fragment {
    private static final String TAG = "CommentFragment";

    private CommentViewModel                commentViewModel;

    private RecyclerView                    recyclerView;
    private RecyclerView.LayoutManager      layoutManager;
    private CommentAdapter                  adapter;
    private ArrayList<Comment>              arrayList;

    private String      uuid;
    private TextView    commentCount, commentFollow;

    public CommentFragment(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comment, container, false);

        commentViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(CommentViewModel.class);

        commentCount    = getActivity().findViewById(R.id.tv_comment_count);
        commentFollow   = getActivity().findViewById(R.id.tv_comment_followId);

        recyclerView = rootView.findViewById(R.id.commentRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(commentViewModel);
        // Realtime DB 버전
        // commentViewModel.selectQnaItemComment(uuid);

        // Firestore DB 버전
        commentViewModel.selectQnaComment(uuid);

        commentViewModel.getpCommentLists().observeForever(commentLists -> {
            if(!commentLists.isEmpty()) {
                Log.d(TAG,"부모 코맨트 정보 --> " + commentLists);
                adapter.notifyDataSetChanged();
                adapter.setCommentList(commentLists);
                recyclerView.setAdapter(adapter);

                commentCount.setText("댓글 " + commentLists.size() + " 개");
            }

        });

        /*
         * 특정 댓글에 대댓글 달기
         * */
        adapter.setCommentClickListener(new CommentClickListener() {
            @Override
            public void onCommentClick(CommentAdapter.ViewHolder holder, View view, int position) {
                setComment(position);
            }
        });

        return rootView;
    }

    private void setAdapter(ArrayList<Comment> comments) {

    }

    private void setComment(int position) {

        getActivity().findViewById(R.id.commentLayout).setVisibility(View.VISIBLE);
        commentFollow.setTextColor(Color.DKGRAY);
        commentFollow.setText(adapter.getUserId(position) + "님에게 댓글을 작성합니다.");

        ((QnaDetail)getActivity()).setCommentParentId(adapter.getCmntId(position));
        ((QnaDetail)getActivity()).setSortId(adapter.getSortId(position));
    }

}