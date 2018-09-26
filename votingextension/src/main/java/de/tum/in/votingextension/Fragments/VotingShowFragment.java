package de.tum.in.votingextension.Fragments;

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

import de.tum.in.votingextension.ExtensionType.Voting;
import de.tum.in.votingextension.R;
import de.tum.localcampuslib.ShowPostFragment;

public class VotingShowFragment extends ShowPostFragment {

    static final String TAG = VotingShowFragment.class.getSimpleName();
    static final String DESCRIPTION = "Here you can vote for temperature";
    static final String TEXT_ABOVE_TEMP = "Current Temperature";

    private TextView txtDesciption;
    private TextView txtAboveTemp;
    private TextView txtTemp;

    private EditText editText;

    private ImageButton btnUp;
    private ImageButton btnDown;
    private Button btnVote;

    private VotingShowViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        viewModel = new VotingShowViewModel(getDataProvider());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.voting_layout, null, false);

        txtDesciption =  view.findViewById(R.id.txt_desciption);
        txtDesciption.setText(DESCRIPTION);
        txtAboveTemp = view.findViewById(R.id.txt_above_temp);
        txtAboveTemp.setText(TEXT_ABOVE_TEMP);
        txtTemp = view.findViewById(R.id.txt_temp);

        editText = view.findViewById(R.id.input);

        btnUp = view.findViewById(R.id.button_upvote);
        btnDown = view.findViewById(R.id.button_downvote);

        btnVote = view.findViewById(R.id.button_vote);

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
                , "Your entered value is outside of the range ["+ Voting.TEMP_MIN +" ; "+Voting.TEMP_MAX + "]"
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