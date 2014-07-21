package models.statistic;

import java.io.Serializable;

import models.Playday;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "gamestatistics", noClassnameStored = true)
public class GameStatistic implements Serializable {
    private static final long serialVersionUID = 1428636773667817535L;

    @Id
    private ObjectId id;

    @Reference
    private Playday playday;

    private String gameResult;

    private int resultCount;

    public ObjectId getId() {
        return id;
    }

    public Playday getPlayday() {
        return playday;
    }

    public void setPlayday(Playday playday) {
        this.playday = playday;
    }

    public String getGameResult() {
        return gameResult;
    }

    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}