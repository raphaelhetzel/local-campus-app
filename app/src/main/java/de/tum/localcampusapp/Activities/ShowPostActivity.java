package de.tum.localcampusapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.extensioninterface.ExtensionLoader;
import de.tum.localcampusapp.extensioninterface.RealShowPostDataProvider;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.BaseFragment;
import de.tum.localcampuslib.ExtensionContext;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampuslib.ShowPostHostActivity;

public class ShowPostActivity extends ShowPostHostActivity {

    static final String TAG = ShowPostActivity.class.getSimpleName();

    private Post post;
    private ShowPostDataProvider showPostDataProvider;
    private Context fragmentContext;
    private BaseFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_show_post);

        Intent intent = getIntent();
        if(!intent.hasExtra("selectedPostId")) finish();
        long postId = Long.valueOf(intent.getStringExtra("selectedPostId"));

        // Probably called to often if we call it here
        //ExtensionLoader.init(this.getApplicationContext());


        // TODO: Evaluate whether a direct query on main  is better
        RepositoryLocator.getPostRepository().getPost(postId).observe(this, livePost -> {
            this.post = livePost;
            if(post == null) return;

            this.showPostDataProvider = new RealShowPostDataProvider(post.getId());
//            this.fragment = ExtensionLoader.getShowPostFragmentFor(post.getTypeId());
            this.fragment = ExtensionLoader.getShowPostFragmentFor("ee5afd62-6e72-4728-8404-e91d7ea2c303");
            if(fragment == null) return;
//            this.fragmentContext = new ExtensionContext(this, ExtensionLoader.getPathFor(post.getTypeId()));
            this.fragmentContext = new ExtensionContext(this, ExtensionLoader.getPathFor("ee5afd62-6e72-4728-8404-e91d7ea2c303"));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, this.fragment).commit();
        });

    }

    @Override
    public ShowPostDataProvider getDataProvider() {
        return this.showPostDataProvider;
    }

    @Override
    public Context getFragmentContext() {
        return fragmentContext;
    }
}
