package de.tum.localcampusapp;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import dalvik.system.DexClassLoader;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.testlibrary.BaseFragment;

public class ServiceTestActivity extends AppCompatActivity {

    static final String TAG = ServiceTestActivity.class.getSimpleName();

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
                        .loadClass("de.tum.testextension.Registry");

                Field classesField = classToLoad.getDeclaredField("_classes");

                ArrayList<Class<?>> classes = (ArrayList<Class<?>>) classesField.get(null);

                for(Class<?> cls : classes) {
                    Log.v("loadDexClasses", "Class loaded " + cls.getName());

                    if (cls.getName().contains("TestFragment")) {
                        Log.v("loadDexClasses", "return instance");

                        Method createInstance = cls.getMethod("createInstance", String.class);
                        Object instance = createInstance.invoke(null, "Foo");
                        return (BaseFragment) instance;
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
