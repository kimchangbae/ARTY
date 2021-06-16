package com.arty.FreeBoard;

import android.view.View;

public interface FreeBoardClickListener {

    public void onItemClick(FreeBoardAdapter.ViewHolder holder, View view, int position);
}
