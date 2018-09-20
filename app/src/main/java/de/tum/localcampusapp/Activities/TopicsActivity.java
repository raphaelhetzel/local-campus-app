package de.tum.localcampusapp.Activities;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.extensioninterface.ExtensionLoader;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;


public class TopicsActivity extends AppCompatActivity {
    static final String TAG = TopicsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TopicsViewModel viewModel;
    private TopicsViewAdapter mTopicsViewAdapter;

    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSIONS_REQUEST = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);
        if(!hasPermission()) {
            requestPermission();
        } else {
            onCreateWithPermissions();
        }
    }

    private void onCreateWithPermissions() {
        //Real Data
        super.startService(new Intent(this, AppLibService.class));
        RepositoryLocator.init(getApplicationContext());
        RepositoryLocator.getExtensionLoader().loadAPKFiles();
        // Fake Data
//        RepositoryLocator.initInMemory(getApplicationContext());
//        FakeDataGenerator.getInstance().setTopicsRepo(RepositoryLocator.getTopicRepository());
//        FakeDataGenerator.getInstance().setPostRepo(RepositoryLocator.getPostRepository());
//        FakeDataGenerator.getInstance().insertSeveralTopics("Fake Topic", 4);
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

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String permissions[],
            final int[] grantResults ) {
        switch ( requestCode ) {
            case PERMISSIONS_REQUEST: {
                if ( grantResults.length > 0
                        && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    this.onCreateWithPermissions();
                } else {
                    requestPermission();
                }
            }
        }
    }

    private boolean hasPermission() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            return checkSelfPermission( PERMISSION_STORAGE ) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if ( shouldShowRequestPermissionRationale( PERMISSION_STORAGE ) ) {
                Toast.makeText( this, "Storage permission are "
                        + "required for this application", Toast.LENGTH_LONG ).show();
            }
            requestPermissions( new String[] { PERMISSION_STORAGE }, PERMISSIONS_REQUEST );
        }
    }

}