package de.tum.localcampusapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
    }
}
