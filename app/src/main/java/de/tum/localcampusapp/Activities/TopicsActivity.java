package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;


public class TopicsActivity extends AppCompatActivity {
    static final String TAG = TopicsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TopicsViewModel viewModel;
    private TopicsViewAdapter mTopicsViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        //Real Data
        super.startService(new Intent(this, AppLibService.class));
        RepositoryLocator.init(getApplicationContext());
        // Fake Data
//        RepositoryLocator.initInMemory(getApplicationContext());
//        FakeDataGenerator.getInstance().setTopicsRepo(RepositoryLocator.getTopicRepository());
//        FakeDataGenerator.getInstance().setPostRepo(RepositoryLocator.getPostRepository());

        try {
            viewModel = new TopicsViewModel(getApplicationContext());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        mTopicsViewAdapter = new TopicsViewAdapter(new ArrayList<Topic>(), this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mTopicsViewAdapter);

        viewModel.getLiveDataTopics().observe(TopicsActivity.this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(@Nullable List<Topic> topics) {
                mTopicsViewAdapter.setItems(topics);
            }
        });

    }

}