package de.tum.localcampusapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jaredrummler.materialspinner.MaterialSpinner;

import de.tum.localcampusapp.R;
import de.tum.localcampusapp.exception.DatabaseException;

public class PostsActivity extends AppCompatActivity {

    static final String TAG = PostsActivity.class.getSimpleName();
    static final String SELECTED_TOPIC_KEY = "selectedTopicId";
    static final String SELECTED_POST_KEY = "selectedPostType";

    private RecyclerView mRecyclerView;
    private PostsAdapter mPostsViewAdapter;

    private PostsViewModel viewModel;
    private PostsAdapterModel adapterModel;

    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_activity);

        Intent intent = getIntent();
        long topicId = Long.valueOf(intent.getStringExtra("topicId"));

        try {
            viewModel = new PostsViewModel(topicId, getApplicationContext());
            adapterModel = new PostsAdapterModel(topicId, getApplicationContext());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        mPostsViewAdapter = new PostsAdapter(adapterModel, getApplicationContext(), PostsActivity.this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPostsViewAdapter);


        //From here the spinner and the dialog are set up, so after a click on the floatingActionButton
        //a sialog will be opened having the spinner inside which contains all available extensions,
        //which are retrieved from the PostSpinnerViewModel
        //After selecting an extension item and clicking on mButton the activity for creating a post of
        //this extension type will start
        FloatingActionButton fab = findViewById(R.id.fab);

        View view = this.getLayoutInflater().inflate(R.layout.post_create, null);
        PostSpinnerViewModel viewModel = new PostSpinnerViewModel();
        MaterialSpinner spinner = (MaterialSpinner) view.findViewById(R.id.spinner);

        Button mButton = (Button) view.findViewById(R.id.button_create);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPostIntent = new Intent(getApplicationContext(), AddPostActivity.class);
                addPostIntent.putExtra(SELECTED_TOPIC_KEY, String.valueOf(topicId));
                addPostIntent.putExtra(SELECTED_POST_KEY, viewModel.getUIID());

                dialog.dismiss();   //Needed otherwise after creating a post the dialog will still be opened

                getApplicationContext().startActivity(addPostIntent);
            }
        });

        spinner.setItems(viewModel.getExtensionDescriptions());
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                viewModel.setChosenPosition(position);
            }
        });

        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        fab.setOnClickListener((View v) -> {
            dialog.show();
        });

    }

}