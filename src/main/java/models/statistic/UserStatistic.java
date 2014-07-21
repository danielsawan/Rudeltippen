package models.statistic;

import java.io.Serializable;

import models.Playday;
import models.User;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "userstatistics", noClassnameStored = true)
public class UserStatistic implements Serializable {
    private static final long serialVersionUID = -1721254237172590008L;

    @Id
    private ObjectId id;

    @Reference
    private Playday playday;

    @Reference
    private User user;

    private int place;
    private int points;
    private int correctTips;
    private int correctTrends;
    private int correctDiffs;
    private int playdayCorrectTips;
    private int playdayCorrectTrends;
    private int playdayCorrectDiffs;
    private int playdayPoints;
    private int playdayPlace;

    public ObjectId getId() {
        return id;
    }

    public Playday getPlayday() {
        return this.playday;
    }

    public void setPlayday(final Playday playday) {
        this.playday = playday;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public int getPlace() {
        return this.place;
    }

    public void setPlace(final int place) {
        this.place = place;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public int getCorrectTips() {
        return this.correctTips;
    }

    public void setCorrectTips(final int correctTips) {
        this.correctTips = correctTips;
    }

    public int getCorrectTrends() {
        return this.correctTrends;
    }

    public void setCorrectTrends(final int correctTrends) {
        this.correctTrends = correctTrends;
    }

    public int getCorrectDiffs() {
        return this.correctDiffs;
    }

    public void setCorrectDiffs(final int correctDiffs) {
        this.correctDiffs = correctDiffs;
    }

    public int getPlaydayPoints() {
        return this.playdayPoints;
    }

    public void setPlaydayPoints(final int playdayPoints) {
        this.playdayPoints = playdayPoints;
    }

    public int getPlaydayPlace() {
        return this.playdayPlace;
    }

    public void setPlaydayPlace(final int playdayPlace) {
        this.playdayPlace = playdayPlace;
    }

    public int getPlaydayCorrectTips() {
        return this.playdayCorrectTips;
    }

    public void setPlaydayCorrectTips(final int playdayCorrectTips) {
        this.playdayCorrectTips = playdayCorrectTips;
    }

    public int getPlaydayCorrectTrends() {
        return this.playdayCorrectTrends;
    }

    public void setPlaydayCorrectTrends(final int playdayCorrectTrends) {
        this.playdayCorrectTrends = playdayCorrectTrends;
    }

    public int getPlaydayCorrectDiffs() {
        return this.playdayCorrectDiffs;
    }

    public void setPlaydayCorrectDiffs(final int playdayCorrectDiffs) {
        this.playdayCorrectDiffs = playdayCorrectDiffs;
    }
}