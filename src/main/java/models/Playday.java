package models;

import java.io.Serializable;
import java.util.List;

import models.statistic.GameStatistic;
import models.statistic.GameTipStatistic;
import models.statistic.UserStatistic;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

import de.svenkubiak.ninja.morphia.MorphiaModel;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "playdays", noClassnameStored = true)
public class Playday extends MorphiaModel implements Serializable {
    private static final long serialVersionUID = -7329808092314093714L;

    @Reference(value = "playday_games")
    private List<Game> games;

    @Reference(value = "playday_userstatistics", lazy = true)
    private List<UserStatistic> userStatistics;

    @Reference(value = "playday_gametipstatistics", lazy = true)
    private List<GameTipStatistic> gameTipStatistics;

    @Reference(value = "playday_gamestatistic", lazy = true)
    private List<GameStatistic> gameStatistic;

    private String name;

    private int number;

    private boolean playoff;
    private boolean current;
    
    public boolean isCurrent() {
        return this.current;
    }

    public void setCurrent(final boolean current) {
        this.current = current;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(final List<Game> games) {
        this.games = games;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public boolean isPlayoff() {
        return this.playoff;
    }

    public void setPlayoff(final boolean playoff) {
        this.playoff = playoff;
    }

    public List<UserStatistic> getUserStatistics() {
        return userStatistics;
    }

    public void setUserStatistics(List<UserStatistic> userStatistics) {
        this.userStatistics = userStatistics;
    }

    public List<GameTipStatistic> getGameTipStatistics() {
        return gameTipStatistics;
    }

    public void setGameTipStatistics(List<GameTipStatistic> gameTipStatistics) {
        this.gameTipStatistics = gameTipStatistics;
    }

    public List<GameStatistic> getGameStatistic() {
        return gameStatistic;
    }

    public void setGameStatistic(List<GameStatistic> gameStatistic) {
        this.gameStatistic = gameStatistic;
    }
}