package de.tum.localcampusapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.extensioninterface.RealShowPostDataProvider;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.BaseFragment;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampuslib.ShowPostHostActivity;

public class ShowPostActivity extends ShowPostHostActivity {

    static final String TAG = ShowPostActivity.class.getSimpleName();
    static final String UNKNOWN_POST_TYPE_WARNING = "The Extension required to show this type of post is not installed.";

    private Post post;
    private ShowPostDataProvider showPostDataProvider;
    private Context fragmentContext;
    private BaseFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_fragment_host);

        Intent intent = getIntent();
        if (!intent.hasExtra("selectedPostId")) finish();
        long postId = Long.valueOf(intent.getStringExtra("selectedPostId"));


        RepositoryLocator.getPostRepository().getPost(postId).observe(this, livePost -> {
            this.post = livePost;
            if (post == null) return;

            this.showPostDataProvider = new RealShowPostDataProvider(post.getId());
            this.fragment = RepositoryLocator.getExtensionRepository().getShowPostFragmentFor(post.getTypeId());
            if (fragment == null) {
                Toast toast = Toast.makeText(this, UNKNOWN_POST_TYPE_WARNING, Toast.LENGTH_SHORT);
                toast.show();
                this.finish();
                return;
            }
            this.fragmentContext = RepositoryLocator.getExtensionRepository().getContextFor(post.getTypeId(), this);

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
