package de.tum.localcampuslib;

import android.content.Context;
import android.support.annotation.Nullable;

public class AddPostFragment extends BaseFragment {
    private Context context;
    private AddPostDataProvider addPostDataProvider;
    private AddPostHostActivity hostActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        hostActivity = ((AddPostHostActivity) getActivity());
        this.context = hostActivity.getFragmentContext();
        this.addPostDataProvider = hostActivity.getAddPostDataProvider();
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    protected AddPostDataProvider getAddPostDataProvider() {
        return addPostDataProvider;
    }

    protected void finishActivity() {
        hostActivity.finishActivity();
    }
}
