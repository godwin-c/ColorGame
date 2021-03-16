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

public class LeaderBoardActivity extends AppCompatActivity {

    private static final String TAG = LeaderBoardActivity.class.getSimpleName();
    // tab titles
    private String[] titles = new String[]{"Easy", "Medium", "Hard"};

    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

   // ViewPager leaderBoardViewPager;
    public static TabLayout tabLayout;
    ViewPager leadersViewPager, tabsViewPager;
    TabsAdapter tabsAdapter;
    EachOfThreeAdapter adapter;

    Integer[] colors = null;

    HighScoreModelResponse topThreeLeader;
    ArrayList<HighScoreModelResponse> topThreeLeaders;
    //SwipeRefreshLayout swipe_to_refresh_leader_board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        initViews();
        setActionOnViewItems();



        if (fileExist("top_three_high_scores")){
            Log.d(TAG, "onCreate: File Exists");
            setupThreeLeaders();
        }


    }

    private void setActionOnViewItems() {

        //Action on Tab
        tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[1]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[2]));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Action on Tab Adaptor
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        tabsViewPager.setAdapter(tabsAdapter);
        tabsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Action on Tabs Layout
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

        //Action on SwipeRefresh
//        swipe_to_refresh_leader_board.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                refreshLeaderBoard();
//            }
//        });
//        swipe_to_refresh_leader_board.setColorSchemeResources(
//                android.R.color.holo_blue_bright,
//
//                android.R.color.holo_green_light,
//
//                android.R.color.holo_orange_light,
//
//                android.R.color.holo_red_light);
    }

    private void initViews() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        leadersViewPager = findViewById(R.id.leaders_viewpager);
        tabsViewPager = findViewById(R.id.tab_layout_viewpager);
        //swipe_to_refresh_leader_board = findViewById(R.id.swipe_to_refresh_leader_board);
    }


    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    private void setupThreeLeaders() {
        topThreeLeaders = readFromStorage("top_three_high_scores");
        Log.d(TAG, "setupThreeLeaders: Top Three High Score : " + topThreeLeaders.get(0).getHighscore());
        
        adapter = new EachOfThreeAdapter(topThreeLeaders, LeaderBoardActivity.this);
        Log.d(TAG, "setupThreeLeaders: Initialize Adapter");
        
        leadersViewPager.setAdapter(adapter);
        Log.d(TAG, "setupThreeLeaders: Set Adapter");
        
        leadersViewPager.setCurrentItem(1);
        Log.d(TAG, "setupThreeLeaders: Set Current Item");
        
        leadersViewPager.setPadding(60, 0, 60, 0);
        Log.d(TAG, "setupThreeLeaders: Set Padding");

        leadersViewPager.setPageMargin(10);
        colors = new Integer[]{
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white),
        };

        //leadersViewPager.setPadding(0,0,0,20);
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

    public boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
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