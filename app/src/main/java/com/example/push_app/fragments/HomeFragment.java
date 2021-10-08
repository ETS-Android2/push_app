package com.example.push_app.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.push_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;


import static java.lang.Math.abs;
public class HomeFragment extends Fragment implements View.OnClickListener {
    private String cityNameText = null;
    private CheckBox checkBox;
    private boolean isAddButtonPressed = false;

    private static final String API_KEY = "06d5e4957656768f786a9fd4dac684f0";
    private static final String UNITS = "metric";
    private static final String CITY = "city";

    private TextView helloMyNameIs, username, removeData;
    private Button deleteButton;


    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null. This will be called between onCreate(Bundle)
     * and onActivityCreated(Bundle)
     *
     * @param inflater:          The LayoutInflater object that can be used to inflate any
     *                           views in the fragment,
     * @param container:         If non-null, this is the parent view that the fragment's UI should
     *                           be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState : If non-null, this fragment is being re-constructed from
     *                           a previous saved state as given here.
     **/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        helloMyNameIs = view.findViewById(R.id.hello);
        username = view.findViewById(R.id.user_name);
        removeData = view.findViewById(R.id.loc);
        deleteButton = view.findViewById(R.id.button_delete);

        deleteButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "To be implemented", Toast.LENGTH_SHORT).show();
    }
}