package com.arty.Comment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arty.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<Comment>      commentList = null;
    private CommentClickListener    listener;
    private CommentViewModel        commentViewModel;

    private RecyclerView                recyclerView;
    private RecyclerView.LayoutManager  layoutManager;
    private CommentChildAdapter         childAdapter;


    public CommentAdapter(CommentViewModel commentViewModel) {
        this.commentViewModel = commentViewModel;
    }

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setCommentClickListener(CommentClickListener listener) {
        this.listener = listener;
    }

    public String getCmntId(int position) {
        return commentList.get(position).getComtId();
    }

    public String getUserId(int position) {
        return commentList.get(position).getUserId();
    }

    public String getSortId(int position) {
        return commentList.get(position).getSortId();
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.comment_item, parent, false);
/*
        recyclerView    = itemView.findViewById(R.id.childCommentRecyclerView);
        layoutManager   = new LinearLayoutManager(itemView.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
*/
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        System.out.println("현재 바인딩 중인 부모 댓글 [" +commentList.get(position).getComtId()+"]");
        
        holder.userId.setText(commentList.get(position).getUserId());
        holder.comment.setText(commentList.get(position).getComment());
        holder.time.setText(commentList.get(position).getCreateTime());
/*
        commentViewModel.selectChildComment(commentList.get(position).getComtId());

        commentViewModel.getcCommentLists().observeForever(commentLists -> {
            if(!commentLists.isEmpty()) {
                System.out.println("자식 댓글 정보 --> " + commentLists);
                childAdapter = new CommentChildAdapter();
                childAdapter.notifyDataSetChanged();
                childAdapter.setCommentList(commentLists);
                recyclerView.setAdapter(childAdapter);;
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userId, comment, time;

        public ViewHolder(View itemView, final CommentClickListener listener) {
            super(itemView);

            userId      = itemView.findViewById(R.id.tv_comnent_userId);
            comment     = itemView.findViewById(R.id.tv_comment);
            time        = itemView.findViewById(R.id.tv_comment_time);

            itemView.findViewById(R.id.tv_comment_btn)
            .setOnClickListener(view -> {
                int position = getAdapterPosition();
                Log.d("QnaAdapter","position " + position + " 클릭 됨.");
                if(listener != null)
                    listener.onCommentClick(ViewHolder.this, view, position);
            });
        }
    }
}
