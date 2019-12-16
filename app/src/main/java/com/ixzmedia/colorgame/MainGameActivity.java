package com.ixzmedia.colorgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.ixzmedia.colorgame.classes.MyColors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainGameActivity extends AppCompatActivity {


    private MaterialButton[] allButtons = new MaterialButton[8];
    private int[] allButtonIds = {R.id.BTN_01, R.id.BTN_02, R.id.BTN_03, R.id.BTN_04, R.id.BTN_05, R.id.BTN_06, R.id.BTN_07, R.id.BTN_08};
    TextView timer_view;
    CounterClass timer;
    ArrayList<MyColors> myColors;
    MyColors coloToCheck;

    private static final String TAG = MainGameActivity.class.getSimpleName();
    Button testPlain;
    TextView scoreView;
    int score;

    boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        timer_view = (TextView) findViewById(R.id.txt_timerview);
        scoreView = (TextView) findViewById(R.id.txt_scoreview);

        testPlain = (Button) findViewById(R.id.imv_color_in_test);

        startGame();

    }

    private void startGame() {

        score = 0;
        scoreView.setText("00 Points");

        myColors = new ArrayList<MyColors>();

        JSONObject colors_in_json;
        try {
            colors_in_json = new JSONObject(loadJSONFromAsset());

            JSONArray array_of_colors = colors_in_json.getJSONArray("colors");

            for (int i = 0; i < array_of_colors.length(); i++) {
                JSONObject eachColorObject = array_of_colors.getJSONObject(i);
                MyColors myColor = new MyColors(eachColorObject.getString("id"), eachColorObject.getString("name"), eachColorObject.getString("code"));
                myColors.add(myColor);
            }
        } catch (JSONException e) {
            Log.d(TAG, "startGame: Error in Json Read" + e.getMessage());
        }

        if (timer != null) {
//            fromTimerCancel = false;
            timer.cancel();
        }

        timer = new CounterClass(getTimeLimit(), getCountDownStep());
        timer.start();
    }

    private long getCountDownStep(){
        int countDownStepInSeconds = 3;
        long countDownStep = TimeUnit.SECONDS.toMillis(countDownStepInSeconds);
        return countDownStep;
    }

    private long getTimeLimit(){
        int timeLimitInSeconds = 31;
        long timeLimit = TimeUnit.SECONDS.toMillis(timeLimitInSeconds);
        return timeLimit;
    }

    private int generateRandomNumber(ArrayList arrayList) {
        Random rand = new Random();
        int n = rand.nextInt(arrayList.size());
        return n;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("my_colors.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            clicked = false;
            enableAllButtons();

            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            timer_view.setText(hms);

            ArrayList<MyColors> colorsInFocus = fetchEightColors();
            coloToCheck = colorsInFocus.get(generateRandomNumber(colorsInFocus));
            testPlain.setBackgroundColor(Color.parseColor(coloToCheck.getCode()));

            for (int i = 0; i < allButtons.length; i++) {
                allButtons[i] = (MaterialButton) findViewById(allButtonIds[i]);
                final MyColors eachOfTheEightColors = colorsInFocus.get(i);
                allButtons[i].setText(eachOfTheEightColors.getName());

                allButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (eachOfTheEightColors.getCode().equals(coloToCheck.getCode())) {
                            if (!clicked) {
                                score = score + 5;
                                clicked = true;
                            }

                            scoreView.setText(score + " points");
                            Snackbar.make(v,"Correct! The Color is " + eachOfTheEightColors.getName(), Snackbar.LENGTH_SHORT).show();
//                            Toast.makeText(MainGameActivity.this, "Correct! The Color is " + eachOfTheEightColors.getName(), Toast.LENGTH_SHORT).show();
                        }

                        disableAllButtons();
                    }
                });


            }


        }

        @Override
        public void onFinish() {
            disableAllButtons();
            timer_view.setText("00:00");
            GameOver();
        }
    }

    private void disableAllButtons() {
        for (int i = 0; i < allButtons.length; i++) {
            allButtons[i] = (MaterialButton) findViewById(allButtonIds[i]);

            allButtons[i].setEnabled(false);
        }
    }

    private void enableAllButtons(){
        for (int i = 0; i < allButtons.length; i++) {
            allButtons[i] = (MaterialButton) findViewById(allButtonIds[i]);

            allButtons[i].setEnabled(true);
        }
    }

    private ArrayList<MyColors> fetchEightColors() {
        ArrayList<MyColors> eightColors = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            eightColors.add(myColors.get(generateRandomNumber(myColors)));
        }

        return eightColors;
    }

    private void GameOver() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainGameActivity.this);
        alertDialogBuilder
                .setMessage("You scored " + score + " out of 50 possible points. \nPlay Game again?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        timer.cancel();

                        dialog.dismiss();
                        startGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        System.exit(0);
//                        fromTimerCancel = false;
                        timer.cancel();

                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialogBuilder.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null){
            timer.cancel();
        }
    }
}
