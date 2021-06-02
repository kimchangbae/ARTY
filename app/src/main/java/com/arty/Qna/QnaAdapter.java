package com.arty.Qna;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class QnaAdapter extends RecyclerView.Adapter<QnaAdapter.ViewHolder> {
    ArrayList<Qna> qnaList = new ArrayList<Qna>();
    private Context context;

    QnaClickListener listener;

    public QnaAdapter() {}

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
    public QnaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.qnalist_item, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull QnaAdapter.ViewHolder holder, int position) {
        // 추후 이미지 불러오기 위해 glide 기술을 사용할 예정
        holder.tv_title.setText(qnaList.get(position).getTitle());
        holder.tv_qnaType.setText(qnaList.get(position).getQnaType());
        holder.tv_userId.setText(qnaList.get(position).getUserId());
        Glide.with(holder.itemView).load(qnaList.get(position).getImg()).into(holder.imageView);

        // Qna item = qnaLists.get(position);
        // holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return qnaList != null ? qnaList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView    tv_title;      // 제목
        TextView    tv_qnaType;    // 글 타입
        TextView    tv_userId;   // 작성자명
        ImageView   imageView;

        public ViewHolder(View itemView, final QnaClickListener listener) {
            super(itemView);

            tv_title    = itemView.findViewById(R.id.tv_title);
            tv_qnaType  = itemView.findViewById(R.id.tv_qnaType);
            tv_userId   = itemView.findViewById(R.id.tv_userId);
            imageView   = itemView.findViewById(R.id.imageView);

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
            tv_qnaType.setText(item.getQnaType());
            tv_userId.setText(item.getUserId());
        }
    }
}
