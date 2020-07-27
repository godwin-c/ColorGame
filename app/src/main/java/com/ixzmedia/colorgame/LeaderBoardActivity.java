package com.ixzmedia.colorgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.ixzmedia.colorgame.adapters.TabsAdapter;

public class LeaderBoardActivity extends AppCompatActivity {

    // tab titles
    private String[] titles = new String[]{"Easy", "Medium", "Hard"};

    ViewPager vp;
    public static TabLayout tabLayout;
    ViewPager leadersViewPager, tabsViewPager;
    TabsAdapter tabsAdapter;

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
    }
}