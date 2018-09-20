package de.tum.localcampusapp;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import de.tum.localcampusapp.extensioninterface.RealShowPostDataProvider;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.localcampuslib.ShowPostHostActivity;
import de.tum.localcampuslib.ShowPostDataProvider;

public class TestHostActivity extends ShowPostHostActivity {

    static final String TAG = TestHostActivity.class.getSimpleName();

    static final String POST_TYPE = "ee5afd62-6e72-4728-8404-e91d7ea2c303";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d( TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicetest);


        super.startService( new Intent( this, AppLibService.class ) );
        RepositoryLocator.init(getApplicationContext());

        Fragment testFragment = RepositoryLocator.getExtensionRepository().getShowPostFragmentFor(POST_TYPE);
        if(testFragment == null) {
            Log.d(TAG, "fragment not found");
            return;
        }
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            testFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, testFragment).commit();
        }
    }

//    @Override
//    public AddPostDataProvider getAddPostDataProvider() {
//        return new RealAddPostDataProvider(1, "ee5afd62-6e72-4728-8404-e91d7ea2c303");
//    }
//
//    @Override
//    public Context getFragmentContext() {
//        return new ExtensionContext(this, "/data/local/tmp/testjars/load.apk");
//    }
//
//    @Override
//    public void finishActivity() {
//        //finish();
//    }

    @Override
    public ShowPostDataProvider getDataProvider() {
        return new RealShowPostDataProvider(2);
    }

    @Override
    public Context getFragmentContext() {
        return RepositoryLocator.getExtensionRepository().getContextFor(POST_TYPE, this);
    }


}
