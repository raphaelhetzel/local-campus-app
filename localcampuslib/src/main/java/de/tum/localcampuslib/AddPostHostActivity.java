package de.tum.localcampuslib;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public abstract class AddPostHostActivity extends AppCompatActivity {
    public abstract AddPostDataProvider getAddPostDataProvider();
    public abstract Context getFragmentContext();
    public abstract void finishActivity();
}