package de.tum.localcampusapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.extensioninterface.RealAddPostDataProvider;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.AddPostDataProvider;
import de.tum.localcampuslib.AddPostHostActivity;
import de.tum.localcampuslib.BaseFragment;

public class AddPostActivity extends AddPostHostActivity {


    static final String TAG = AddPostActivity.class.getSimpleName();
    static final String UNKNOWN_POST_TYPE_WARNING = "The Extension to create a post of this type is missing!";

    private AddPostDataProvider addPostDataProvider;
    private Context fragmentContext;
    private BaseFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_fragment_host);

        Intent intent = getIntent();
        if (!intent.hasExtra("selectedTopicId") || !intent.hasExtra("selectedPostType")) finish();

        long topicId = Long.valueOf(intent.getStringExtra("selectedTopicId"));
        String postType = intent.getStringExtra("selectedPostType");

        this.addPostDataProvider = new RealAddPostDataProvider(topicId, postType);

        this.fragment = RepositoryLocator.getExtensionRepository().getAddPostFragmentFor(postType);
        if (fragment == null) {
            Toast toast = Toast.makeText(this, UNKNOWN_POST_TYPE_WARNING, Toast.LENGTH_SHORT);
            toast.show();
            this.finish();
            return;
        }
        this.fragmentContext = RepositoryLocator.getExtensionRepository().getContextFor(postType, this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, this.fragment).commit();

    }

    @Override
    public AddPostDataProvider getAddPostDataProvider() {
        return this.addPostDataProvider;
    }

    @Override
    public Context getFragmentContext() {
        return this.fragmentContext;
    }

    @Override
    public void finishActivity() {
        this.finish();
    }
}
