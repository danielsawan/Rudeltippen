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
@Entity(value = "playdaystatistics", noClassnameStored = true)
public class PlaydayStatistic implements Serializable {
    private static final long serialVersionUID = 5267463981633151053L;

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
        return this.playday;
    }

    public void setPlayday(final Playday playday) {
        this.playday = playday;
    }

    public String getGameResult() {
        return this.gameResult;
    }

    public void setGameResult(final String gameResult) {
        this.gameResult = gameResult;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}