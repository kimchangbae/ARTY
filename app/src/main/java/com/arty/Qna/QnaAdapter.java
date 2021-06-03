package com.arty.Qna;

import android.content.Context;
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
    ArrayList<Qna> qnaList = new ArrayList<Qna>();
    private Context context;

    QnaClickListener listener;

    public QnaAdapter(ArrayList<Qna> qnaLists, Context context) {
        this.qnaList = qnaLists;
        this.context = context;
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

    public void setItem (int position, Qna item) {
        qnaList.set(position, item);
    }

    public void setQnaClickListener(QnaClickListener listener) {
        this.listener = listener;
    }

    @Override
    public QnaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.qnalist_item, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(QnaAdapter.ViewHolder holder, int position) {
        holder.tv_title.setText(qnaList.get(position).getTitle());
        holder.tv_contentType.setText(qnaList.get(position).getContentType());
        holder.tv_userId.setText(qnaList.get(position).getUserId());

        Glide.with(holder.itemView).load(qnaList.get(position).getImage1()).into(holder.imageView);

        //Qna item = qnaLists.get(position);
        //holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return qnaList != null ? qnaList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView    tv_title;           // 제목
        TextView    tv_contentType;     // 글 타입
        TextView    tv_userId;          // 작성자명
        TextView    tv_date;            // 작성시간
        ImageView   imageView;

        public ViewHolder(View itemView, final QnaClickListener listener) {
            super(itemView);

            tv_title        = itemView.findViewById(R.id.tv_title);
            tv_contentType  = itemView.findViewById(R.id.tv_contentType);
            tv_userId       = itemView.findViewById(R.id.tv_userId);
            tv_date         = itemView.findViewById(R.id.tv_date);
            imageView       = itemView.findViewById(R.id.imageView);

            // Qna가 클릭 될 경우.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null) {
                        listener.onQnaClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem (Qna item) {
            tv_title.setText(item.getTitle());
            tv_contentType.setText(item.getContentType());
            tv_userId.setText(item.getUserId());
        }
    }
}
