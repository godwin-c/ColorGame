package com.ixzmedia.colorgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ixzmedia.colorgame.classes.Highscore;
import com.ixzmedia.colorgame.classes.NetworkAvaillabilityClass;
import com.ixzmedia.colorgame.classes.ViewAnimation;
import com.ixzmedia.colorgame.networkoperations.CustomCallBack;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_Client;
import com.ixzmedia.colorgame.networkoperations.Rest_DB_interface;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModel;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;
import com.ixzmedia.colorgame.networkoperations.models.MyErrorClass;
import com.ixzmedia.colorgame.networkoperations.models.UserModel;
import com.ixzmedia.colorgame.networkoperations.models.UserModelResponse;
import com.squareup.picasso.Picasso;
import com.vdx.designertoast.DesignerToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    FloatingActionButton floatingActionButton, fab_show, fab_login;
    LinearLayout profile_layout;
    boolean isRotate = false;

    TextView txt_game_share_app, app_user_username, app_user_highscore, app_user_game_level;
    ImageView app_user_profile_picture;
    CardView app_user_username_display;

    Button startGameBTN, gameInfo, gameLevel, btn_leaderboard;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    public static int RC_SIGN_IN = 10012;
    private static final String FILENAME = "all_leaders_scores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

       initViewsActions();


    }

    private void initViewsActions() {
        gameLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameLevel();
            }
        });
        gameInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameInfo();
            }
        });
        startGameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainGameActivity.class);
                startActivity(intent);
            }
        });

        ViewAnimation.init(fab_login);
        ViewAnimation.init(fab_show);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                slideUpDown(profile_layout);
                isRotate = ViewAnimation.rotateFab(v, !isRotate);
                if (isRotate) {
                    ViewAnimation.showIn(fab_show);
                    ViewAnimation.showIn(fab_login);
                } else {
                    ViewAnimation.showOut(fab_show);
                    ViewAnimation.showOut(fab_login);
                }
            }
        });

        fab_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUpDown(profile_layout);
            }
        });

        fab_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DesignerToast.Info(MainActivity.this, "Google Sign-On coming soon", Gravity.CENTER, Toast.LENGTH_SHORT);
                NetworkAvaillabilityClass networkAvaillabilityClass = new NetworkAvaillabilityClass(MainActivity.this);

                if (networkAvaillabilityClass.hasNetwork()) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Login?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    performGoogleLogin();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                } else {
                    DesignerToast.Warning(MainActivity.this, "You may not be connected to the internet", Gravity.CENTER, Toast.LENGTH_SHORT);
                }

            }
        });


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        txt_game_share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // DesignerToast.Info(MainActivity.this, "coming soon!", Gravity.CENTER, Toast.LENGTH_SHORT);
                shareApp();
            }
        });

        btn_leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DesignerToast.Info(MainActivity.this, "coming soon!", Gravity.CENTER, Toast.LENGTH_SHORT);
//                if (fileExists()) {
//                    Intent intent = new Intent(MainActivity.this, LeaderBoardActivity.class);
//                    startActivity(intent);
//                } else {
//                    downloadHighScore();
//                }
                startActivity(new Intent(MainActivity.this, LeaderBoardActivity.class));
            }
        });

        app_user_highscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadHighScore();


