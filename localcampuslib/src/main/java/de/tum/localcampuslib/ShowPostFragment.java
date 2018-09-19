package de.tum.localcampuslib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class ShowPostFragment extends BaseFragment {

    private Context context;
    private ShowPostDataProvider dataProvider;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ShowPostHostActivity hostActivity = ((ShowPostHostActivity) getActivity());
        this.context = hostActivity.getFragmentContext();
        this.dataProvider = hostActivity.getDataProvider();
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    protected ShowPostDataProvider getDataProvider() {
        return dataProvider;
    }
}
