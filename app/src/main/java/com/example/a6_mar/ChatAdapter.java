package com.example.a6_mar;

import android.graphics.Color;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    List<ChatModel> list;

    public ChatAdapter(List<ChatModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel chatModel = list.get(position);
        if (chatModel.isChatGDP()) {
            holder.imageViewKM_logo.setVisibility(View.INVISIBLE);
            holder.imageViewChatGPT.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.parseColor("#84BAAD"));
        } else {
            holder.imageViewChatGPT.setVisibility(View.INVISIBLE);
            holder.imageViewKM_logo.setVisibility(View.VISIBLE);
        }
        holder.textView.setText(chatModel.getStringText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewChatGPT, imageViewKM_logo;
        TextView textView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewChatGPT = itemView.findViewById(R.id.chatGPT_icon);
            imageViewKM_logo = itemView.findViewById(R.id.km_logo);
            textView = itemView.findViewById(R.id.recyclerView_textView);
        }

    }
}
