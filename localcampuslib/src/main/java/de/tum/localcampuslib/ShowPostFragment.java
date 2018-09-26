package de.tum.localcampuslib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
    Extensions need to base their Fragment to show a Post on this class.
    The modified context needs to be used in the <code>onCreateView</code> method of the new Fragment:
    <code>LayoutInflater newInflator = inflater.cloneInContext(getContext());</code>,
    then use the new inflater to return the inflated view.

    Resources can be used, however Resource links within xml resources won't work (e.g. referencing a
    drawable from a layout.
 */
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
