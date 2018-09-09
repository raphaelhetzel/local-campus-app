package de.tum.localcampusapp;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;

public class PostsActivity extends AppCompatActivity{

        static final String TAG = PostsActivity.class.getSimpleName();

        private RecyclerView mRecyclerView;
        private PostsAdapterViewModel viewModel;
        private PostsViewAdapter mPostsViewAdapter;

        class PostClickListener implements View.OnClickListener{
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostsActivity.this, TopicsActivity.class);
                intent.putExtra("key", 123);
                PostsActivity.this.startActivity(intent);
            }
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            Log.d( TAG, "onCreate");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.posts_activity);

            Intent intent = getIntent();
            //int topic_id = intent.getIntExtra(TopicsActivity.EXTRA_MESSAGE);
            long topic_id = intent.getIntExtra("topicId", 0);
            String an = intent.getStringExtra("android");
            Log.d(TAG, "id: "+topic_id + " strung: "+an);
            //long topicId = (long) savedInstanceState.get("toicId");
            //Log.d(TAG, Long.toString(topicId));
            //TODO: change to dynamic id
            long topicId = 1212;

            try {
                viewModel = new PostsAdapterViewModel(topicId, getApplication());
            } catch (DatabaseException e) {
                e.printStackTrace();
            }

            mPostsViewAdapter = new PostsViewAdapter(new ArrayList<Post>(), new PostClickListener());

            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mPostsViewAdapter);

            viewModel.getLiveDataPosts().observe(PostsActivity.this, new Observer<List<Post>>() {
                @Override
                public void onChanged(@Nullable List<Post> posts) {
                    mPostsViewAdapter.setItems(posts);
                }
            });

        }


    }