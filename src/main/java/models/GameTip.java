package models;

import java.io.Serializable;
import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

import de.svenkubiak.ninja.morphia.MorphiaModel;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "gametips", noClassnameStored = true)
public class GameTip extends MorphiaModel implements Serializable {
    private static final long serialVersionUID = -8038881739647141796L;

    @Reference
    private User user;

    @Reference
    private Game game;

    private int homeScore;
    private int awayScore;
    private int points;

    private Date placed;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public Date getPlaced() {
        return placed;
    }

    public void setPlaced(Date placed) {
        this.placed = placed;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}