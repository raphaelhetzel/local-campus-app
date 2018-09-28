package de.tum.in.commentsextensionmodule.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.tum.in.commentsextensionmodule.R;
import de.tum.localcampuslib.AddPostFragment;

public class PostAddFragment extends AddPostFragment {

    static final String TAG = PostAddFragment.class.getSimpleName();

    private PostAddViewModel viewModel;


    private RelativeLayout rootLayout;

    private TextView postDate;
    private TextView postType;

    private EditText titleText;

    private Button buttonSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new PostAddViewModel(getAddPostDataProvider(), getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.post_fragment_layout, null, false);

        rootLayout = view.findViewById(R.id.root_layout);
        rootLayout.setBackgroundColor(viewModel.getColor());

        titleText = (EditText) view.findViewById(R.id.edit_title);

        postDate = (TextView) view.findViewById(R.id.view_date);
        postDate.setText(viewModel.getDate());

        postType = (TextView)  view.findViewById(R.id.view_type);
        postType.setText(viewModel.getTypePost());

        buttonSave = (Button) view.findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!viewModel.addPost(titleText.getText().toString())){
                    String errorMessage = viewModel.getErrorMessage();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(getContext(), "Post successfully added", Toast.LENGTH_LONG).show();
                    finishActivity();
                }

            }
        });

        return view;
    }

}