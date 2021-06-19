package com.arty.FreeBoard;

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

public class FreeBoardAdapter extends RecyclerView.Adapter<FreeBoardAdapter.ViewHolder> {
    ArrayList<FreeBoard> freeBoardList = new ArrayList<FreeBoard>();
    private Context context;

    FreeBoardClickListener listener;

    public FreeBoardAdapter(ArrayList<FreeBoard> freeBoardLists) {
        this.freeBoardList = freeBoardLists;
    }

    public FreeBoardAdapter(ArrayList<FreeBoard> freeBoardLists, Context context) {
        this.freeBoardList = freeBoardLists;
        this.context = context;
    }

    public void addItem(FreeBoard item) {
        freeBoardList.add(item);
    }

    public void setItems(ArrayList<FreeBoard> items) {
        this.freeBoardList = items;
    }

    public FreeBoard getItems(int position) {
        return freeBoardList.get(position);
    }

    public void setItem (int position, FreeBoard item) {
        freeBoardList.set(position, item);
    }

    public String getUuid(int position) {
        return freeBoardList.get(position).getUuId();
    }

    public void setClickListener(FreeBoardClickListener listener) {
        this.listener = listener;
    }

    @Override
    public FreeBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.freeboard_list_item, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(FreeBoardAdapter.ViewHolder holder, int position) {
        holder.content.setText(freeBoardList.get(position).getContent());
        holder.userId.setText(freeBoardList.get(position).getUserId());
        holder.uploadDate.setText(freeBoardList.get(position).getUploadTime());
        Glide.with(holder.itemView).load(freeBoardList.get(position).getImage1()).into(holder.imageView);

        //FreeBoard item = qnaLists.get(position);
        //holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return freeBoardList != null ? freeBoardList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView    content;         // 제목
        TextView    userId;          // 작성자명
        TextView    uploadDate;      // 작성시간
        ImageView   imageView;

        public ViewHolder(View itemView, final FreeBoardClickListener listener) {
            super(itemView);

            content      = itemView.findViewById(R.id.tv_f_content);
            userId       = itemView.findViewById(R.id.tv_f_userId);
            uploadDate   = itemView.findViewById(R.id.tv_f_uploadDate);
            imageView    = itemView.findViewById(R.id.iv_f_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem (FreeBoard item) {
            content.setText(item.getContent());
            userId.setText(item.getUserId());
        }
    }
}
