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
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryTopicRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;

public class ServiceTestActivity extends AppCompatActivity {

    static final String TAG = ServiceTestActivity.class.getSimpleName();

    private AppLibService.ScampiBinder scampiBinder;
    private boolean boundService = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = new Intent(getApplicationContext(), AppLibService.class);
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT);

        Log.d( TAG, "onCreate");
        super.onCreate(savedInstanceState);
        RepositoryLocator.setCustomTopicRepository(new InMemoryTopicRepository());
        setContentView(R.layout.activity_servicetest);
        super.startService( new Intent( this, AppLibService.class ) );
        try {
            RepositoryLocator.getTopicRepository(this).getTopics().observe(this, topics -> {
                TextView textView = findViewById(R.id.centered_text);
                Log.d(TAG, Integer.toString(topics.size()));
                textView.setText(topics.stream().map(t -> t.getTopicName()).reduce("", (concat, topic) -> concat+topic+"\n"));
            });
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        Button button = findViewById(R.id.test_button);
        button.setOnClickListener((view)-> {
            Post testpost = new Post(
                    1,
                    UUID.randomUUID().toString(),
                    1,
                    1,
                    "Test",
                    new Date(),
                    new Date(),
                    "DATA",
                    0
                    );
            scampiBinder.publishPost(testpost);
        });
    }



    // TODO: Just here for testing, need to figure out a way to bind the service in the repository
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Service conencted");
            AppLibService.ScampiBinder scampi = (AppLibService.ScampiBinder) service;
            scampiBinder = scampi;
            boundService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundService = false;
        }
    };
}
