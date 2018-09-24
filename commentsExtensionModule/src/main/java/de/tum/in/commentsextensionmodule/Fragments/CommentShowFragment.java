package de.tum.in.commentsextensionmodule.Fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.commentsextensionmodule.R;
import de.tum.in.commentsextensionmodule.ExtensionType.Comment;
import de.tum.in.commentsextensionmodule.Generator.PostMapper;
import de.tum.localcampuslib.ShowPostFragment;
import de.tum.localcampuslib.entity.IPost;

public class CommentShowFragment extends ShowPostFragment{


    static final String TAG = CommentShowFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private CommentShowViewModel viewModel;
    private CommentsShowAdapter mCommentsViewAdapter;

    private RelativeLayout rootLayout;
    private RelativeLayout postParentLayout;
    private TextView postDate;
    private TextView postType;
    private TextView postText;
    private ImageView like;
    private ImageView dislike;
    private TextView numLikes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Comment Extension");
        super.onCreate(savedInstanceState);

        viewModel = new CommentShowViewModel(getDataProvider());
        mCommentsViewAdapter = new CommentsShowAdapter(new ArrayList<>());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.comments_layout, container, false);

        setPostVariables(view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_comments);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mCommentsViewAdapter);


        viewModel.getLivePost().observe(this, new Observer<IPost>() {
            @Override
            public void onChanged(@Nullable IPost post) {
                PostMapper postMapper = PostMapper.getWorkingPostMapper(post);
                if(postMapper != null){
                    mCommentsViewAdapter.setBackColor(postMapper.getColor());
                    updatePostVariables(postMapper);
                }
            }
        });

        viewModel.getLiveComments().observe(this,  new Observer<List<Comment>>() {
            @Override
            public void onChanged(@Nullable List<Comment> comments) {
                mCommentsViewAdapter.setItems(comments);
            }
        });

        return view;
    }

    private void updatePostVariables(PostMapper postMapper) {
        postDate.setText(postMapper.getDate());
        int color = postMapper.getColor();
        rootLayout.setBackgroundColor(color);
        postParentLayout.setBackgroundColor(color);
        postType.setText(postMapper.getType());
        numLikes.setText(postMapper.getLikesString());
        postText.setText(postMapper.getTextComment());
    }



    private void setPostVariables (View view) {

        rootLayout = view.findViewById(R.id.posts_comment_layout);
        postParentLayout = view.findViewById(R.id.posts_template_layout);

        postDate = view.findViewById(R.id.post_date);
        postType = view.findViewById(R.id.post_type);
        postText = view.findViewById(R.id.post_text);
        numLikes = view.findViewById(R.id.num_likes);
        like = view.findViewById(R.id.button_upvote);
        dislike = view.findViewById(R.id.button_downvote);


        FloatingActionButton btnAddComment = view.findViewById(R.id.btn_add);

        btnAddComment.setOnClickListener((View v) -> {
            final EditText editText = new EditText(getContext());
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("New Comment")
                    .setMessage("Add comment text below")
                    .setView(editText)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "floating clicked");
                            String textData = String.valueOf(editText.getText());
                            viewModel.addComment(textData);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        });


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.upVote();
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.downVote();
            }
        });

    }
}
