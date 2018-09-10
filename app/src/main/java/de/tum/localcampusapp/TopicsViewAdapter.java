package de.tum.localcampusapp;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
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
    private Context context;


    public TopicsViewAdapter(List<Topic> topicList, Context context, TopicsActivity.ItemInsertLongClickListener longClickListener) {
        this.topicList = topicList;
        this.context = context;
        this.longClickListener = longClickListener;
    }

    public TopicsViewAdapter(List<Topic> topicList, Context context){
        this.topicList = topicList;
        this.context = context;
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
                Intent intent = new Intent(context, PostsActivity.class);
                intent.putExtra("topicId", String.valueOf(selectedTopicId));
                Log.d(TAG, "topic_id clicked: "+String.valueOf(selectedTopicId));
                context.startActivity(intent);
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