package services;

import java.util.Date;
import java.util.List;

import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.Playday;
import models.Settings;
import models.Team;
import models.User;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class ViewService {
    private static final String ICON_ARROW_GREEN = "<span class=\"glyphicon glyphicon-arrow-up green\"></span>";
    private static final String ICON_ARROW_RED = "<span class=\"glyphicon glyphicon-arrow-down red\"></span>";
    private static final String ICON_MINUS = "<span class=\"glyphicon glyphicon-minus black\"></span>";

    @Inject
    private DataService dataService;

    @Inject
    private I18nService i18nService;

    public String getScore(final Game game) {
        String score = "- : -";
        if (game.isEnded()) {
            if (game.isOvertime()) {
                score = game.getHomeScore() + " : " + game.getAwayScore() + " / " + game.getHomeScoreOT() + ":" + game.getAwayScoreOT() + " (" + game.getOvertimeType() + ")";
            } else {
                score = game.getHomeScore() + " : " + game.getAwayScore();
            }
        }

        return score;
    }

    public String getGameTipAndPoints(final Game game, User user) {
        String tip = "-";
        final GameTip gameTip = dataService.findGameTipByGameAndUser(game, user);

        if (gameTip != null && gameTip.getGame() != null) {
            if (gameTip.getGame().isEnded()) {
                tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore() + " (" + gameTip.getPoints() + ")";
            } else {
                tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore();
            }
        }

        return tip;
    }

    public String getResult(final Game game) {
        String result = "-";
        if (game.isEnded()) {
            if (game.isOvertime()) {
                result = game.getHomeScoreOT() + " : " + game.getAwayScoreOT() + " (" + i18nService.get(game.getOvertimeType()) + ")";
            } else {
                result = game.getHomeScore() + " : " + game.getAwayScore();
            }
        }

        return result;
    }

    public String getTrend(final Game game) {
        String trend = i18nService.get("model.game.notenoughtipps");
        final List<GameTip> gameTips = game.getGameTips();
        if ((gameTips != null) && (gameTips.size() >= 4)) {
            int tipsHome = 0;
            int tipsDraw = 0;
            int tipsAway = 0;

            for (final GameTip gameTip : gameTips) {
                final int homeScore = gameTip.getHomeScore();
                final int awayScore = gameTip.getAwayScore();

                if (homeScore == awayScore) {
                    tipsDraw++;
                } else if (homeScore > awayScore) {
                    tipsHome++;
                } else if (homeScore < awayScore) {
                    tipsAway++;
                }
            }

            trend = tipsHome + " / " + tipsDraw + " / " + tipsAway;
        }

        return trend;
    }

    public String difference (final Date date) {
        final int min = 60;
        final int hour = min * 60;
        final int day = hour * 24;
        final int month = day * 30;
        final int year = day * 365;

        final Date now = new Date();
        String difference = null;
        if (date != null && date.after(now)) {
            final long delta = (date.getTime() - now.getTime()) / 1000;
            if (delta < 60) {
                difference = i18nService.get("in.second", new Object[]{delta});
            } else if (delta < hour) {
                final long minutes = delta / min;
                difference = i18nService.get("in.minute", new Object[]{minutes});
            } else if (delta < day) {
                final long hours = delta / hour;
                difference = i18nService.get("in.hour", new Object[]{hours});
            } else if (delta < month) {
                final long days = delta / day;
                difference = i18nService.get("in.day", new Object[]{days});
            } else if (delta < year) {
                final long months = delta / month;
                difference = i18nService.get("in.month", new Object[]{months});
            } else {
                final long years = delta / year;
                difference = i18nService.get("in.year", new Object[]{years});
            }
        } else {
            difference = i18nService.get("in.ended");
        }

        return difference;
    }

    public String getHomeScoreTip(final Game game, User user) {
        String homeScore = "";

        GameTip gameTip = dataService.findGameTipByGameAndUser(game, user);
        if (gameTip != null) {
            homeScore = String.valueOf(gameTip.getHomeScore());
        }

        return homeScore;
    }

    public String getAwayScoreTip(final Game game, User user) {
        String awayScore = "";

        GameTip gameTip = dataService.findGameTipByGameAndUser(game, user);
        if (gameTip != null) {
            awayScore = String.valueOf(gameTip.getAwayScore());
        }

        return awayScore;
    }

    public String getGameTipAndPoints(final GameTip gameTip, User user) {
        String tip = "-";
        final Date date = new Date();

        if (gameTip != null) {
            final Game game = gameTip.getGame();
            if (game != null) {
                if (game.isEnded()) {
                    tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore() + " (" + gameTip.getPoints() + ")";
                } else {
                    if (date.after(getTippEnding(game))) {
                        tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore();
                    } else {
                        if (user.equals(gameTip.getUser())) {
                            tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore();
                        }
                    }
                }
            }
        }

        return tip;
    }

    private Date getTippEnding(Game game) {
        final long time = game.getKickoff().getTime();
        final int offset = dataService.findSettings().getMinutesBeforeTip() * 60000 ;

        return new Date (time - offset);
    }

    public String getPlaceTrend(final User user) {
        final int currentPlace = user.getPlace();
        final int previousPlace = user.getPreviousPlace();
        String trend = "";

        if (previousPlace > 0) {
            if (currentPlace < previousPlace) {
                trend = ICON_ARROW_GREEN + " (" + previousPlace + ")";
            } else if (currentPlace > previousPlace) {
                trend = ICON_ARROW_RED + " (" + previousPlace + ")";
            } else {
                trend = ICON_MINUS + " (" + previousPlace + ")";
            }
        }

        return trend;
    }

    public String getPlaceTrend(final Team team) {
        final int currentPlace = team.getPlace();
        final int previousPlace = team.getPreviousPlace();
        String trend = "";

        if (previousPlace > 0) {
            if (currentPlace < previousPlace) {
                trend = ICON_ARROW_GREEN + " (" + previousPlace + ")";
            } else if (currentPlace > previousPlace) {
                trend = ICON_ARROW_RED + " (" + previousPlace + ")";
            } else {
                trend = ICON_MINUS + " (" + previousPlace + ")";
            }
        }

        return trend;
    }

    public boolean gameIsTippable(Game game) {
        final Date now = new Date();
        final Settings settings = dataService.findSettings();
        final int secondsBefore = settings.getMinutesBeforeTip() * 60000;

        if (game.isEnded()) {
            return false;
        } else if (((game.getKickoff().getTime() - secondsBefore) > now.getTime()) && (game.getHomeTeam() != null) && (game.getAwayTeam() != null)) {
            return true;
        }

        return false;
    }
    public boolean playdayIsTippable(Playday playday) {
        for (final Game game : playday.getGames()){
            if (gameIsTippable(game)) {
                return true;
            }
        }
        return false;
    }

    public String getAnswer(final Extra extra, User user) {
        final ExtraTip extraTip = dataService.findExtraTipByExtraAndUser(extra, user);
        String answer = "-";

        if ((extraTip != null) && (extraTip.getAnswer() != null)) {
            answer = i18nService.get(extraTip.getAnswer().getName());
        }

        return answer;
    }
}