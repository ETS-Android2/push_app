package com.example.push_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.push_app.R;

/**
 * About fragment containing basic information about the app.
 */
public class AboutFragment extends Fragment {

    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null. This will be called between onCreate(Bundle)
     * and onActivityCreated(Bundle)
     *
     * @param inflater: The LayoutInflater object that can be used to inflate any
     *                  views in the fragment,
     *
     * @param container: If non-null, this is the parent view that the fragment's UI should
     *                   be attached to. The fragment should not add the view itself,
     *                   but this can be used to generate the LayoutParams of the view.
     *
     * @param savedInstanceState : If non-null, this fragment is being re-constructed from
     *                             a previous saved state as given here.
     **/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}