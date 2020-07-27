package com.ixzmedia.colorgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ixzmedia.colorgame.classes.Highscore;
import com.ixzmedia.colorgame.classes.ImageCreationClass;
import com.ixzmedia.colorgame.classes.MyColors;
import com.ixzmedia.colorgame.classes.NetworkAvaillabilityClass;
import com.ixzmedia.colorgame.networkoperations.CustomCallBack;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_Client;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_interface;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModel;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;
import com.ixzmedia.colorgame.networkoperations.models.UserModelResponse;
import com.vdx.designertoast.DesignerToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainGameActivity extends AppCompatActivity {


    private MaterialButton[] allButtons = new MaterialButton[8];
    private int[] allButtonIds = {R.id.BTN_01, R.id.BTN_02, R.id.BTN_03, R.id.BTN_04, R.id.BTN_05, R.id.BTN_06, R.id.BTN_07, R.id.BTN_08};
    TextView timer_view;
    CounterClass timer;

    Vibrator vibrator;

    ArrayList<MyColors> myColors;
    MyColors coloToCheck;

    private static final String TAG = MainGameActivity.class.getSimpleName();
    Button testPlain;
    TextView scoreView;
    int score;

    String gameLevel;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        timer_view = (TextView) findViewById(R.id.txt_timerview);
        scoreView = (TextView) findViewById(R.id.txt_scoreview);

        testPlain = (Button) findViewById(R.id.imv_color_in_test);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d(TAG, "onInitializationComplete: COMPLETED");
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                if (timer != null){
                    timer.cancel();
                }

                finish();
            }
        });

        startGame();

    }

    private void startGame(){

        gameLevel = getSelectedLevel();
        startAllGame();
    }


    private String getSelectedLevel(){
        SharedPreferences preferences = getSharedPreferences("color_game_level",MODE_PRIVATE);

        String level = preferences.getString("level","simple");
        return level;
    }

    private void startAllGame() {

        score = 0;
        scoreView.setText("00 Points");

        myColors = new ArrayList<MyColors>();

        JSONObject colors_in_json;
        try {
            colors_in_json = new JSONObject(loadJSONFromAsset());

            JSONArray array_of_colors = colors_in_json.getJSONArray("colors");

            for (int i = 0; i < array_of_colors.length(); i++) {
                JSONObject eachColorObject = array_of_colors.getJSONObject(i);
                MyColors myColor = new MyColors(eachColorObject.getString("_id"), eachColorObject.getString("name"), eachColorObject.getString("code"));
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
        int countDownStepInSeconds;
        if (gameLevel.equalsIgnoreCase("hard")) {
            countDownStepInSeconds = 3;
        } else if (gameLevel.equalsIgnoreCase("simple")) {
            countDownStepInSeconds = 3;
        } else {
            countDownStepInSeconds = 1;
        }
//        int countDownStepInSeconds = 3;
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
            InputStream is = getAssets().open("new_colors.json");
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
        //String level;

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
            Log.d(TAG, "onTick: " + coloToCheck.getCode());
            testPlain.setBackgroundColor(Color.parseColor(coloToCheck.getCode()));

            for (int i = 0; i < allButtons.length; i++) {
                allButtons[i] = (MaterialButton) findViewById(allButtonIds[i]);
                final MyColors eachOfTheEightColors = colorsInFocus.get(i);

                if (gameLevel.equalsIgnoreCase("hard")){
                    allButtons[i].setText(eachOfTheEightColors.getName());
                }else{
                    allButtons[i].setBackgroundColor(Color.parseColor(eachOfTheEightColors.getCode()));
                }

                allButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (eachOfTheEightColors.getCode().equals(coloToCheck.getCode())) {
                            if (!clicked) {
                                score = score + 5;
                                clicked = true;
                            }

                            scoreView.setText(score + " points");
                           // Snackbar.make(v,"Correct! The Color is " + eachOfTheEightColors.getName(), Snackbar.LENGTH_SHORT).show();
                           // DesignerToast.Success(MainGameActivity.this,"Correct! The Color is " + eachOfTheEightColors.getName(),Gravity.TOP,Toast.LENGTH_SHORT);
//                            Toast.makeText(MainGameActivity.this, "Correct! The Color is " + eachOfTheEightColors.getName(), Toast.LENGTH_SHORT).show();
                        }else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                            }else{
                                vibrator.vibrate(500);
                            }
                            //DesignerToast.Error(MainGameActivity.this,"Right answer is '" + coloToCheck.getName() + "'", Gravity.TOP,Toast.LENGTH_SHORT);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_over_display_layout,null);
        builder.setView(dialogView);

        final TextView exitButton = dialogView.findViewById(R.id.btn_game_over_exit_game);
        final Button playAgainButton = dialogView.findViewById(R.id.btn_game_over_play_again);
        final TextView txtScoreView = dialogView.findViewById(R.id.txt_game_score_display);
        final TextView shareScore = dialogView.findViewById(R.id.txt_game_share_score);

        final AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        int highestPoint;
        if (gameLevel.equalsIgnoreCase("medium")){
            highestPoint = 150;
        }else {
            highestPoint = 50;
        }
        final String scoreText = "You scored " + score + " out of " + highestPoint + " possible points.";
        txtScoreView.setText(scoreText);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();

                alertDialog.dismiss();
                startGame();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                timer.cancel();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    finish();
                }
            }
        });

        final String shareText = "I just got a high score of " + score + " out of " + highestPoint +  " possible points, on Game level '" + getSelectedLevel().toUpperCase() + "'\nDo you think you can do better?\nShare your high score with me";
        shareScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Share
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, shareText);
//                intent.putExtra(Intent.EXTRA_SUBJECT,"Color Game High score");
//                startActivity(Intent.createChooser(intent,"Share Via"));

                //uploadHighScore();


                //String text1 = "Can you beat my record in COLOR GAME?";
                String text2 = "Beat my COLOR GAME high Score : " + score + "  Level : " + getSelectedLevel();

                ImageCreationClass imageCreationClass = new ImageCreationClass(text2, R.drawable.plain_bg_color_game, MainGameActivity.this);
