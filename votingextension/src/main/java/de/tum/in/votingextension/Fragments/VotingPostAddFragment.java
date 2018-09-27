package de.tum.in.votingextension.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.tum.in.votingextension.R;
import de.tum.localcampuslib.AddPostFragment;


public class VotingPostAddFragment extends AddPostFragment {

    static final String TAG = VotingPostAddFragment.class.getSimpleName();

    private VotingPostViewModel viewModel;

    //Extension
    private TextView txtDesciption;
    private TextView txtTempChange;
    private TextView txtTemp;
    private TextView txtDeviceId;


    //Post
    private EditText postDesciption;
    private EditText deviceId;
    private EditText tempMin;
    private EditText tempMax;
    private EditText tempCurr;
    private EditText tempChange;
    private Button btnSave;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new VotingPostViewModel(getAddPostDataProvider(), getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater newInflator = inflater.cloneInContext(getContext());
        View view = newInflator.inflate(R.layout.create_post_layout, null, false);

        setUpVariables(view);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setInputValues(
                        postDesciption.getText().toString(),
                        deviceId.getText().toString(),
                        tempMin.getText().toString(),
                        tempMax.getText().toString(),
                        tempCurr.getText().toString(),
                        tempChange.getText().toString());

                if(!viewModel.addPost()){
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


    private void setUpVariables(View view){
        //Extension Variables
        txtDesciption =  view.findViewById(R.id.txt_desciption);
        txtTempChange = view.findViewById(R.id.txt_change);;
        txtTemp = view.findViewById(R.id.txt_temp);
        txtDeviceId = view.findViewById(R.id.txt_device_id);

        //Post Variables
        postDesciption = view.findViewById(R.id.post_description);
        deviceId = view.findViewById(R.id.device_id);
        tempMin = view.findViewById(R.id.tempMin);
        tempMax = view.findViewById(R.id.tempMax);
        tempCurr = view.findViewById(R.id.tempCurr);
        tempChange = view.findViewById(R.id.tempChange);
        btnSave = view.findViewById(R.id.btnSave);

        //Listeners for "Live" Updates
        postDesciption.addTextChangedListener(new EditTextListener<TextView>(txtDesciption));
        tempCurr.addTextChangedListener(new EditTextListener<TextView>(txtTemp));
        tempChange.addTextChangedListener(new EditTextListener<TextView>(txtTempChange));
        deviceId.addTextChangedListener(new EditTextListener<TextView>(txtDeviceId));
    }


    public class EditTextListener<elemExtension> implements TextWatcher{
        private TextView elemExtension;

        public EditTextListener(elemExtension elem2){
            this.elemExtension = (TextView) elem2;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            elemExtension.setText(s.toString());
        }
    }


}