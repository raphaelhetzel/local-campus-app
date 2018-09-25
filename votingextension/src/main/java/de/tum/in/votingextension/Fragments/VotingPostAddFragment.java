package de.tum.in.votingextension.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import de.tum.localcampuslib.AddPostFragment;


public class VotingPostAddFragment extends AddPostFragment {
    private VotingPostViewModel viewModel;

    static final String TAG = VotingPostAddFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new VotingPostViewModel(getAddPostDataProvider(), getContext());

        final EditText editText = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("New Post with Voting Extension")
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