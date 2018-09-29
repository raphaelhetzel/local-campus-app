package de.tum.piechartsurvey.Fragments;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tum.piechartsurvey.ExtensionsType.Vote;
import de.tum.piechartsurvey.ExtensionsType.VoteOption;
import de.tum.piechartsurvey.ExtensionsType.VotingOptions;
import de.tum.localcampuslib.ShowPostFragment;
import de.tum.localcampuslib.entity.IPost;
import de.tum.piechartsurvey.R;
import im.dacer.androidcharts.PieHelper;
import im.dacer.androidcharts.PieView;


public class PieExtensionFragment extends ShowPostFragment {

    private PieExtensionViewModel viewModel;

    private TextView textTitle;
    private LinearLayout buttonsLayout;
    private PieView pieView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new PieExtensionViewModel(getDataProvider());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.piechart_extension_layout, container, false);

        setUpLayout(view);


        viewModel.getLivePost().observe(this, new Observer<IPost>() {
            @Override
            public void onChanged(@Nullable IPost post) {
                setUpButtons(viewModel.getVotingOptions(post));
            }
        });

        viewModel.getLiveVotes().observe(this, new Observer<List<Vote>>() {
            @Override
            public void onChanged(@Nullable List<Vote> votes) {
                setUpVotes(votes);
            }
        });

        return view;
    }

    private void setUpLayout(View view) {
        buttonsLayout = (LinearLayout) view.findViewById(R.id.btnLayout);
        FrameLayout placeholder = view.findViewById(R.id.pie_container);
        pieView = new PieView(this.getContext());
        placeholder.addView(pieView);
        textTitle = (TextView) view.findViewById(R.id.title);
    }

    private void setUpVotes(List<Vote> votes) {
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();

        for (int i = 0; i < votes.size(); i++) {
            pieHelperArrayList.add(new PieHelper(votes.get(i).getScoreInPerctentage(), votes.get(i).getColor()));
        }

        //pieView.selectedPie(2); //optional
        pieView.showPercentLabel(true);
        pieView.setDate(pieHelperArrayList);
    }

    private void setUpButtons(VotingOptions votingOptions) {
        textTitle.setText(votingOptions.getTitle());

        for (VoteOption option : votingOptions.getOptions()) {
            Button button = new Button(getContext());
            button.setBackgroundColor(option.getColor());
            button.setText(option.getText());
            button.setTextColor(Color.WHITE);
            button.setId(option.getId());

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.addVote(button.getId());
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonsLayout.addView(button, lp);
        }
    }


}