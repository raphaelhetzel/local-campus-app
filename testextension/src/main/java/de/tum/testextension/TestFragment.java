package de.tum.testextension;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.tum.testlibrary.BaseFragment;

public class TestFragment extends BaseFragment {
    private static final String APK_PATH = "APK_PATH";

    public static TestFragment createInstance(String apkPath) {
        Log.d("RAH", "Create called");
        TestFragment fragment = new TestFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(APK_PATH, apkPath);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TestViewModel testViewModel = new TestViewModel(11);

        LayoutInflater newInflator = inflater.cloneInContext(getContext());

        View view = newInflator.inflate(R.layout.fragment_test, null, false);

        ImageView img= (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.ic_launcher_foreground);
        return view;
    }
}