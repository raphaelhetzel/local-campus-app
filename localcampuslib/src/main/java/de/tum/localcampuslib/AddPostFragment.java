package de.tum.localcampuslib;

import android.content.Context;
import android.support.annotation.Nullable;

/**
 Extensions need to base their Fragment to add a Post on this class.
 The modified context needs to be used in the <code>onCreateView</code> method of the new Fragment:
 <code>LayoutInflater newInflator = inflater.cloneInContext(getContext());</code>,
 then use the new inflater to return the inflated view.

 Resources can be used, however Resource links within xml resources won't work (e.g. referencing a
 drawable from a layout.
 */
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
