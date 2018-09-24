package de.tum.in.postcreater.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import de.tum.localcampuslib.AddPostFragment;

public class PostAddFragment extends AddPostFragment {
    private PostAddViewModel viewModel;

    static final String TAG = PostAddFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new PostAddViewModel(getAddPostDataProvider(), getContext());

        final EditText editText = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("New Post with Comment")
                .setMessage("Add Posts text below")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "floating clicked");
                        String textData = String.valueOf(editText.getText());
                        viewModel.addPost(textData);
                        finishActivity();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();


    }

}
