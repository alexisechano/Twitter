package com.codepath.apps.restclienttemplate;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
// ...

public class EditFragmentDialog extends DialogFragment implements EditText.OnEditorActionListener, View.OnClickListener {
    // constants
    public static final String TAG = "Fragment";

    // UI elements
    EditText etCompose;
    Button btnTweet;

    public EditFragmentDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String tweetContent);
    }

    public static EditFragmentDialog newInstance() {
        EditFragmentDialog frag = new EditFragmentDialog();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_compose, container);
        btnTweet = (Button) v.findViewById(R.id.btnTweet);
        etCompose = (EditText) v.findViewById(R.id.etCompose);
        btnTweet.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View view) {
        onEditorAction(null, 0,null);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        EditNameDialogListener listener = (EditNameDialogListener) getActivity();
        listener.onFinishEditDialog(etCompose.getText().toString());
        dismiss();
        return true;

    }
}