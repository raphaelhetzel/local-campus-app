package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.postTypes.Comment;

public class PostCommentActivity extends AppCompatActivity {

    static final String TAG = PostCommentActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private PostCommentViewModel viewModel;
    private PostCommentViewAdapter mCommentsViewAdapter;

    private RelativeLayout parentLayout;
    private TextView postDate;
    private TextView postType;
    private TextView postText;
    private Button like;
    private Button dislike;
    private TextView numLikes;

    private Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_comment_activity);

        setPostVariables();

        Intent intent = getIntent();

        long postId = Long.valueOf(intent.getStringExtra("selectedPostId"));
        Log.d(TAG, "post_id received: "+ String.valueOf(postId));


        try {
            viewModel = new PostCommentViewModel(postId, getApplicationContext());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        mCommentsViewAdapter = new PostCommentViewAdapter(new ArrayList<Comment>(), this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_comments);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCommentsViewAdapter);

        viewModel.getLiveDataPost().observe(PostCommentActivity.this, new Observer<Post>() {
            @Override
            public void onChanged(@Nullable Post post) {
                updatePostVariables(post);
            }
        });


        viewModel.getLiveDataComments().observe(PostCommentActivity.this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(@Nullable List<Comment> comments) {
                mCommentsViewAdapter.setItems(comments);
            }
        });

    }


    private void setPostVariables(){
        parentLayout = findViewById(R.id.posts_layout);
        postDate = findViewById(R.id.post_date);
        postType = findViewById(R.id.post_type);
        postText = findViewById(R.id.post_text);
        numLikes = findViewById(R.id.num_likes);
        like = findViewById(R.id.button_upvote);
        dislike = findViewById(R.id.button_downvote);

        FloatingActionButton btnAddComment = findViewById(R.id.btn_add);


        btnAddComment.setOnClickListener((View v) -> {
            final EditText editText = new EditText(PostCommentActivity.this);
            AlertDialog dialog = new AlertDialog.Builder(PostCommentActivity.this)
                    .setTitle("New Comment")
                    .setMessage("Add comment text below")
                    .setView(editText)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String textData = String.valueOf(editText.getText());
                            try {
                                viewModel.addComment(textData);
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setScore(post.getScore() + 1);
                numLikes.setText(Integer.toString(post.getScore()));
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setScore(post.getScore() -1);
                numLikes.setText(Integer.toString(post.getScore()));
            }
        });

    }

    private void updatePostVariables(Post post){
        this.post = post;
        postDate.setText(post.getUpdatedAt().toString());
        postType.setText(String.valueOf(post.getTypeId()));
        postText.setText(post.getData());
        numLikes.setText(String.valueOf(post.getScore()));
    }

}
