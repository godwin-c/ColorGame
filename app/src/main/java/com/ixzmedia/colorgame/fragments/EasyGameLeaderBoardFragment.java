package com.ixzmedia.colorgame.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ixzmedia.colorgame.LeaderBoardActivity;
import com.ixzmedia.colorgame.R;
import com.ixzmedia.colorgame.adapters.HighScoreAdapter;
import com.ixzmedia.colorgame.classes.NetworkAvaillabilityClass;
import com.ixzmedia.colorgame.classes.VerticalSpace;
import com.ixzmedia.colorgame.networkoperations.CustomCallBack;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_Client;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_interface;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;
import com.vdx.designertoast.DesignerToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EasyGameLeaderBoardFragment extends Fragment {

    private static final String RECEIVER_ID = "com.ixzmedia.colorgame.LEADERBOARD_BROADCAST";
    SwipeRefreshLayout frag_easy_swipe_refresh;
    RecyclerView frag_easy_recycler_view;

    TextView empty_data_view;

    private static final String EASY_LEVEL = "easy_level";

    private static final String TAG = EasyGameLeaderBoardFragment.class.getSimpleName();

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
        View view = inflater.inflate(R.layout.fragment_easy_game_leader_board, container, false);

        frag_easy_swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.frag_easy_swipe_refresh);
        frag_easy_recycler_view = (RecyclerView) view.findViewById(R.id.frag_easy_recycler_view);
        empty_data_view = (TextView) view.findViewById(R.id.empty_data_view);

        setActionsOnViewItems();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (fileExist(EASY_LEVEL)){
            Log.d(TAG, "onCreate: File Exists");

            ArrayList<HighScoreModelResponse> easyHighScores = readFromStorage(EASY_LEVEL);

            frag_easy_recycler_view.setVisibility(View.VISIBLE);
            empty_data_view.setVisibility(View.GONE);

            setupRecyclerView(easyHighScores);
        } else {
            frag_easy_recycler_view.setVisibility(View.GONE);
            empty_data_view.setVisibility(View.VISIBLE);
        }
    }

    private void setActionsOnViewItems() {

        frag_easy_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLeaderBoard();
            }
        });

        frag_easy_swipe_refresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,

                android.R.color.holo_green_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_red_light);
}


    private void refreshLeaderBoard() {
        NetworkAvaillabilityClass networkAvaillabilityClass = new NetworkAvaillabilityClass(getContext());
        if (networkAvaillabilityClass.hasNetwork()) {
            Rest_DB_interface rest_db_interface = Rest_DB_Client.getClient().create(Rest_DB_interface.class);
            Call<ArrayList<HighScoreModelResponse>> call = rest_db_interface.getAllHighScores();

            call.enqueue(new CustomCallBack<>(getActivity(), new Callback<ArrayList<HighScoreModelResponse>>() {
                @Override
                public void onResponse(Call<ArrayList<HighScoreModelResponse>> call, Response<ArrayList<HighScoreModelResponse>> response) {

                    if (response.code() == 200) {
                        ArrayList<HighScoreModelResponse> highScoreModelResponses = response.body();

                        assert highScoreModelResponses != null;
                        if (highScoreModelResponses.size() > 0) {

                            Log.d(TAG, "onResponse: Size of Data : " + highScoreModelResponses.size());
//                            medium
                            frag_easy_swipe_refresh.setRefreshing(false);
                            Intent intent = new Intent(RECEIVER_ID);
                            getContext().sendBroadcast(intent);

                            setupDataForDisplay(highScoreModelResponses);

                        } else {
                            DesignerToast.Info(getContext(), "no high scores uploaded yet. Be the first to upload yours", Gravity.CENTER, Toast.LENGTH_SHORT);
                        }

                    } else {
                        DesignerToast.Error(getContext(), "error while fetching high scores", Gravity.CENTER, Toast.LENGTH_SHORT);
                    }

                    frag_easy_swipe_refresh.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<ArrayList<HighScoreModelResponse>> call, Throwable t) {
                    frag_easy_swipe_refresh.setRefreshing(false);
                    DesignerToast.Error(getContext(), "error while fetching high scores '" + t.getMessage() + "'", Gravity.CENTER, Toast.LENGTH_SHORT);
                }
            }));
        } else {
            frag_easy_swipe_refresh.setRefreshing(false);
            DesignerToast.Error(getContext(), "looks like you are not connected to the internet", Gravity.CENTER, Toast.LENGTH_SHORT);
        }
    }

    private void setupDataForDisplay(ArrayList<HighScoreModelResponse> highScoreModelResponses) {

        ArrayList<HighScoreModelResponse> easyHighScores = new ArrayList<>();
        ArrayList<HighScoreModelResponse> mediumHighScore = new ArrayList<>();
        ArrayList<HighScoreModelResponse> hardHighScore = new ArrayList<>();


        for (HighScoreModelResponse highScoreModelResponse : highScoreModelResponses) {
            String level = highScoreModelResponse.getGame_level();
            switch (level) {
                case "simple":

                    easyHighScores.add(highScoreModelResponse);
                    break;
                case "medium":
                    mediumHighScore.add(highScoreModelResponse);
                    break;
                case "hard":
                    hardHighScore.add(highScoreModelResponse);
                    break;
            }
        }

        ArrayList<HighScoreModelResponse> topThreeHighScore = new ArrayList<>();

        if (easyHighScores.size() > 0) {
            Log.d(TAG, "onResponse: Size of Easy : " + easyHighScores.size());
            topThreeHighScore.add(checkHighestScore(easyHighScores));
            storeResponse(easyHighScores, "easy_level");
        }

        if (mediumHighScore.size() > 0) {
            Log.d(TAG, "onResponse: Size of Medium : " + mediumHighScore.size());
            topThreeHighScore.add(checkHighestScore(mediumHighScore));
            storeResponse(mediumHighScore, "medium_level");
        }

        if (hardHighScore.size() > 0) {
            Log.d(TAG, "onResponse: Size of Hard : " + hardHighScore.size());
            topThreeHighScore.add(checkHighestScore(hardHighScore));
            storeResponse(hardHighScore, "hard_level");
        }


        storeResponse(topThreeHighScore, "top_three_high_scores");
        //restartActivity();
        setupRecyclerView(easyHighScores);
    }

    private void setupRecyclerView(ArrayList<HighScoreModelResponse> easyHighScores) {
        HighScoreAdapter adapter;
        if (frag_easy_recycler_view.getAdapter() == null || frag_easy_recycler_view.getAdapter().getItemCount() < 1){
            adapter = new HighScoreAdapter(getContext(), easyHighScores);
            frag_easy_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
            frag_easy_recycler_view.addItemDecoration(new VerticalSpace(20));
            frag_easy_recycler_view.setAdapter(adapter);
        }else {
            adapter = (HighScoreAdapter) frag_easy_recycler_view.getAdapter();
            adapter.setItems(easyHighScores);
            adapter.notifyDataSetChanged();
        }
    }

    private HighScoreModelResponse checkHighestScore(ArrayList<HighScoreModelResponse> sampleHighScores) {

        int highScore = 1;
        HighScoreModelResponse winner = null;
        for (HighScoreModelResponse highScoreModelResponse : sampleHighScores) {
            if (highScoreModelResponse.getHighscore() >= highScore) {
                highScore = highScoreModelResponse.getHighscore();
                winner = highScoreModelResponse;
            }
        }


        return winner;
    }

    public boolean fileExist(String fname){
        File file = getContext().getFileStreamPath(fname);
        return file.exists();
    }

    private void storeResponse(ArrayList<HighScoreModelResponse> highScoreModelResponses, String filename) {
        try {
            FileOutputStream fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(highScoreModelResponses);

            objectOutputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "storeResponse: " + e.getLocalizedMessage());
        }
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