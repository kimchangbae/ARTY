package com.arty.Qna;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arty.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<Comment> commentList;

    private CommentClickListener listener;

    public CommentAdapter() {}

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setCommentClickListener(CommentClickListener listener) {
        this.listener = listener;
    }

    public long getSeqNo(int position) {
        return commentList.get(position).getSeqNo();
    }

    public String getUserId(int position) {
        return commentList.get(position).getUserId();
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.qna_comment_item, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        holder.userId.setText(commentList.get(position).getUserId());
        holder.comment.setText(commentList.get(position).getComment());
        holder.time.setText(commentList.get(position).getUploadTime());
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
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Log.d("QnaAdapter","position " + position + " 클릭 됨.");
                    if(listener != null)
                        listener.onCommentClick(ViewHolder.this, view, position);
                }
            });
        }
    }
}
