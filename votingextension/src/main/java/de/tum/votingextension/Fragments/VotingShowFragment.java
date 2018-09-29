package de.tum.votingextension.Fragments;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.tum.votingextension.ExtensionType.Voting;
import de.tum.localcampuslib.ShowPostFragment;
import de.tum.localcampuslib.entity.IPost;
import de.tum.votingextension.R;

public class VotingShowFragment extends ShowPostFragment {

    static final String TAG = VotingShowFragment.class.getSimpleName();

    private TextView txtDesciption;
    private TextView txtTemp;
    private TextView txtDeviceId;
    private TextView txtChange;

    private EditText editText;

    private ImageButton btnUp;
    private ImageButton btnDown;
    private Button btnVote;

    private VotingShowViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        viewModel = new VotingShowViewModel(getDataProvider(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.voting_layout, null, false);

        txtDesciption =  view.findViewById(R.id.txt_desciption);
        txtTemp = view.findViewById(R.id.txt_temp);
        txtDeviceId = view.findViewById(R.id.device_id);
        txtChange = view.findViewById(R.id.txt_change);
        editText = view.findViewById(R.id.input);

        btnUp = view.findViewById(R.id.button_upvote);
        btnDown = view.findViewById(R.id.button_downvote);

        btnVote = view.findViewById(R.id.button_vote);

        viewModel.getPost().observe(this, new Observer<IPost>() {
            @Override
            public void onChanged(@Nullable IPost post) {
                viewModel.setPostVariables(post.getData());
                txtDesciption.setText(viewModel.getDescription());
                txtDeviceId.setText(viewModel.getDeviceId());
                txtChange.setText(viewModel.getTempChange());
            }
        });

        viewModel.getLiveVotes().observe(this, new Observer<List<Voting>>() {
            @Override
            public void onChanged(@Nullable List<Voting> votings) {
                txtTemp.setText(viewModel.getAvgTempString(votings));
            }
        });

        setListeners();

        return view;
    }

    private void showWarning(){
        Toast.makeText(getContext()
                , "You either voted already or entered value that is outside of the range ["+ viewModel.getTempMin() +" ; "+ viewModel.getTempMax() + "]"
                , Toast.LENGTH_LONG).show();
    }

    private void setListeners(){
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float value = viewModel.getDownVoteValue();
                if(!viewModel.vote(value)){
                    showWarning();
                }
                else{
                    editText.setText(Float.toString(value));
                }
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float value = viewModel.getUpVoteValue();
                if(!viewModel.vote(value)){
                    showWarning();
                }
                else{
                    editText.setText(Float.toString(value));
                }
            }
        });

        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueString = editText.getText().toString();
                if(valueString.isEmpty()){
                    showWarning();
                }
                else if(!viewModel.vote(Float.parseFloat(valueString))){
                    showWarning();
                }
            }
        });

    }

}