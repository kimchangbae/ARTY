package com.arty.Comment;

import android.view.View;

public interface CommentClickListener {
    public void onCommentClick(CommentAdapter.ViewHolder holder, View view, int position);

}
