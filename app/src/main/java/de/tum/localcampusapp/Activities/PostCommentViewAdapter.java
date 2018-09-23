package de.tum.localcampusapp.Activities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.generator.DateTransformer;
import de.tum.localcampusapp.postTypes.Comment;

public class PostCommentViewAdapter extends RecyclerView.Adapter<PostCommentViewAdapter.ViewHolder>{

    private static final String TAG = PostCommentViewAdapter.class.getSimpleName();

    private List<Comment> commentsList;
    private int color;


    public PostCommentViewAdapter(List<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    @Override
    public PostCommentViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments_listitem, parent, false);
        PostCommentViewAdapter.ViewHolder holder = new PostCommentViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PostCommentViewAdapter.ViewHolder holder, final int position) {

        Comment comment = commentsList.get(position);
        holder.parentLayout.setBackgroundColor(color);

        holder.dateText.setText(DateTransformer.getTimeDate(comment.getUpdatedComment()));
        holder.commentText.setText(comment.getData());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public void setItems(List<Comment> comments){
        this.commentsList = comments;
        notifyDataSetChanged();
    }

    public void setBackColor(int colorGet){
        color = colorGet;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout parentLayout;
        TextView dateText;
        TextView commentText;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            dateText = itemView.findViewById(R.id.comment_date);
            commentText = itemView.findViewById(R.id.comment_text);
        }
    }

}