//                Bitmap bitmap = imageCreationClass.drawtextToBitmap();

//                Bitmap bitmap = imageCreationClass.secondDraw();
                Bitmap bitmap = imageCreationClass.textAsBitmap();
//                    Bitmap bitmap = textAsBitmap(scoreToShare, 40, Color.WHITE);

                shareBitmap(bitmap);

            }
        });

        sortHighscore();

        alertDialog.show();

//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainGameActivity.this);
//        alertDialogBuilder
//                .setMessage("You scored " + score + " out of 50 possible points. \nPlay Game again?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        timer.cancel();
//
//                        dialog.dismiss();
//                        startGame();
//                    }
//                })
//                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        System.exit(0);
////                        fromTimerCancel = false;
//                        timer.cancel();
//
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
//        alertDialogBuilder.show();

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void shareBitmap(@NonNull Bitmap bitmap)
    {
        //---Save bitmap to external cache directory---//
        //get cache directory
        File cachePath = new File(getExternalCacheDir(), "my_images/");
        cachePath.mkdirs();

        //create png file
        File file = new File(cachePath, "color_game_highscore.png");
        FileOutputStream fileOutputStream;
        try
        {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //---Share File---//
        //get file uri
        Uri myImageFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

        //create a intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    //method to convert your text to image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void uploadHighScore() {
        NetworkAvaillabilityClass networkAvaillabilityClass = new NetworkAvaillabilityClass(MainGameActivity.this);

        if (networkAvaillabilityClass.hasNetwork()){
            performUpload();
        }else{
            DesignerToast.Warning(MainGameActivity.this,"You may not be connected to the internet",Gravity.CENTER,Toast.LENGTH_SHORT);
        }
    }

    private void performUpload() {
        UserModelResponse userModelResponse = retrieveUser();
        if (userModelResponse != null){
            Rest_DB_interface rest_db_interface = Rest_DB_Client.getClient().create(Rest_DB_interface.class);

            HighScoreModel highScoreModel = new HighScoreModel(userModelResponse.get_id(),score,getSelectedLevel(), getDate(), userModelResponse.getName(),userModelResponse.getProfile_url());

            Call<HighScoreModelResponse> call = rest_db_interface.uploadHighscore(highScoreModel);
            call.enqueue(new CustomCallBack<HighScoreModelResponse>(MainGameActivity.this, new Callback<HighScoreModelResponse>() {
                @Override
                public void onResponse(Call<HighScoreModelResponse> call, Response<HighScoreModelResponse> response) {

                    if (response.code() == 201) {
                        Log.d(TAG, "onResponse:  ret " + response.body());
                        Log.d(TAG, "onResponse:  ret " + getClass().getEnclosingMethod().getName());
                        Log.d(TAG, "onResponse: " + response.code());
                        DesignerToast.Success(MainGameActivity.this,"High Score uploaded", Gravity.CENTER,Toast.LENGTH_SHORT);
                    }else {
                        DesignerToast.Error(MainGameActivity.this, "Ooops! Upload failed", Gravity.CENTER,Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(Call<HighScoreModelResponse> call, Throwable t) {

                    Log.d(TAG, "onResponse:  ret " + t.getMessage());
                    Log.d(TAG, "onResponse:  ret " + getClass().getEnclosingMethod().getName());
                }
            }));
        }else {
            DesignerToast.Error(MainGameActivity.this,"you need to login before you can upload high score",Gravity.CENTER,Toast.LENGTH_SHORT);
        }
    }

    private String getDate() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null){
            timer.cancel();
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
    }

    private void sortHighscore(){
        Highscore highscore = retrieveHighscore();
        String presentLevel = getSelectedLevel();
        if (highscore != null){
            String level = highscore.getLevel();
            int scoreTocheck = highscore.getScore();

            if (level.equalsIgnoreCase(presentLevel)){
                if (score > scoreTocheck){
                    Highscore newHighscore = new Highscore(level,score);
                    storeHighScore(newHighscore);
                }
            }else{
                Highscore newHighscore = new Highscore(presentLevel,score);
                storeHighScore(newHighscore);
            }
        }else{
            Highscore newHighscore = new Highscore(presentLevel,score);
            storeHighScore(newHighscore);
        }
    }

    private void storeHighScore(Highscore highscore){

        SharedPreferences preferences = getSharedPreferences("user_highscore", Context.MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(highscore);
        prefsEditor.putString("current_highscore", json);
        prefsEditor.commit();
    }

    private Highscore retrieveHighscore(){
        SharedPreferences preferences = getSharedPreferences("user_highscore", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString("current_highscore", "");
        if (json.equals("")) {

            return null;
        }

        return gson.fromJson(json, Highscore.class);
    }

    private UserModelResponse retrieveUser() {

        SharedPreferences preferences = getSharedPreferences("current_app_user", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString("app_user", "");
        if (json.equals("")) {

            return null;
        }

        return gson.fromJson(json, UserModelResponse.class);
    }

}
