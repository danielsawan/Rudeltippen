package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import models.enums.Avatar;
import models.statistic.ResultStatistic;
import models.statistic.UserStatistic;
import ninja.morphia.MorphiaModel;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "users", noClassnameStored = true)
public class User extends MorphiaModel implements Serializable {
    private static final long serialVersionUID = 5381913315282528298L;
    
    @Reference(value = "user_gametips", lazy = true)
    private List<GameTip> gameTips;

    @Reference(value = "user_extratips", lazy = true)
    private List<ExtraTip> extraTips;

    @Reference(value = "user_confirmation", lazy = true)
    private List<Confirmation> confirmations;

    @Reference(value = "user_userstatistics", lazy = true)
    private List<UserStatistic> userStatistics;

    @Reference(value = "user_resultstatistics", lazy = true)
    private List<ResultStatistic> resultStatistic;
    
    @Embedded
    private Avatar avatar;

    private String userpass;
    private String username;
    private String email;
    private String salt;
    private String picture;

    private Date registered;

    private boolean reminder;
    private boolean admin;
    private boolean active;
    private boolean notification;
    private boolean sendStandings;
    private boolean sendGameTips;

    private int tipPoints;
    private int extraPoints;
    private int points;
    private int place;
    private int previousPlace;
    private int correctResults;
    private int correctDifferences;
    private int correctTrends;
    private int correctExtraTips;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getUserpass() {
        return this.userpass;
    }

    public void setUserpass(final String userpass) {
        this.userpass = userpass;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(final String salt) {
        this.salt = salt;
    }

    public Date getRegistered() {
        return this.registered;
    }

    public void setRegistered(final Date registered) {
        this.registered = registered;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(final String picture) {
        this.picture = picture;
    }

    public List<GameTip> getGameTips() {
        return this.gameTips;
    }

    public void setGameTips(final List<GameTip> gameTips) {
        this.gameTips = gameTips;
    }

    public List<ExtraTip> getExtraTips() {
        return this.extraTips;
    }

    public void setExtraTips(final List<ExtraTip> extraTips) {
        this.extraTips = extraTips;
    }

    public List<Confirmation> getConfirmations() {
        return this.confirmations;
    }

    public void setConfirmations(final List<Confirmation> confirmations) {
        this.confirmations = confirmations;
    }

    public boolean isReminder() {
        return this.reminder;
    }

    public void setReminder(final boolean reminder) {
        this.reminder = reminder;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public int getTipPoints() {
        return this.tipPoints;
    }

    public void setTipPoints(final int tipPoints) {
        this.tipPoints = tipPoints;
    }

    public int getExtraPoints() {
        return this.extraPoints;
    }

    public void setExtraPoints(final int extraPoints) {
        this.extraPoints = extraPoints;
    }

    public int getPlace() {
        return this.place;
    }

    public void setPlace(final int place) {
        this.place = place;
    }

    public boolean isNotification() {
        return this.notification;
    }

    public void setNotification(final boolean notification) {
        this.notification = notification;
    }

    public int getCorrectResults() {
        return this.correctResults;
    }

    public void setCorrectResults(final int correctResults) {
        this.correctResults = correctResults;
    }

    public int getCorrectDifferences() {
        return this.correctDifferences;
    }

    public void setCorrectDifferences(final int correctDifferences) {
        this.correctDifferences = correctDifferences;
    }

    public int getCorrectTrends() {
        return this.correctTrends;
    }

    public void setCorrectTrends(final int correctTrends) {
        this.correctTrends = correctTrends;
    }

    public int getCorrectExtraTips() {
        return this.correctExtraTips;
    }

    public void setCorrectExtraTips(final int correctExtraTips) {
        this.correctExtraTips = correctExtraTips;
    }

    public int getPreviousPlace() {
        return this.previousPlace;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public void setPreviousPlace(final int previousPlace) {
        this.previousPlace = previousPlace;
    }

    public boolean isSendStandings() {
        return this.sendStandings;
    }

    public void setSendStandings(final boolean sendStandings) {
        this.sendStandings = sendStandings;
    }

    public List<UserStatistic> getUserStatistics() {
        return userStatistics;
    }

    public void setUserStatistics(List<UserStatistic> userStatistics) {
        this.userStatistics = userStatistics;
    }

    public List<ResultStatistic> getResultStatistic() {
        return resultStatistic;
    }

    public void setResultStatistic(List<ResultStatistic> resultStatistic) {
        this.resultStatistic = resultStatistic;
    }

    public boolean isSendGameTips() {
        return sendGameTips;
    }

    public void setSendGameTips(boolean sendGameTips) {
        this.sendGameTips = sendGameTips;
    }
}