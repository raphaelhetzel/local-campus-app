package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.postTypes.PostMapper;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String TAG = PostsAdapter.class.getSimpleName();

    private List<PostMapper> postsList;
    private long selectedPostId;
    private Context context;
    private LifecycleOwner lifecycleOwner;


    private PostsAdapterModel adapterModel;

    private PostMapper postMapper;

    private PostsViewModel postsViewModel;

    public PostsAdapter(PostsAdapterModel postsAdapterModel, Context context, LifecycleOwner lifecycleOwner) {
        this.adapterModel = postsAdapterModel;
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;

        adapterModel.getLiveData().observe(lifecycleOwner, new Observer<List<PostMapper>>() {
            @Override
            public void onChanged(@Nullable List<PostMapper> postMappers) {
                setItems(postMappers);
            }
        });
    }


    public PostsAdapter(List<PostMapper> postsList) {
        this.postsList = postsList;
    }

    public PostsAdapter(List<PostMapper> postsList, Context context) {
        this.postsList = postsList;
        this.context = context;
    }


    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posts_listitem, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder holder, final int position) {

        PostMapper postMapper = postsList.get(position);

        holder.parentLayout.setBackgroundColor(postMapper.getColor());
        holder.postType.setText(postMapper.getType());
        holder.postDate.setText(postMapper.getDate());
        holder.postText.setText(postMapper.getTextComment());
        holder.numLikes.setText(postMapper.getLikesString());

        holder.parentLayout.setOnClickListener((View v) -> {

            int clickedPosition = holder.getAdapterPosition();
            selectedPostId = postsList.get(clickedPosition).getId();
            Intent intent = new Intent(context, ShowPostActivity.class);
            intent.putExtra("selectedPostId", String.valueOf(selectedPostId));
            context.startActivity(intent);
        });


        holder.like.setOnClickListener((View v) -> {
            RepositoryLocator.getPostRepository().upVote(postMapper.getId());
        });

        holder.dislike.setOnClickListener((View v) -> {
            RepositoryLocator.getPostRepository().downVote(postMapper.getId());
        });

    }

    @Override
    public int getItemCount() {
        if (postsList == null) return 0;
        return postsList.size();
    }

    public void setItems(List<PostMapper> posts) {
        this.postsList = posts;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;
        TextView postDate;
        TextView postType;
        TextView postText;
        ImageView like;
        ImageView dislike;
        TextView numLikes;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.posts_template_layout);
            postDate = (TextView) itemView.findViewById(R.id.post_date);
            postType = (TextView) itemView.findViewById(R.id.post_type);
            postText = (TextView) itemView.findViewById(R.id.post_text);
            numLikes = (TextView) itemView.findViewById(R.id.num_likes);
            like = itemView.findViewById(R.id.button_upvote);
            dislike = itemView.findViewById(R.id.button_downvote);
        }
    }

}