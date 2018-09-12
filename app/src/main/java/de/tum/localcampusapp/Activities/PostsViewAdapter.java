package de.tum.localcampusapp.Activities;

import android.content.Context;
import android.content.Intent;
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

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostsViewAdapter extends RecyclerView.Adapter<PostsViewAdapter.ViewHolder>{

    private static final String TAG = PostsViewAdapter.class.getSimpleName();

    private List<Post> postsList;
    private long selectedPostId;
    private Context context;

    private Post post;


    public PostsViewAdapter(List<Post> postsList) {
        this.postsList = postsList;
    }

    public PostsViewAdapter(List<Post> postsList, Context context) {
        this.postsList = postsList;
        this.context = context;
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

        //Post post = postsList.get(position);
        post = postsList.get(position);

        holder.postDate.setText(post.getCreatedAt().toString());
        holder.postType.setText(post.getTypeId());
        holder.postText.setText(post.getData());
        holder.numLikes.setText(Long.toString(post.getScore()));


        holder.parentLayout.setOnClickListener((View v) -> {
                selectedPostId = post.getId();
                Intent intent = new Intent(context, PostCommentActivity.class);
                intent.putExtra("selectedPostId", String.valueOf(selectedPostId));
                Log.d(TAG, "post_id clicked: "+ String.valueOf(selectedPostId));
                context.startActivity(intent);
        });


        holder.like.setOnClickListener((View v) -> {
            RepositoryLocator.getPostRepository().upVote(post.getId());
        });

        holder.dislike.setOnClickListener((View v) -> {
            RepositoryLocator.getPostRepository().downVote(post.getId());
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
        ImageView like;
        ImageView dislike;
        TextView numLikes;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.posts_layout);
            postDate = (TextView) itemView.findViewById(R.id.post_date);
            postType = (TextView) itemView.findViewById(R.id.post_type);
            postText = (TextView) itemView.findViewById(R.id.post_text);
            numLikes = (TextView) itemView.findViewById(R.id.num_likes);
            like = itemView.findViewById(R.id.button_upvote);
            dislike = itemView.findViewById(R.id.button_downvote);
        }
    }

}