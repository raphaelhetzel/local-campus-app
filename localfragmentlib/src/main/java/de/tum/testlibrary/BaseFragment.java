package de.tum.testlibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

public class BaseFragment extends Fragment {

    private static final String APK_PATH = "APK_PATH";
    private Context context;

    public static BaseFragment createInstance(String apkPath) {
        Log.d("RAH", "Create called");
        BaseFragment fragment = new BaseFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(APK_PATH, apkPath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //String apkPath = getArguments().getString(APK_PATH);
        this.context = new ExtensionContext(super.getContext(), "/data/local/tmp/testjars/load.apk");
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }
}
