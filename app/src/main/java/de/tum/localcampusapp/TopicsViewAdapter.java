package de.tum.localcampusapp;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.entity.Topic;

public class TopicsViewAdapter extends RecyclerView.Adapter<TopicsViewAdapter.ViewHolder>{

    private static final String TAG = TopicsViewAdapter.class.getSimpleName();

    private List<Topic> topicList;
    private View.OnLongClickListener longClickListener;
    private long selectedTopicId;


    public TopicsViewAdapter(List<Topic> topicList, TopicsActivity.ItemInsertLongClickListener longClickListener) {
        this.topicList = topicList;
        this.longClickListener = longClickListener;
    }

    public TopicsViewAdapter(List<Topic> topicList){
        this.topicList = topicList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topics_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Topic topic = topicList.get(position);
        holder.imageName.setText(topic.getTopicName());
        holder.itemView.setOnLongClickListener(longClickListener);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTopicId = topic.getId();
                Log.d(TAG, "onClick clicked, element: "+ topic.getTopicName() + " topic_id: "+topic.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void setItems(List<Topic> topics){
        this.topicList = topics;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView imageName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}