package com.arty.Comment;

import android.view.View;

public interface CommentChildClickListener {
    public void onChildCommentClick(CommentChildAdapter.ViewHolder holder, View view, int position);
}
