package de.tum.testextension;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.tum.localcampuslib.AddPostFragment;

public class TestAddFragment extends AddPostFragment {
    private TestAddViewModel testAddViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Possibly use Android ViewModels bound to the context
        // ViewModelProviders.of ...
        testAddViewModel = new TestAddViewModel(getAddPostDataProvider());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater newInflator = inflater.cloneInContext(getContext());

        View view = newInflator.inflate(R.layout.fragment_test, null, false);

        Button button= view.findViewById(R.id.testButton);

        button.setOnClickListener(v -> {
            testAddViewModel.addEmptyPost();
            finishActivity();
        });
        return view;
    }
}
