package com.ixzmedia.colorgame.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import com.ixzmedia.colorgame.R;
import com.ixzmedia.colorgame.classes.TopThreeLeaders;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EachOfThreeAdapter extends PagerAdapter {

    private List<HighScoreModelResponse> topThreeLeaders;
    private LayoutInflater layoutInflater;
    private Context context;

    public EachOfThreeAdapter(List<HighScoreModelResponse> topThreeLeaders, Context context) {
        this.topThreeLeaders = topThreeLeaders;
        this.context = context;
    }

    @Override
    public int getCount() {

        return topThreeLeaders.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.each_leader_of_three_layout, container, false);


        ImageView leader_Image = view.findViewById(R.id.each_leader_of_three_scorer_image);
        TextView leader_name = view.findViewById(R.id.each_leader_of_three_score_shirt);
        TextView leader_level = view.findViewById(R.id.each_leader_of_three_game_level);
        TextView leader_score = view.findViewById(R.id.each_leader_of_three_score);
        TextView each_leader_of_three_date = view.findViewById(R.id.each_leader_of_three_date);

        HighScoreModelResponse leaders = topThreeLeaders.get(position);

        leader_level.setText(String.format(Locale.ENGLISH,"Level : %s", leaders.getGame_level().toUpperCase()));
        leader_name.setText(leaders.getUser_name());
        leader_score.setText(String.format(Locale.ENGLISH,"High Score : %d", leaders.getHighscore()));

      // Date date = formartDate(leaders)
        each_leader_of_three_date.setText(String.format("Date : %s", leaders.getHighscore_date()));

        Picasso.get()
                .load(leaders.getPhoto_url())
                .placeholder(R.drawable.ic_baseline_person_pin_24)
                .error(R.drawable.ic_error)
                .into(leader_Image);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
