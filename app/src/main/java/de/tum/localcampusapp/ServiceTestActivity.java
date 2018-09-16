package de.tum.localcampusapp;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;

public class ServiceTestActivity extends AppCompatActivity {

    static final String TAG = ServiceTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d( TAG, "onCreate");
        super.onCreate(savedInstanceState);


        super.startService( new Intent( this, AppLibService.class ) );
        RepositoryLocator.init(getApplicationContext());

        setContentView(R.layout.activity_servicetest);

        RepositoryLocator.getTopicRepository().getTopics().observe(this, topics -> {
            TextView textView = findViewById(R.id.centered_text);
            textView.setText(topics.stream().map(t -> t.getTopicName()).reduce("", (concat, topic) -> concat+topic+"\n"));
        });

        RepositoryLocator.getPostRepository().getPostsforTopic(1).observe(this, posts -> {
            TextView textView = findViewById(R.id.posts);
            Log.d(TAG, Integer.toString(posts.size()));
            textView.setText(posts.stream().map(t -> t.getUuid()).reduce("", (concat, topic) -> concat+topic+"\n"));
        });

        Button button = findViewById(R.id.test_button);
        button.setOnClickListener((view)-> {
            Post testpost = new Post(1, "1", "DATA");
                RepositoryLocator.getPostRepository().addPost(testpost);
        });
    }
}
