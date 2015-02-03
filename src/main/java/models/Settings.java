package models;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Entity;

import de.svenkubiak.ninja.morphia.MorphiaModel;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "settings", noClassnameStored = true)
public class Settings extends MorphiaModel implements Serializable {
    private static final long serialVersionUID = 864836696835606862L;

    private String appSalt;
    private String appName;
    private String gameName;

    private int pointsGameWin;
    private int pointsGameDraw;
    private int pointsTip;
    private int pointsTipDiff;
    private int pointsTipTrend;
    private int minutesBeforeTip;
    private int numPrePlayoffGames;
    private int numPlayoffTeams;

    private boolean informOnNewTipper;
    private boolean playoffs;
    private boolean enableRegistration;
    
    public String getAppSalt() {
        return appSalt;
    }

    public void setAppSalt(final String appSalt) {
        this.appSalt = appSalt;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(final String gameName) {
        this.gameName = gameName;
    }

    public int getPointsGameWin() {
        return pointsGameWin;
    }

    public void setPointsGameWin(final int pointsGameWin) {
        this.pointsGameWin = pointsGameWin;
    }

    public int getPointsGameDraw() {
        return pointsGameDraw;
    }

    public void setPointsGameDraw(final int pointsGameDraw) {
        this.pointsGameDraw = pointsGameDraw;
    }

    public int getPointsTip() {
        return pointsTip;
    }

    public void setPointsTip(final int pointsTip) {
        this.pointsTip = pointsTip;
    }

    public int getPointsTipDiff() {
        return pointsTipDiff;
    }

    public void setPointsTipDiff(final int pointsTipDiff) {
        this.pointsTipDiff = pointsTipDiff;
    }

    public int getPointsTipTrend() {
        return pointsTipTrend;
    }

    public void setPointsTipTrend(final int pointsTipTrend) {
        this.pointsTipTrend = pointsTipTrend;
    }

    public int getMinutesBeforeTip() {
        return minutesBeforeTip;
    }

    public void setMinutesBeforeTip(final int minutesBeforeTip) {
        this.minutesBeforeTip = minutesBeforeTip;
    }

    public int getNumPrePlayoffGames() {
        return numPrePlayoffGames;
    }

    public void setNumPrePlayoffGames(final int numPrePlayoffGames) {
        this.numPrePlayoffGames = numPrePlayoffGames;
    }

    public int getNumPlayoffTeams() {
        return numPlayoffTeams;
    }

    public void setNumPlayoffTeams(final int numPlayoffTeams) {
        this.numPlayoffTeams = numPlayoffTeams;
    }

    public boolean isInformOnNewTipper() {
        return informOnNewTipper;
    }

    public void setInformOnNewTipper(final boolean informOnNewTipper) {
        this.informOnNewTipper = informOnNewTipper;
    }

    public boolean isPlayoffs() {
        return playoffs;
    }

    public void setPlayoffs(final boolean playoffs) {
        this.playoffs = playoffs;
    }

    public boolean isEnableRegistration() {
        return enableRegistration;
    }

    public void setEnableRegistration(final boolean enableRegistration) {
        this.enableRegistration = enableRegistration;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(final String appName) {
        this.appName = appName;
    }
}