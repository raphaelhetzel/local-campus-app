package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.Observer;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.commentsextensionmodule.Fragments.CommentShowFragment;
import de.tum.in.commentsextensionmodule.Fragments.PostAddFragment;
import de.tum.localcampusapp.PermissionManager;
import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ShowPostFragment;

import static de.tum.localcampusapp.PermissionManager.PERMISSION_STORAGE;


public class TopicsActivity extends AppCompatActivity {
    static final String TAG = TopicsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TopicsViewModel viewModel;
    private TopicsViewAdapter mTopicsViewAdapter;

    private static final int PERMISSIONS_REQUEST = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        // Real Data
        RepositoryLocator.init(getApplicationContext());

        // Fake Data - initially used before the Backend was up
//        RepositoryLocator.initInMemory(getApplicationContext());
//        FakeDataGenerator.getInstance().setTopicsRepo(RepositoryLocator.getTopicRepository());
//        FakeDataGenerator.getInstance().setPostRepo(RepositoryLocator.getPostRepository());
//        FakeDataGenerator.getInstance().insertSeveralTopics("Fake Topic", 4);

        // Post Type Included by default
        RepositoryLocator.getExtensionRepository().registerExtension(
                "6ed88f3a-5895-4cac-b096-d260ecc9b71d",
                "Comments Extension"
                ,CommentShowFragment.class,
                PostAddFragment.class, null
        );

        if(! new PermissionManager(this.getApplicationContext()).hasStoragePermission()) {
            requestStoragePermission();
        } else {
            enableAPKExtensions();
        }

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

    public void enableAPKExtensions() {
        RepositoryLocator.getExtensionLoader().loadAPKFiles();
        RepositoryLocator.getExtensionPublisher().enableSharing();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String permissions[], final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.enableAPKExtensions();
        } else {
            Toast.makeText(this, "Application does not have storage Permission," +
                    " APK Extensions are now disabled", Toast.LENGTH_LONG).show();
        }
    }

    private void requestStoragePermission() {
        requestPermissions(new String[]{PERMISSION_STORAGE}, PERMISSIONS_REQUEST);
    }

}