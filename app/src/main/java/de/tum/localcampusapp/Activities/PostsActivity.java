package de.tum.localcampusapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.exception.DatabaseException;

public class PostsActivity extends AppCompatActivity{

        static final String TAG = PostsActivity.class.getSimpleName();

        private RecyclerView mRecyclerView;
        private PostsViewModel viewModel;
        private PostsAdapterModel adapterModel;

        private PostsAdapter mPostsViewAdapter;


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
                adapterModel = new PostsAdapterModel(topicId, getApplicationContext());
            } catch (DatabaseException e) {
                e.printStackTrace();
            }


            mPostsViewAdapter = new PostsAdapter(adapterModel, getApplicationContext(), PostsActivity.this);

            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mPostsViewAdapter);


            FloatingActionButton fab = findViewById(R.id.fab);

            fab.setOnClickListener((View v) -> {
                Intent addPostIntent = new Intent(this, AddPostActivity.class);
                addPostIntent.putExtra("selectedTopicId", String.valueOf(topicId));
                // TODO: replace with Real Type (needs a picker)
                addPostIntent.putExtra("selectedPostType", "ee5afd62-6e72-4728-8404-e91d7ea2c303");
                this.startActivity(addPostIntent);
            });

        }
}