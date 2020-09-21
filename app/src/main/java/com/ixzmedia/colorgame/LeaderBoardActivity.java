package com.ixzmedia.colorgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.ixzmedia.colorgame.adapters.EachOfThreeAdapter;
import com.ixzmedia.colorgame.adapters.TabsAdapter;
import com.ixzmedia.colorgame.classes.NetworkAvaillabilityClass;
import com.ixzmedia.colorgame.classes.TopThreeLeaders;
import com.ixzmedia.colorgame.networkoperations.CustomCallBack;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_Client;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_interface;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModel;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;
import com.vdx.designertoast.DesignerToast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderBoardActivity extends AppCompatActivity {

    private static final String TAG = LeaderBoardActivity.class.getSimpleName();
    // tab titles
    private String[] titles = new String[]{"Easy", "Medium", "Hard"};

    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    ViewPager vp;
    public static TabLayout tabLayout;
    ViewPager leadersViewPager, tabsViewPager;
    TabsAdapter tabsAdapter;
    EachOfThreeAdapter adapter;

    Integer[] colors = null;

    HighScoreModelResponse topThreeLeader;
    ArrayList<HighScoreModelResponse> topThreeLeaders;
    SwipeRefreshLayout swipe_to_refresh_leader_board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[1]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[2]));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        leadersViewPager = findViewById(R.id.leaders_viewpager);
        tabsViewPager = findViewById(R.id.tab_layout_viewpager);
        swipe_to_refresh_leader_board = findViewById(R.id.swipe_to_refresh_leader_board);


        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        tabsViewPager.setAdapter(tabsAdapter);
        tabsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        swipe_to_refresh_leader_board.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_to_refresh_leader_board.setRefreshing(false);
                refreshLeaderBoard();
            }
        });
        setupThreeLeaders();

    }

    private void refreshLeaderBoard() {
        NetworkAvaillabilityClass networkAvaillabilityClass = new NetworkAvaillabilityClass(LeaderBoardActivity.this);
        if (networkAvaillabilityClass.hasNetwork()) {
            Rest_DB_interface rest_db_interface = Rest_DB_Client.getClient().create(Rest_DB_interface.class);
            Call<ArrayList<HighScoreModelResponse>> call = rest_db_interface.getAllHighScores();

            call.enqueue(new CustomCallBack<>(LeaderBoardActivity.this, new Callback<ArrayList<HighScoreModelResponse>>() {
                @Override
                public void onResponse(Call<ArrayList<HighScoreModelResponse>> call, Response<ArrayList<HighScoreModelResponse>> response) {

                    if (response.code() == 200) {
                        ArrayList<HighScoreModelResponse> highScoreModelResponses = response.body();

                        assert highScoreModelResponses != null;
                        if (highScoreModelResponses.size() > 0) {
//                            medium
                            ArrayList<HighScoreModelResponse> easyHighScores = new ArrayList<>();
                            ArrayList<HighScoreModelResponse> mediumHighScore = new ArrayList<>();
                            ArrayList<HighScoreModelResponse> hardHighScore = new ArrayList<>();


                            for (HighScoreModelResponse highScoreModelResponse : highScoreModelResponses) {
                                String level = highScoreModelResponse.getGame_level();
                                switch (level) {
                                    case "easy":

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
                                topThreeHighScore.add(checkHighestScore(easyHighScores));
                                storeResponse(easyHighScores, "easy_level");
                            }

                            if (mediumHighScore.size() > 0) {
                                topThreeHighScore.add(checkHighestScore(mediumHighScore));
                                storeResponse(mediumHighScore, "medium_level");
                            }

                            if (hardHighScore.size() > 0) {
                                topThreeHighScore.add(checkHighestScore(hardHighScore));
                                storeResponse(hardHighScore, "hard_level");
                            }

                            storeResponse(topThreeHighScore, "top_three_high_scores");
                            restartActivity();

                        } else {
                            DesignerToast.Info(LeaderBoardActivity.this, "no high scores uploaded yet. Be the first to upload yours", Gravity.CENTER, Toast.LENGTH_SHORT);
                        }

                    } else {
                        DesignerToast.Error(LeaderBoardActivity.this, "error while fetching high scores", Gravity.CENTER, Toast.LENGTH_SHORT);
                    }

                }

                @Override
                public void onFailure(Call<ArrayList<HighScoreModelResponse>> call, Throwable t) {
                    DesignerToast.Error(LeaderBoardActivity.this, "error while fetching high scores '" + t.getMessage() + "'", Gravity.CENTER, Toast.LENGTH_SHORT);
                }
            }));
        } else {

            DesignerToast.Error(LeaderBoardActivity.this, "looks like you are not connected to the internet", Gravity.CENTER, Toast.LENGTH_SHORT);
        }
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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

    private void storeResponse(ArrayList<HighScoreModelResponse> highScoreModelResponses, String filename) {
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(highScoreModelResponses);

            objectOutputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "storeResponse: " + e.getLocalizedMessage());
        }
    }

    private void setupThreeLeaders() {
        topThreeLeaders = readFromStorage("top_three_high_scores");

        adapter = new EachOfThreeAdapter(topThreeLeaders, LeaderBoardActivity.this);
        leadersViewPager.setAdapter(adapter);
        leadersViewPager.setCurrentItem(1);
        leadersViewPager.setPadding(130, 0, 130, 20);

        colors = new Integer[]{
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimary),
        };

        leadersViewPager.setPadding(0,0,0,20);
        leadersViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (adapter.getCount() - 1) && position < (colors.length - 1)){
                    leadersViewPager.setBackgroundColor(
                            (Integer)argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                }

                else{
                    leadersViewPager.setBackgroundColor(colors[colors.length - 1] );
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private ArrayList<HighScoreModelResponse> readFromStorage(String address) {

        ArrayList<HighScoreModelResponse> storedData = null;
        FileInputStream fis;
        try {
            fis = openFileInput(address);
            ObjectInputStream ois = new ObjectInputStream(fis);
            storedData = (ArrayList<HighScoreModelResponse>) ois.readObject();

            return storedData;
        } catch (Exception e) {
            Log.d("Read Stored Info", "Read ::: " + e.getMessage());
        }

        return null;
    }

}