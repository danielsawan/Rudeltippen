package models.statistic;

import java.io.Serializable;

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
@Entity(value = "resultstatistics", noClassnameStored = true)
public class ResultStatistic implements Serializable {
    private static final long serialVersionUID = -8915315063073633008L;

    @Id
    private ObjectId id;

    @Reference
    private User user;

    private String result;

    private int correctTips;
    private int correctTrends;
    private int correctDiffs;
    private int points;

    public ObjectId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCorrectTips() {
        return correctTips;
    }

    public void setCorrectTips(int correctTips) {
        this.correctTips = correctTips;
    }

    public int getCorrectTrends() {
        return correctTrends;
    }

    public void setCorrectTrends(int correctTrends) {
        this.correctTrends = correctTrends;
    }

    public int getCorrectDiffs() {
        return correctDiffs;
    }

    public void setCorrectDiffs(int correctDiffs) {
        this.correctDiffs = correctDiffs;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}