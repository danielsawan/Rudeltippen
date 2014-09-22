package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.enums.Constants;
import ninja.morphia.MorphiaModel;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "games", noClassnameStored = true)
public class Game extends MorphiaModel implements Serializable {
    private static final long serialVersionUID = -8249455429423822352L;

    @Reference
    private Team homeTeam;

    @Reference
    private Team awayTeam;

    @Reference
    private Bracket bracket;

    @Reference
    private Playday playday;

    @Reference("game_gametips")
    private List<GameTip> gameTips;

    private Date kickoff;

    private String overtimeType;
    private String homeReference;
    private String awayReference;
    private String webserviceID;
    private String homeScore;
    private String awayScore;
    private String homeScoreOT;
    private String awayScoreOT;

    private int number;
    private int homePoints;
    private int awayPoints;

    private boolean overtime;
    private boolean playoff;
    private boolean ended;
    private boolean informed;
    private boolean updatable;
    private boolean reminder;

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public Playday getPlayday() {
        return this.playday;
    }

    public void setPlayday(final Playday playday) {
        this.playday = playday;
    }

    public List<GameTip> getGameTips() {
        return this.gameTips;
    }

    public void setGameTips(final List<GameTip> gameTips) {
        this.gameTips = gameTips;
    }

    public Team getHomeTeam() {
        return this.homeTeam;
    }

    public void setHomeTeam(final Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return this.awayTeam;
    }

    public void setAwayTeam(final Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Date getKickoff() {
        return this.kickoff;
    }

    public void setKickoff(final Date kickoff) {
        this.kickoff = kickoff;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public boolean isOvertime() {
        return this.overtime;
    }

    public void setOvertime(final boolean overtime) {
        this.overtime = overtime;
    }

    public boolean isPlayoff() {
        return this.playoff;
    }

    public void setPlayoff(final boolean playoff) {
        this.playoff = playoff;
    }

    public String getOvertimeType() {
        return this.overtimeType;
    }

    public void setOvertimeType(final String overtimeType) {
        this.overtimeType = overtimeType;
    }

    public String getHomeReference() {
        return this.homeReference;
    }

    public void setHomeReference(final String homeReference) {
        this.homeReference = homeReference;
    }

    public String getAwayReference() {
        return this.awayReference;
    }

    public void setAwayReference(final String awayReference) {
        this.awayReference = awayReference;
    }

    public String getWebserviceID() {
        return this.webserviceID;
    }

    public void setWebserviceID(final String webserviceID) {
        this.webserviceID = webserviceID;
    }

    public boolean isEnded() {
        return this.ended;
    }

    public void setEnded(final boolean ended) {
        this.ended = ended;
    }

    public int getHomePoints() {
        return this.homePoints;
    }

    public void setHomePoints(final int homePoints) {
        this.homePoints = homePoints;
    }

    public int getAwayPoints() {
        return this.awayPoints;
    }

    public void setAwayPoints(final int awayPoints) {
        this.awayPoints = awayPoints;
    }

    public String getHomeScore() {
        return this.homeScore;
    }

    public void setHomeScore(final String homeScore) {
        this.homeScore = homeScore;
    }

    public String getAwayScore() {
        return this.awayScore;
    }

    public void setAwayScore(final String awayScore) {
        this.awayScore = awayScore;
    }

    public String getHomeScoreOT() {
        return this.homeScoreOT;
    }

    public void setHomeScoreOT(final String homeScoreOT) {
        this.homeScoreOT = homeScoreOT;
    }

    public String getAwayScoreOT() {
        return this.awayScoreOT;
    }

    public void setAwayScoreOT(final String awayScoreOT) {
        this.awayScoreOT = awayScoreOT;
    }

    public Bracket getBracket() {
        return this.bracket;
    }

    public void setBracket(final Bracket bracket) {
        this.bracket = bracket;
    }

    public boolean isInformed() {
        return this.informed;
    }

    public void setInformed(final boolean informed) {
        this.informed = informed;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }
    
    //TODO Needs dynamic offset from settings
    public String getTippEnding() {
        final long time = this.kickoff.getTime();
        final int offset = 5 * 60000 ;
        
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATETIMEFORMAT.get());
        return sdf.format(new Date (time - offset));
    }
}