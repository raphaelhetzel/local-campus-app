package de.tum.localcampuslib;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public abstract class ShowPostHostActivity extends AppCompatActivity {
    public abstract ShowPostDataProvider getDataProvider();
    public abstract Context getFragmentContext();
}
