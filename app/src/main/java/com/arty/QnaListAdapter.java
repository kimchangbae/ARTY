package com.arty;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class QnaListAdapter extends RecyclerView.Adapter<QnaListAdapter.ViewHolder> {
    ArrayList<QnaList> qnaLists = new ArrayList<QnaList>();

    public void addItem(QnaList item) {
        qnaLists.add(item);
    }

    public void setItems(ArrayList<QnaList> items) {
        this.qnaLists = items;
    }

    public QnaList getItems(int position) {
        return qnaLists.get(position);
    }

    public void setItem (int position, QnaList item) {
        qnaLists.set(position, item);
    }

    @Override
    public QnaListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.qnalist_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QnaListAdapter.ViewHolder holder, int position) {
        QnaList item = qnaLists.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return qnaLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView qnaType;
        // TextView context;
        TextView userName;
        // TextView timeStamp;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_qnaList_title);
            qnaType = itemView.findViewById(R.id.textView_qnaList_type);
            userName = itemView.findViewById(R.id.textView_qnaList_name);


        }

        public void setItem (QnaList item) {
            title.setText(item.getTitle());
            qnaType.setText(item.getQnaType());
            userName.setText(item.getUserName());
            //timeStamp.setText(item.getTimeStamp());
        }
    }
}
