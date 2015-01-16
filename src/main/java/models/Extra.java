package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import morphia.MorphiaModel;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "extras", noClassnameStored = true)
public class Extra extends MorphiaModel implements Serializable {
    private static final long serialVersionUID = 4289210466754449470L;

    @Reference("extra_extratips")
    private List<ExtraTip> extraTips;

    @Reference("extra_answers")
    private List<Team> answers;

    @Reference("extra_gamereferences")
    private List<Game> gameReferences;

    @Reference
    private Team answer;

    private String question;
    private String questionShort;
    private String extraReference;

    private int points;

    private Date ending;

    private boolean reminder;

    public List<ExtraTip> getExtraTips() {
        return extraTips;
    }

    public void setExtraTips(List<ExtraTip> extraTips) {
        this.extraTips = extraTips;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public List<ExtraTip> getExtraTipps() {
        return extraTips;
    }

    public void setExtraTipps(List<ExtraTip> extraTips) {
        this.extraTips = extraTips;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionShort() {
        return questionShort;
    }

    public void setQuestionShort(String questionShort) {
        this.questionShort = questionShort;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getEnding() {
        return ending;
    }

    public void setEnding(Date ending) {
        this.ending = ending;
    }

    public List<Team> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Team> answers) {
        this.answers = answers;
    }

    public String getExtraReference() {
        return extraReference;
    }

    public void setExtraReference(String extraReference) {
        this.extraReference = extraReference;
    }

    public List<Game> getGameReferences() {
        return gameReferences;
    }

    public void setGameReferences(List<Game> gameReferences) {
        this.gameReferences = gameReferences;
    }

    public Team getAnswer() {
        return answer;
    }

    public void setAnswer(Team answer) {
        this.answer = answer;
    }
    
    public boolean isTipable() {
        if (new Date().getTime() >= this.ending.getTime()) {
            return false;
        }

        return true;
    }
}