//                Highscore highscore = retrieveHighscore();
//                if (highscore != null){
//                    //40, Color.WHITE
//                   // String scoreToShare = "Can you beat my record in COLOR GAME?\nScore : " + highscore.getScore() + "\nLevel : " + highscore.getLevel();
//
//                    String text1 = "Can you beat my record in COLOR GAME?";
//                   String text2 = "Score : " + highscore.getScore() + "  Level : " + highscore.getLevel();
//
//                    ImageCreationClass imageCreationClass = new ImageCreationClass(text1, text2, R.drawable.other_page_bg, MainActivity.this);
//                    Bitmap bitmap = imageCreationClass.drawtextToBitmap();
//
////                    Bitmap bitmap = textAsBitmap(scoreToShare, 40, Color.WHITE);
//
//                    shareBitmap(bitmap);
//
//                }

            }
        });
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Color Interactive Game");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "share via"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    private void initViews() {
        startGameBTN = (Button) findViewById(R.id.btnStartGame);
        gameInfo = findViewById(R.id.btnGameInfo);
        gameLevel = findViewById(R.id.btnGameLevel);

        floatingActionButton = findViewById(R.id.fab_profile);
        profile_layout = findViewById(R.id.profile_layout);
        fab_show = findViewById(R.id.fab_show_profile);
        fab_login = findViewById(R.id.fab_login_user);

        txt_game_share_app = findViewById(R.id.txt_game_share_app);
        app_user_username = findViewById(R.id.app_user_username);
        app_user_highscore = findViewById(R.id.app_user_highscore);
        app_user_game_level = findViewById(R.id.app_user_game_level);

        app_user_profile_picture = findViewById(R.id.app_user_profile_picture);
        app_user_username_display = findViewById(R.id.app_user_username_display);
        btn_leaderboard = findViewById(R.id.btn_leaderboard);
    }

    private void downloadHighScore() {

        NetworkAvaillabilityClass networkAvaillabilityClass = new NetworkAvaillabilityClass(MainActivity.this);
        if (networkAvaillabilityClass.hasNetwork()) {
            Rest_DB_interface rest_db_interface = Rest_DB_Client.getClient().create(Rest_DB_interface.class);
            Call<ArrayList<HighScoreModelResponse>> call = rest_db_interface.getAllHighScores();

            call.enqueue(new CustomCallBack<>(MainActivity.this, new Callback<ArrayList<HighScoreModelResponse>>() {
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

                            Intent intent = new Intent(MainActivity.this, LeaderBoardActivity.class);
                            startActivity(intent);

                        } else {
                            DesignerToast.Info(MainActivity.this, "no high scores uploaded yet. Be the first to upload yours", Gravity.CENTER, Toast.LENGTH_SHORT);
                        }

                    } else {
                        DesignerToast.Error(MainActivity.this, "error while fetching high scores", Gravity.CENTER, Toast.LENGTH_SHORT);
                    }

                }

                @Override
                public void onFailure(Call<ArrayList<HighScoreModelResponse>> call, Throwable t) {
                    DesignerToast.Error(MainActivity.this, "error while fetching high scores '" + t.getMessage() + "'", Gravity.CENTER, Toast.LENGTH_SHORT);
                }
            }));
        } else {

            DesignerToast.Error(MainActivity.this, "looks like you are not connected to the internet", Gravity.CENTER, Toast.LENGTH_SHORT);
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

    private boolean fileExists() {
        File file = getFileStreamPath(FILENAME);
        // File file = new File(FILENAME );
        return file.exists();
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

    private void uploadHighScore() {
        NetworkAvaillabilityClass networkAvaillabilityClass = new NetworkAvaillabilityClass(MainActivity.this);

        if (networkAvaillabilityClass.hasNetwork()) {
            performUpload();
        } else {
            DesignerToast.Warning(MainActivity.this, "You may not be connected to the internet", Gravity.CENTER, Toast.LENGTH_SHORT);
        }
    }

    private void performUpload() {
        Highscore highscore = retrieveHighscore();
        if (highscore != null) {
            UserModelResponse userModelResponse = retrieveUser();
            if (userModelResponse != null) {
                Rest_DB_interface rest_db_interface = Rest_DB_Client.getClient().create(Rest_DB_interface.class);

                HighScoreModel highScoreModel = new HighScoreModel(userModelResponse.get_id(), highscore.getScore(), getSelectedLevel(), getDate(), userModelResponse.getName(), userModelResponse.getProfile_url());

                Call<HighScoreModelResponse> call = rest_db_interface.uploadHighscore(highScoreModel);
                call.enqueue(new CustomCallBack<HighScoreModelResponse>(MainActivity.this, new Callback<HighScoreModelResponse>() {
                    @Override
                    public void onResponse(Call<HighScoreModelResponse> call, Response<HighScoreModelResponse> response) {

                        if (response.code() == 201) {
                            Log.d(TAG, "onResponse:  ret " + response.body());
                            Log.d(TAG, "onResponse:  ret " + getClass().getEnclosingMethod().getName());
                            Log.d(TAG, "onResponse: " + response.code());
                            DesignerToast.Success(MainActivity.this, "High Score uploaded", Gravity.CENTER, Toast.LENGTH_SHORT);
                        } else {
                            DesignerToast.Error(MainActivity.this, "Ooops! Upload failed", Gravity.CENTER, Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onFailure(Call<HighScoreModelResponse> call, Throwable t) {

                        Log.d(TAG, "onResponse:  ret " + t.getMessage());
                        Log.d(TAG, "onResponse:  ret " + getClass().getEnclosingMethod().getName());
                    }
                }));
            } else {
                DesignerToast.Error(MainActivity.this, "you need to login before you can upload high score", Gravity.CENTER, Toast.LENGTH_SHORT);
            }
        } else {
            DesignerToast.Error(MainActivity.this, "you have no high score to upload", Gravity.CENTER, Toast.LENGTH_SHORT);
        }

    }

    private String getDate() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void shareBitmap(@NonNull Bitmap bitmap) {
        //---Save bitmap to external cache directory---//
        //get cache directory
        File cachePath = new File(getExternalCacheDir(), "my_images/");
        cachePath.mkdirs();

        //create png file
        File file = new File(cachePath, "color_game_highscore.png");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    private void performGoogleLogin() {
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showGameInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_info_layout, null);
        builder.setView(dialogView);

        final Button dismissButton = dialogView.findViewById(R.id.btn_game_info_dismiss);
        final AlertDialog alertDialog = builder.create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);


        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showGameLevel() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_level_select_layout, null);
        builder.setView(dialogView);

        final Button selectButton = dialogView.findViewById(R.id.game_level_select_dismiss);
        final AlertDialog alertDialog = builder.create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        RadioGroup radioGroup = dialogView.findViewById(R.id.game_level_radiogroup);
        final RadioButton simpleGameBTN = dialogView.findViewById(R.id.radiobutton_simple);
        final RadioButton hardGameBTN = dialogView.findViewById(R.id.radiobutton_hard);
        final RadioButton mediumGame = dialogView.findViewById(R.id.radiobutton_medium);

        if (getSelectedLevel().equalsIgnoreCase("hard")) {
            hardGameBTN.setChecked(true);
        } else if (getSelectedLevel().equalsIgnoreCase("simple")) {
            simpleGameBTN.setChecked(true);
        } else {
            mediumGame.setChecked(true);
        }

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simpleGameBTN.isChecked()) {
                    DesignerToast.Success(MainActivity.this, "Level 'SIMPLE' is selected", Gravity.CENTER, Toast.LENGTH_SHORT);
                    storeSelectedLevel("simple");
                } else if (hardGameBTN.isChecked()) {
                    DesignerToast.Success(MainActivity.this, "Level 'HARD' is selected", Gravity.CENTER, Toast.LENGTH_SHORT);
                    storeSelectedLevel("hard");
                } else if (mediumGame.isChecked()) {
                    DesignerToast.Success(MainActivity.this, "Level 'Medium' is selected", Gravity.CENTER, Toast.LENGTH_SHORT);
                    storeSelectedLevel("medium");
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private String getSelectedLevel() {
        SharedPreferences preferences = getSharedPreferences("color_game_level", MODE_PRIVATE);

        return preferences.getString("level", "simple");
    }

    private void storeSelectedLevel(String level) {
        SharedPreferences preferences = getSharedPreferences("color_game_level", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("level", level);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (!isPanelShown(profile_layout)) {
            super.onBackPressed();
        } else {
            slideUpDown(profile_layout);
        }
    }

    private void slideUpDown(LinearLayout layout) {

        if (!isPanelShown(layout)) {

            // Show the panel

            Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this,

                    R.anim.bottom_up);


            Highscore highscore = retrieveHighscore();
            if (highscore != null) {
                app_user_game_level.setText("Game Level : " + highscore.getLevel().toUpperCase());
                app_user_highscore.setText("High Score : " + highscore.getScore());

            } else {
                app_user_game_level.setText("Game Level : " + getSelectedLevel().toUpperCase());
                app_user_highscore.setText("No highscores set");
            }

            UserModelResponse userModelResponse = retrieveUser();
            if (userModelResponse != null) {
                setUserDetails(userModelResponse.getProfile_url(), userModelResponse.getName());
            }

            enableButtons(false);

            layout.startAnimation(bottomUp);

            layout.setVisibility(View.VISIBLE);


        } else {

            // Hide the Panel

            Animation bottomDown = AnimationUtils.loadAnimation(MainActivity.this,

                    R.anim.bottom_down);

            enableButtons(true);

            layout.startAnimation(bottomDown);

            layout.setVisibility(View.GONE);

        }

    }

    private void enableButtons(boolean b) {
        startGameBTN.setEnabled(b);
        gameInfo.setEnabled(b);
        gameLevel.setEnabled(b);
    }


    private boolean isPanelShown(LinearLayout layout) {

        return layout.getVisibility() == View.VISIBLE;

    }

    private void setUserDetails(String url, String name) {
        Log.d(TAG, "setUserDetails: URL : " + url);
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_baseline_person_pin_24)
                .error(R.drawable.ic_error)
                .into(app_user_profile_picture);

        app_user_username.setText(name);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.d(getLocalClassName(), "handleSignInResult: NAme : " + account.getDisplayName());
            Log.d(getLocalClassName(), "handleSignInResult: Email : " + account.getEmail());
            Log.d(getLocalClassName(), "handleSignInResult: Image : " + account.getPhotoUrl());

//            AppUsers user = new AppUsers(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());

            setUserDetails(account.getPhotoUrl().toString(), account.getDisplayName());

            UserModel model = new UserModel(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());
            recordUser(model);

//            saveUserInfo(user);


//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(getLocalClassName(), "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void recordUser(UserModel model) {

        Rest_DB_interface rest_db_interface = Rest_DB_Client.getClient().create(Rest_DB_interface.class);
        Call<UserModelResponse> call = rest_db_interface.registerUser(model);

        call.enqueue(new CustomCallBack<UserModelResponse>(MainActivity.this, new Callback<UserModelResponse>() {
            @Override
            public void onResponse(Call<UserModelResponse> call, Response<UserModelResponse> response) {

                if (response.code() == 201) {

                    Log.d(TAG, "onResponse:  ret " + response.body());
                    Log.d(TAG, "onResponse:  ret " + getClass().getEnclosingMethod().getName());
                    Log.d(TAG, "onResponse: " + response.code());

                    DesignerToast.Success(MainActivity.this, "login Success", Gravity.CENTER, Toast.LENGTH_SHORT);

                    saveUserInfo(response.body());

                } else {
                    Log.d(TAG, "onResponse: " + response.code());
                    Gson gson = new Gson();
                    Type type = new TypeToken<MyErrorClass>() {
                    }.getType();
                    MyErrorClass errorResponse = gson.fromJson(response.errorBody().charStream(), type);

                    Log.d(TAG, "onResponse:  ret " + errorResponse.getMessage());
                    Log.d(TAG, "onResponse:  ret " + getClass().getEnclosingMethod().getName());
                    Log.d(TAG, "onResponse: " + response.code());

                    HashMap hashMap = errorResponse.getList().get(0);
                    String field = (String) hashMap.get("field");
                    ArrayList<String> messages = (ArrayList<String>) hashMap.get("message");
                    String message = messages.get(0);

                    if (!errorResponse.getName().equalsIgnoreCase("ValidationError")) {
                        DesignerToast.Error(MainActivity.this, field + " " + message, Gravity.CENTER, Toast.LENGTH_SHORT);
                    } else {
                        DesignerToast.Success(MainActivity.this, "login Success", Gravity.CENTER, Toast.LENGTH_SHORT);
                    }

                }
            }

            @Override
            public void onFailure(Call<UserModelResponse> call, Throwable t) {
                Log.d(TAG, "onResponse:  ret " + t.getMessage());
                Log.d(TAG, "onResponse:  ret " + getClass().getEnclosingMethod().getName());
            }
        }));

    }

    private void saveUserInfo(UserModelResponse modelResponse) {
        SharedPreferences preferences = getSharedPreferences("current_app_user", Context.MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(modelResponse);
        prefsEditor.putString("app_user", json);
        prefsEditor.apply();
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

    private Highscore retrieveHighscore() {
        SharedPreferences preferences = getSharedPreferences("user_highscore", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = preferences.getString("current_highscore", "");
        if (json.equals("")) {

            return null;
        }

        return gson.fromJson(json, Highscore.class);
    }
}
