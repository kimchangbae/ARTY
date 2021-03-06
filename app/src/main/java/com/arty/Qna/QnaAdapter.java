package com.arty.Qna;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arty.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class QnaAdapter extends RecyclerView.Adapter<QnaAdapter.ViewHolder> {
    private ArrayList<Qna> qnaList;
    private Context context;

    QnaClickListener listener;

    public QnaAdapter(ArrayList<Qna> qnaLists) {
        this.qnaList = qnaLists;
    }

    public QnaAdapter(ArrayList<Qna> qnaLists, Context context) {
        this.qnaList = qnaLists;
        this.context = context;
    }

    public void setQnaList(ArrayList<Qna> qnaList) {
        this.qnaList = qnaList;
    }

    public void addItem(Qna item) {
        qnaList.add(item);
    }

    public void setItems(ArrayList<Qna> items) {
        this.qnaList = items;
    }

    public Qna getItems(int position) {
        return qnaList.get(position);
    }

    public String getUuid(int position) {
        return qnaList.get(position).getUuId();
    }

    public void setItem (int position, Qna item) {
        qnaList.set(position, item);
    }

    public void setQnaClickListener(QnaClickListener listener) {
        this.listener = listener;
    }

    @Override
    public QnaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.qna_list_item, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(QnaAdapter.ViewHolder holder, int position) {
        holder.tv_contentType.setText(qnaList.get(position).getContentType());
        holder.tv_userId.setText(qnaList.get(position).getUserId());
        Glide.with(holder.itemView).load(qnaList.get(position).getImage1()).into(holder.imageView);
        holder.tv_uploadDate.setText(qnaList.get(position).getUploadTime());


        if(qnaList.get(position).getContent().length() >= 13) {
            String content = qnaList.get(position).getContent().substring(0, 12).concat("...");
            holder.tv_content.setText(content);
        } else {
            holder.tv_content.setText(qnaList.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return qnaList != null ? qnaList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView    tv_content;         // ??????
        TextView    tv_contentType;     // ??? ??????
        TextView    tv_userId;          // ????????????
        TextView    tv_uploadDate;      // ????????????
        ImageView   imageView;

        public ViewHolder(View itemView, final QnaClickListener listener) {
            super(itemView);

            tv_content      = itemView.findViewById(R.id.tv_content);
            tv_contentType  = itemView.findViewById(R.id.tv_contentType);
            tv_userId       = itemView.findViewById(R.id.tv_userId);
            tv_uploadDate   = itemView.findViewById(R.id.tv_uploadDate);
            imageView       = itemView.findViewById(R.id.imageView);

            // Qna??? ?????? ??? ??????.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Log.d("QnaAdapter","position " + position + " ?????? ???.");
                    if(listener != null) {
                        listener.onQnaClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem (Qna item) {
            tv_content.setText(item.getContent());
            tv_contentType.setText(item.getContentType());
            tv_userId.setText(item.getUserId());
        }
    }
}
