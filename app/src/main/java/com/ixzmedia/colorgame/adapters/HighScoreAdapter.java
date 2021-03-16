package com.ixzmedia.colorgame.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixzmedia.colorgame.R;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.MyViewHolder> {

    ArrayList<HighScoreModelResponse> scoreModelResponses;
    Context context;
    private static final String TAG = HighScoreAdapter.class.getSimpleName();

    public HighScoreAdapter(Context context, ArrayList<HighScoreModelResponse> highScoreModelResponses ) {
        this.context = context;
        this.scoreModelResponses = highScoreModelResponses;
    }

    @NonNull
    @Override
    public HighScoreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_highscore_item_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HighScoreAdapter.MyViewHolder holder, int position) {

//        holder.Name.setText(String.format(Locale.ENGLISH,"Level : %s", leaders.getGame_level().toUpperCase()));
        HighScoreModelResponse current = scoreModelResponses.get(position);

        holder.Name.setText(current.getUser_name());
        holder.Score.setText(String.format(Locale.ENGLISH,"High Score : %d", current.getHighscore()));


        holder.Date.setText(String.format("Date : %s", formatDate(current.getHighscore_date())));

        Picasso.get()
                .load(current.getPhoto_url())
                .placeholder(R.drawable.ic_baseline_person_pin_24)
                .error(R.drawable.ic_error)
                .into(holder.Thumbnail);
    }

    private  String formatDate(String highscore_date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");


        try {
            //String date = formatter.format(Date.parse(highscore_date));
           // Date date = formatter.parse(highscore_date);
            return formatter.format(Date.parse(highscore_date));
        }
        catch(Exception e) {
            Log.d(TAG, "formatDate: Error formatting Date");
            return highscore_date;
        }
    }
    @Override
    public int getItemCount() {
        return scoreModelResponses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Name, Score, Date;
        ImageView Thumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.each_highscore_item_scorer_name);
            Score = (TextView)itemView.findViewById(R.id.each_highscore_item_scorer_score);
            Date = (TextView) itemView.findViewById(R.id.each_highscore_item_scorer_date);
            Thumbnail = (ImageView)itemView.findViewById(R.id.each_highscore_item_scorer_image);
        }
    }

    public void setItems(ArrayList<HighScoreModelResponse> scoreModelResponses) {
        this.scoreModelResponses = scoreModelResponses;
    }
}
