package models;

import java.io.Serializable;

import morphia.MorphiaModel;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 * 
 * @author svenkubiak
 *
 */
@Entity(value = "extratips", noClassnameStored = true)
public class ExtraTip extends MorphiaModel implements Serializable {
    private static final long serialVersionUID = -2648500340981679571L;

    @Reference
    private User user;

    @Reference
    private Extra extra;

    @Reference
    public Team answer;

    private int points;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public Team getAnswer() {
        return answer;
    }

    public void setAnswer(Team answer) {
        this.answer = answer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}