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
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;

public class PostsActivity extends AppCompatActivity{

        static final String TAG = PostsActivity.class.getSimpleName();

        private RecyclerView mRecyclerView;
        private PostsViewModel viewModel;
        private PostsViewAdapter mPostsViewAdapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            Log.d( TAG, "onCreate");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.posts_activity);

            Intent intent = getIntent();

            long topicId = Long.valueOf(intent.getStringExtra("topicId"));
            Log.d(TAG, "topic_id received: "+ String.valueOf(topicId));

            try {
                viewModel = new PostsViewModel(topicId, getApplicationContext());
            } catch (DatabaseException e) {
                e.printStackTrace();
            }

            mPostsViewAdapter = new PostsViewAdapter(new ArrayList<Post>(),this);

            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mPostsViewAdapter);


            viewModel.getLiveDataPosts().observe(PostsActivity.this, new Observer<List<Post>>() {
                @Override
                public void onChanged(@Nullable List<Post> posts) {
                    mPostsViewAdapter.setItems(posts);
                }
            });

            FloatingActionButton fab = findViewById(R.id.fab);


            fab.setOnClickListener((View v) -> {
                final EditText editText = new EditText(PostsActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(PostsActivity.this)
                        .setTitle("New Post")
                        .setMessage("Add post text below")
                        .setView(editText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String textData = String.valueOf(editText.getText());
                                try {
                                    viewModel.addPost(textData);
                                } catch (DatabaseException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            });

        }
}