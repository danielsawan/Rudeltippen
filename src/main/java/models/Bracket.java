package models;

import java.io.Serializable;
import java.util.List;

import ninja.morphia.NinjaMorphiaModel;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "brackets", noClassnameStored = true)
public class Bracket extends NinjaMorphiaModel implements Serializable {
    private static final long serialVersionUID = -7837267515132967024L;

    private String name;

    @Reference("bracket_teams")
    private List<Team> teams;

    @Reference("bracket_games")
    private List<Game> games;

    private int number;

    private boolean updatable;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(final List<Team> teams) {
        this.teams = teams;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(final List<Game> games) {
        this.games = games;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }
}