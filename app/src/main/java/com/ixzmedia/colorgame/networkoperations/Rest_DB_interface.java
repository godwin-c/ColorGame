package com.ixzmedia.colorgame.networkoperations;

import com.ixzmedia.colorgame.networkoperations.models.HighScoreModel;
import com.ixzmedia.colorgame.networkoperations.models.HighScoreModelResponse;
import com.ixzmedia.colorgame.networkoperations.models.UserModel;
import com.ixzmedia.colorgame.networkoperations.models.UserModelResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Rest_DB_interface {

    @POST("color-game-users")
    Call<UserModelResponse> registerUser(@Body UserModel userModel);

    @POST("color-game-highscores")
    Call<HighScoreModelResponse> uploadHighscore(@Body HighScoreModel highScoreModel);

    @GET("color-game-highscores")
    Call<HighScoreModelResponse> getallHighScores();
}
