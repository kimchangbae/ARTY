package com.arty.Comment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arty.R;

import java.util.ArrayList;

public class CommentChildAdapter extends RecyclerView.Adapter<CommentChildAdapter.ViewHolder> {
    private ArrayList<Comment>          commentList;
    private CommentChildClickListener   listener;

    public void setCommentClickListener(CommentChildClickListener listener) {
        this.listener = listener;
    }

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public CommentChildAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.comment_item_child, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(CommentChildAdapter.ViewHolder holder, int position) {
        holder.userId.setText(commentList.get(position).getUserId());
        holder.comment.setText(commentList.get(position).getComment());
        holder.time.setText(commentList.get(position).getCreateTime());
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userId, comment, time;

        public ViewHolder(View itemView, final CommentChildClickListener listener) {
            super(itemView);

            userId      = itemView.findViewById(R.id.tv_comnent_userId);
            comment     = itemView.findViewById(R.id.tv_comment);
            time        = itemView.findViewById(R.id.tv_comment_time);

            itemView.findViewById(R.id.tv_comment_btn)
            .setOnClickListener(view -> {
                int position = getAdapterPosition();
                Log.d("QnaAdapter","position " + position + " 클릭 됨.");
                if(listener != null)
                    listener.onChildCommentClick(ViewHolder.this, view, position);
            });
        }
    }
}
