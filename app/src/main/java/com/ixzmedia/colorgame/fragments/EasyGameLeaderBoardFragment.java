package com.ixzmedia.colorgame.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ixzmedia.colorgame.R;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Objects;

public class EasyGameLeaderBoardFragment extends Fragment {


    public EasyGameLeaderBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_easy_game_leader_board, container, false);
    }

    private ArrayList<HighScoreModelResponse> readFromStorage(String address) {

        ArrayList<HighScoreModelResponse> storedData = null;
        FileInputStream fis;
        try {
            fis = Objects.requireNonNull(getActivity()).openFileInput(address);
            ObjectInputStream ois = new ObjectInputStream(fis);
            storedData = (ArrayList<HighScoreModelResponse>) ois.readObject();

            return storedData;
        } catch (Exception e) {
            Log.d("Read Stored Info", "Read ::: " + e.getMessage());
        }

        return null;
    }
}