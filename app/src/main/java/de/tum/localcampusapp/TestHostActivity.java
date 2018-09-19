package de.tum.localcampusapp;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import de.tum.localcampusapp.extensioninterface.RealAddPostDataProvider;
import de.tum.localcampusapp.extensioninterface.RealShowPostDataProvider;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.localcampuslib.AddPostDataProvider;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.AddPostHostActivity;
import de.tum.localcampuslib.BaseFragment;
import de.tum.localcampuslib.ExtensionContext;
import de.tum.localcampuslib.ShowPostFragment;
import de.tum.localcampuslib.ShowPostHostActivity;
import de.tum.localcampuslib.ShowPostDataProvider;

public class TestHostActivity extends AddPostHostActivity {

    static final String TAG = TestHostActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d( TAG, "onCreate");
        super.onCreate(savedInstanceState);


        super.startService( new Intent( this, AppLibService.class ) );
        RepositoryLocator.init(getApplicationContext());

        setContentView(R.layout.activity_servicetest);

//        RepositoryLocator.getTopicRepository().getTopics().observe(this, topics -> {
//            TextView textView = findViewById(R.id.centered_text);
//            textView.setText(topics.stream().map(t -> t.getTopicName()).reduce("", (concat, topic) -> concat+topic+"\n"));
//        });
//
//        RepositoryLocator.getPostRepository().getPostsforTopic(1).observe(this, posts -> {
//            TextView textView = findViewById(R.id.posts);
//            Log.d(TAG, Integer.toString(posts.size()));
//            textView.setText(posts.stream().map(t -> t.getUuid()).reduce("", (concat, topic) -> concat+topic+"\n"));
//        });

//        Button button = findViewById(R.id.test_button);
//        button.setOnClickListener((view)-> {
//            Post testpost = new Post(1, "1", "DATA");
//                RepositoryLocator.getPostRepository().addPost(testpost);
//        });
        Resources resources = getResources();
        Log.d("RAH Activity", resources.toString());

        Fragment testFragment =  loadDexClassses();
            //Fragment testFragment =  new LocalFragment();
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            testFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, testFragment).commit();
        }
    }

    public Fragment loadDexClassses() {
        try {
            File[] files = new File("data/local/tmp/testjars/").listFiles();

            if (files.length == 0) {
                Log.v("loadDexClasses", "There was no " + "data/local/tmp/testjars/");
                return null;
            }

            Log.v("loadDexClasses", "Dex Preparing to loadDexClasses!");

            for (File file : files) {
                //The following optDexFolder is an arbitrary folder name.
                //E.g., "dex-" + getApplicationContext().getPackageName().hashCode()
                //This way every app will have its own optimized dex folder.
                final File tmpDir = new File("data/local/tmp/optdexjars/" + "testoptdexlca" + "/");

                tmpDir.mkdir();

                Log.v("loadDexClass", file.getAbsolutePath());

                final DexClassLoader classloader = new DexClassLoader(
                        file.getAbsolutePath(), tmpDir.getAbsolutePath(),
                        "data/local/tmp/natives/",
                        this.getClassLoader());

                Log.v("loadDexClasses", "Searching for class : "
                        + "com.registry.Registry");

                Class<?> classToLoad = (Class<?>) classloader
                        .loadClass("de.tum.localcampusextension.Registry");

                //Field showPostFragmentClassField = classToLoad.getDeclaredField("showPostFragmentClass");

                //Class<? extends ShowPostFragment> showPostFragmentClass = (Class<? extends ShowPostFragment>) showPostFragmentClassField.get(null);

                //Object instance = showPostFragmentClass.newInstance();


                Field addPostFragmentClassField = classToLoad.getDeclaredField("addPostFragmentClass");

                Class<? extends AddPostFragment> addPostFragmentClass = (Class<? extends AddPostFragment>) addPostFragmentClassField.get(null);

                Object instance = addPostFragmentClass.newInstance();
                return (BaseFragment) instance;

            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AddPostDataProvider getAddPostDataProvider() {
        return new RealAddPostDataProvider(1, "ee5afd62-6e72-4728-8404-e91d7ea2c303");
    }

    @Override
    public Context getFragmentContext() {
        return new ExtensionContext(this, "/data/local/tmp/testjars/load.apk");
    }

    @Override
    public void finishActivity() {
        //finish();
        Log.d("RAH", "activity should die");
    }

//    @Override
//    public ShowPostDataProvider getDataProvider() {
//        return new RealShowPostDataProvider(1);
//    }
//
//    @Override
//    public Context getFragmentContext() {
//        return new ExtensionContext(this, "/data/local/tmp/testjars/load.apk");
//    }


}
