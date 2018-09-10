package de.tum.localcampusapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryTopicRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;

public class ServiceTestActivity extends AppCompatActivity {

    static final String TAG = ServiceTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d( TAG, "onCreate");
        super.onCreate(savedInstanceState);
        super.startService( new Intent( this, AppLibService.class ) );

        //RepositoryLocator.setCustomTopicRepository(new InMemoryTopicRepository());

        //TODO: workaround to circumvent lazy repository initialization
        RepositoryLocator.getPostRepository(this.getApplicationContext());

        setContentView(R.layout.activity_servicetest);
        try {
            RepositoryLocator.getTopicRepository(this).getTopics().observe(this, topics -> {
                TextView textView = findViewById(R.id.centered_text);
                textView.setText(topics.stream().map(t -> t.getTopicName()).reduce("", (concat, topic) -> concat+topic+"\n"));
            });

            RepositoryLocator.getPostRepository(getApplicationContext()).getPostsforTopic(1).observe(this, posts -> {
                TextView textView = findViewById(R.id.posts);
                Log.d(TAG, Integer.toString(posts.size()));
                textView.setText(posts.stream().map(t -> t.getUuid()).reduce("", (concat, topic) -> concat+topic+"\n"));
            });

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        Button button = findViewById(R.id.test_button);
        button.setOnClickListener((view)-> {
            Post testpost = new Post(
                    1,
                    UUID.randomUUID().toString(),
                    "1",
                    1,
                    "Test",
                    new Date(),
                    new Date(),
                    "DATA",
                    0
                    );
            try {
                RepositoryLocator.getPostRepository(this.getApplicationContext()).addPost(testpost);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        });
    }
}
