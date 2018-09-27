package de.tum.localcampusapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Topic;


public class TopicsViewAdapter extends RecyclerView.Adapter<TopicsViewAdapter.ViewHolder>{

    private static final String TAG = TopicsViewAdapter.class.getSimpleName();

    private List<Topic> topicList;
    private long selectedTopicId;
    private Context context;


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
        holder.topicName.setText(topic.getTopicName());

        holder.itemView.setOnLongClickListener((View v) -> {
            //FakeDataGenerator.getInstance().insertNewTopic("Fake elements name");
            return true;
        });

        holder.parentLayout.setOnClickListener((View v) -> {
            selectedTopicId = topic.getId();
            Intent intent = new Intent(context, PostsActivity.class);
            intent.putExtra("topicId", String.valueOf(selectedTopicId));
            context.startActivity(intent);
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

        TextView topicName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.topic_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}