package de.tum.in.commentsextensionmodule.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.tum.in.commentsextensionmodule.R;
import de.tum.in.commentsextensionmodule.ExtensionType.Comment;
import de.tum.in.commentsextensionmodule.Generator.DateTransformer;

public class CommentsShowAdapter extends RecyclerView.Adapter<CommentsShowAdapter.ViewHolder>{

    private static final String TAG = CommentsShowAdapter.class.getSimpleName();
    private List<Comment> commentsList;
    private int color;

    public CommentsShowAdapter(List<Comment> commentsList) {
    this.commentsList = commentsList;
}


    @Override
    public CommentsShowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.comments_listitem, parent, false);
        CommentsShowAdapter.ViewHolder holder = new CommentsShowAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentsShowAdapter.ViewHolder holder, final int position) {
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
