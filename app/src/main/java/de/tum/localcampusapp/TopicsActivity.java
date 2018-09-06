package de.tum.localcampusapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.entity.Topic;

public class TopicsActivity extends AppCompatActivity {
    static final String TAG = TopicsActivity.class.getSimpleName();

    private ArrayList<String> mNames = new ArrayList<String>();
    private ArrayList<String> mImageUrls = new ArrayList<String>();

    private LiveData<Topic> topicData;

    private RecyclerView mRecyclerView;
    private TopicsAdapterViewModel viewModel;
    private TopicsViewAdapter mTopicsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        //ArrayList<Topic> topics =         Get Topics
        // Fake data
        ArrayList<Topic> topics = new ArrayList<Topic>();
        topics.add(new Topic(12212, "topicName1"));
        topics.add(new Topic(122112, "topicName2"));
        topics.add(new Topic(1212212212, "topicName3"));


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mTopicsViewAdapter = new TopicsViewAdapter(topics, (View.OnLongClickListener) this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mTopicsViewAdapter);

        viewModel = ViewModelProviders.of(this).get(TopicsAdapterViewModel.class);

        viewModel.getTopics().observe(TopicsActivity.this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(@Nullable List<Topic> topics) {   //maybe new Observable<List<Topic>>()
                mTopicsViewAdapter.setItems(topics);
            }
        });
    }

}
