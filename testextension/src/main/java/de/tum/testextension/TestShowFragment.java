package de.tum.testextension;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.tum.localcampuslib.ShowPostFragment;

public class TestShowFragment extends ShowPostFragment {

    private TestShowViewModel testShowViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Possibly use Android ViewModels bound to the context
        // ViewModelProviders.of ...
        testShowViewModel = new TestShowViewModel(getDataProvider());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater newInflator = inflater.cloneInContext(getContext());

        View view = newInflator.inflate(R.layout.fragment_test, null, false);

        ImageView img= (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(R.drawable.ic_launcher_foreground);


        TextView textView = view.findViewById(R.id.fragmentText);

        testShowViewModel.getText().observe(this, text -> {
            textView.setText(text);
        });
        return view;
    }
}