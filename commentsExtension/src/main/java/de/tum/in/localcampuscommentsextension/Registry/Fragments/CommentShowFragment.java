package de.tum.in.localcampuscommentsextension.Registry.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import de.tum.in.localcampuscommentsextension.R;
import de.tum.localcampuslib.ShowPostFragment;

public class CommentShowFragment extends ShowPostFragment {


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
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        viewModel = new CommentShowViewModel(getDataProvider());
        mCommentsViewAdapter = new CommentsShowAdapter(new ArrayList<>());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.comments_layout, null, false);

        setPostVariables(view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_comments);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mCommentsViewAdapter);

        return view;


        /*
         ImageView img= (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.ic_launcher_foreground);
         */


    }

        /*
        setContentView(R.layout.post_comment_activity);

        setPostVariables();


        long postId = 10;






        viewModel.getLiveDataPost().observe(PostCommentActivity.this, new Observer<PostMapper>() {
            @Override
            public void onChanged(PostMapper postMapper) {
                try {
                    mCommentsViewAdapter.setBackColor(postMapper.getColor());
                    updatePostVariables(postMapper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel.getLiveDataComments().observe(PostCommentActivity.this,  new Observer<List<Comment>>() {
            @Override
            public void onChanged(@Nullable List<Comment> comments) {
                mCommentsViewAdapter.setItems(comments);
            }
        });


    }

    private void updatePostVariables(PostMapper postMapper) {
        postDate.setText(postMapper.getDate());

        try {
            int color = postMapper.getColor();
            rootLayout.setBackgroundColor(color);
            postParentLayout.setBackgroundColor(color);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        postType.setText(postMapper.getType());
        numLikes.setText(postMapper.getLikesString());

        try {
            postText.setText(postMapper.getTextComment());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setPostVariables () {

        rootLayout = findViewById(R.id.posts_comment_layout);
        postParentLayout = findViewById(R.id.posts_template_layout);

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
              //  viewModel.upVote();   //TODO: API method needed
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // viewModel.downVote(); //TODO: API method needed
            }
        });

    }
}

/*
 viewModel.getScore().observe(this, score -> {
            postScore.setText(Long.toString(score));
        });
        return view;
 */

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
                            String textData = String.valueOf(editText.getText());

                              //  viewModel.addComment(textData);     //TODO: --> Commend Add Fragment!

                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  viewModel.upVote();   //TODO: API method needed
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // viewModel.downVote(); //TODO: API method needed
            }
        });

    }
}
