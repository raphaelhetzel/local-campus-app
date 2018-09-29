package de.tum.in.piechartsurvey.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.piechartsurvey.ExtensionsType.Colors;
import de.tum.in.piechartsurvey.ExtensionsType.Vote;
import de.tum.in.piechartsurvey.ExtensionsType.VoteOption;
import de.tum.in.piechartsurvey.R;
import de.tum.localcampuslib.AddPostFragment;
import im.dacer.androidcharts.PieHelper;

public class PiePostFragment extends AddPostFragment {

    static final String TAG = PiePostFragment.class.getSimpleName();

    private PiePostViewModel viewModel;


    private LinearLayout optionsLayout;

    private EditText title;
    private EditText txtOption;

    private ImageButton imgButtonAdd;

    private Button btnReset;
    private Button btnSave;

    private ArrayList<Button> buttons;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new PiePostViewModel(getAddPostDataProvider(), getContext());
        buttons = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.piechart_post_layout, null, false);

        setUpLayout(view);

        setUpListeners();

        return view;
    }

    private void setUpLayout(View view) {
        optionsLayout = (LinearLayout) view.findViewById(R.id.selection_layout);

        title = (EditText) view.findViewById(R.id.title);
        txtOption = (EditText) view.findViewById(R.id.text_option);

        imgButtonAdd = (ImageButton) view.findViewById(R.id.img_btn_add);

        btnReset = (Button) view.findViewById(R.id.btn_reset);
        btnSave = (Button) view.findViewById(R.id.btn_save);
    }

    private void setUpListeners() {

        imgButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtOption.getText().toString();
                VoteOption voteOption = viewModel.addNewOption(text);
                if (voteOption != null) {
                    setUpOption(voteOption);
                } else {
                    Toast.makeText(getContext(), viewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = title.getText().toString();
                if (viewModel.saveSurvey(titleText)) {
                    Toast.makeText(getContext(), "Your survey was successfully added", Toast.LENGTH_LONG).show();
                    finishActivity();
                } else {
                    Toast.makeText(getContext(), viewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.resetVoteOptions();
                removeButtons();
            }
        });

    }

    private void setUpOption(VoteOption voteOption) {
        Button button = new Button(getContext());
        button.setBackgroundColor(voteOption.getColor());
        button.setText(voteOption.getText());
        button.setTextColor(Color.WHITE);
        button.setId(voteOption.getId());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        optionsLayout.addView(button, lp);
        buttons.add(button);
    }

    private void removeButtons() {
        for (Button button : buttons) {
            optionsLayout.removeView(button);
        }
    }


}