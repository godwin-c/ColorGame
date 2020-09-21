package com.ixzmedia.colorgame.classes;

public class TopThreeLeaders {
    private String leaderName;
    private String leaderPixURL;
    private String leaderScore;
    private String leaderLevel;


    public TopThreeLeaders(String leaderName, String leaderPixURL, String leaderScore, String leaderLevel) {
        this.leaderName = leaderName;
        this.leaderPixURL = leaderPixURL;
        this.leaderScore = leaderScore;
        this.leaderLevel = leaderLevel;
    }

    public String getLeaderPixURL() {
        return leaderPixURL;
    }

    public void setLeaderPixURL(String leaderPixURL) {
        this.leaderPixURL = leaderPixURL;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderScore() {
        return leaderScore;
    }

    public void setLeaderScore(String leaderScore) {
        this.leaderScore = leaderScore;
    }

    public String getLeaderLevel() {
        return leaderLevel;
    }

    public void setLeaderLevel(String leaderLevel) {
        this.leaderLevel = leaderLevel;
    }
}
