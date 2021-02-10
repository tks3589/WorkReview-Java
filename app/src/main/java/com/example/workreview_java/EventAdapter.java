package com.example.workreview_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workreview_java.model.EventModel;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<EventModel> data;
    private Context context;

    public EventAdapter(Context context,ArrayList<EventModel> arrayList) {
        this.context = context;
        data = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EventViewHolder){
            ((EventViewHolder) holder).bind(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }

        void bind(EventModel model){
            Glide.with(context).load(model.imageurl).into(imageView);
            textView.setText(model.title);
        }
    }


}
