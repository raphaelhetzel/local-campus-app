package de.tum.localcampusapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;

public class PostsViewAdapter extends RecyclerView.Adapter<PostsViewAdapter.ViewHolder>{

    private static final String TAG = PostsViewAdapter.class.getSimpleName();

    private List<Post> postsList;
    private long selectedPostId;


    private View.OnClickListener postClickListener;


    public PostsViewAdapter(List<Post> postsList, PostsActivity.PostClickListener postClickListener) {
        this.postsList = postsList;
        this.postClickListener = postClickListener;
    }

    public PostsViewAdapter(List<Post> postsList){
        this.postsList = postsList;
    }


    @Override
    public PostsViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posts_listitem, parent, false);
        PostsViewAdapter.ViewHolder holder = new PostsViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PostsViewAdapter.ViewHolder holder, final int position) {

        Post post = postsList.get(position);

        //TODO:set all Post Attributes here
        //holder.imageName.setText(post.getTopicName());
        holder.postDate.setText(post.getUpdatedAt().toString());
        holder.postType.setText(Long.toString(post.getTypeId()));   //TODO: mapping: typeId --> PostType
        holder.postText.setText(post.getData());
        holder.numLikes.setText(Integer.toString(post.getScore()));

        holder.itemView.setOnClickListener(postClickListener);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPostId = post.getId();
                Log.d(TAG, "onClick clicked, element: post_id: "+post.getId());
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setScore(post.getScore() + 1);
                holder.numLikes.setText(Integer.toString(post.getScore()));
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setScore(post.getScore() - 1);
                holder.numLikes.setText(Integer.toString(post.getScore()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public void setItems(List<Post> posts){
        this.postsList = posts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout parentLayout;
        TextView postDate;
        TextView postType;
        TextView postText;
        Button like;
        Button dislike;
        TextView numLikes;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.posts_layout);
            postDate = (TextView) itemView.findViewById(R.id.post_date);
            postType = (TextView) itemView.findViewById(R.id.post_type);
            postText = (TextView) itemView.findViewById(R.id.post_text);
            numLikes = (TextView) itemView.findViewById(R.id.num_likes);
            like = (Button) itemView.findViewById(R.id.button_upvote);
            dislike = (Button) itemView.findViewById(R.id.button_downvote);
        }
    }
}