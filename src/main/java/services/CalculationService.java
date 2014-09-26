package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Bracket;
import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.Playday;
import models.Settings;
import models.Team;
import models.User;
import models.ws.WSResult;
import models.ws.WSResults;
import ninja.cache.NinjaCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class CalculationService {
    private static final Logger LOG = LoggerFactory.getLogger(CalculationService.class);

    @Inject
    private DataService dataService;
    
    @Inject
    private ResultService resultService;

    @Inject
    private StatisticService statisticService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ValidationService validationService;

    @Inject
    private CommonService commonService;

    @Inject
    private NinjaCache ninjaCache;
    
    public void calculations() {
        ninjaCache.clear();
        
        calculateBrackets();
        setTeamPlaces();
        setExtraAnswers();
        calculateUserPoints();
        setUserPlaces();
        setPlayoffTeams();
        calculateStatistics();
        setCurrentPlayday();
    }

    private void calculateStatistics() {
        final List<Playday> playdays = dataService.findAllPlaydaysOrderByNumber();
        final List<User> users = dataService.findAllActiveUsers();
        for (final Playday playday : playdays) {
            if (commonService.allGamesEnded(playday)) {
                final Map<String, Integer> scores = getScores(playday);
                statisticService.setPlaydayStatistics(playday, scores);

                for (final User user : users) {
                    statisticService.setPlaydayPoints(playday, user);
                }

                statisticService.setPlaydayPlaces(playday);
                statisticService.setGameTipStatistics(playday);
                statisticService.setGameStatistic(playday);
            }
        }

        for (final User user : users) {
            statisticService.setResultStatistic(user);
        }
    }

    private void calculateUserPoints() {
        final List<User> users = dataService.findAllActiveUsers();
        for (final User user : users) {
            int correctResults = 0;
            int correctDifferences = 0;
            int correctTrends = 0;
            int correctExtraTips = 0;
            int userTipPoints = 0;

            final List<GameTip> gameTips = dataService.findGameTipsByUser(user);
            for (final GameTip gameTip : gameTips) {
                final Game game = gameTip.getGame();

                if (!game.isEnded()) {
                    continue;
                }

                int pointsForTipp = 0;
                if (game.isOvertime()) {
                    pointsForTipp = resultService.getTipPointsOvertime(Integer.parseInt(game.getHomeScore()), Integer.parseInt(game.getAwayScore()), Integer.parseInt(game.getHomeScoreOT()), Integer.parseInt(game.getAwayScoreOT()), gameTip.getHomeScore(), gameTip.getAwayScore());
                } else {
                    pointsForTipp = resultService.getTipPoints(Integer.parseInt(game.getHomeScore()), Integer.parseInt(game.getAwayScore()), gameTip.getHomeScore(), gameTip.getAwayScore());
                }
                gameTip.setPoints(pointsForTipp);
                dataService.save(gameTip);

                final Settings settings = dataService.findSettings();
                if (pointsForTipp == settings.getPointsTip()) {
                    correctResults++;
                } else if (pointsForTipp == settings.getPointsTipDiff()) {
                    correctDifferences++;
                } else if (pointsForTipp == settings.getPointsTipTrend()) {
                    correctTrends++;
                }

                userTipPoints = userTipPoints + pointsForTipp;
            }
            user.setTipPoints(userTipPoints);
            user.setCorrectResults(correctResults);
            user.setCorrectDifferences(correctDifferences);
            user.setCorrectTrends(correctTrends);

            int bonusPoints = 0;
            List<Extra> extras = dataService.findAllExtras();
            for (final Extra extra : extras) {
                final ExtraTip extraTip = dataService.findExtraTipByExtraAndUser(extra, user);
                if (extraTip != null) {
                    final Team bonusAnswer = extra.getAnswer();
                    final Team userAnswer = extraTip.getAnswer();
                    if (bonusAnswer != null && userAnswer != null && bonusAnswer.equals(userAnswer)) {
                        final int bPoints = extra.getPoints();
                        extraTip.setPoints(bPoints);
                        correctExtraTips++;
                        dataService.save(extraTip);
                        bonusPoints = bonusPoints + bPoints;
                    }
                }
            }

            user.setExtraPoints(bonusPoints);
            user.setPoints(bonusPoints + userTipPoints);
            user.setCorrectExtraTips(correctExtraTips);
            dataService.save(user);
        }
    }

    private void setExtraAnswers() {
        List<Extra> extras = dataService.findAllExtras();
        for (final Extra extra : extras) {
            if (extra.getAnswer() == null && commonService.allReferencedGamesEnded(extra.getGameReferences())) {
                final Team team = commonService.getTeamByReference(extra.getExtraReference());
                if (team != null) {
                    extra.setAnswer(team);
                    dataService.save(extra);
                }
            }
        }
    }

    private void calculateBrackets() {
        final Settings settings = dataService.findSettings();
        final int pointsWin = settings.getPointsGameWin();
        final int pointsDraw = settings.getPointsGameDraw();

        final List<Team> teams = dataService.findAllTeams();
        for (final Team team : teams) {
            final List<Game> homeGames = dataService.findGamesByHomeTeam(team);
            final List<Game> awayGames = dataService.findGamesByAwayTeam(team);

            int homePoints = 0;
            int awayPoints = 0;
            int gamesPlayed = 0;
            int gamesWon = 0;
            int gamesDraw = 0;
            int gamesLost = 0;
            int goalsFor = 0;
            int goalsAgainst = 0;
            for (final Game game : homeGames) {
                if (!game.isPlayoff() && validationService.isValidScore(game.getHomeScore(), game.getAwayScore())) {
                    final int points = game.getHomePoints();
                    homePoints = homePoints + points;
                    gamesPlayed++;

                    if (points == pointsWin) {
                        gamesWon++;
                    } else if (points == pointsDraw) {
                        gamesDraw++;
                    } else if (points == 0) {
                        gamesLost++;
                    }
                    goalsFor = goalsFor + Integer.parseInt(game.getHomeScore());
                    goalsAgainst = goalsAgainst + Integer.parseInt(game.getAwayScore());
                }
            }

            for (final Game game : awayGames) {
                if (!game.isPlayoff() && validationService.isValidScore(game.getHomeScore(), game.getAwayScore())) {
                    final int points = game.getAwayPoints();
                    awayPoints = awayPoints + points;
                    gamesPlayed++;

                    if (points == pointsWin) {
                        gamesWon++;
                    } else if (points == pointsDraw) {
                        gamesDraw++;
                    } else if (points == 0) {
                        gamesLost++;
                    }
                    goalsFor = goalsFor + Integer.parseInt(game.getAwayScore());
                    goalsAgainst = goalsAgainst + Integer.parseInt(game.getHomeScore());
                }
            }
            team.setPoints(homePoints + awayPoints);
            team.setGamesDraw(gamesDraw);
            team.setGamesLost(gamesLost);
            team.setGamesWon(gamesWon);
            team.setGamesPlayed(gamesPlayed);
            team.setGoalsFor(goalsFor);
            team.setGoalsAgainst(goalsAgainst);
            team.setGoalsDiff(goalsFor - goalsAgainst);
            dataService.save(team);
        }
    }

    private void setUserPlaces() {
        int place = 1;
        final List<User> users = dataService.findAllActiveUsersOrdered();
        for (final User user : users) {
            user.setPreviousPlace(user.getPlace());
            user.setPlace(place);
            dataService.save(user);
            place++;
        }
    }

    private boolean setCurrentPlayday() {
        boolean changed = false;
        final Playday currentPlayday = dataService.findCurrentPlayday();
        final List<Playday> playdays = dataService.findAllPlaydaysOrderByNumber();
        for (final Playday playday : playdays) {
            if (commonService.allGamesEnded(playday)) {
                playday.setCurrent(false);
                dataService.save(playday);
            } else {
                playday.setCurrent(true);
                dataService.save(playday);
                break;
            }
        }

        if (currentPlayday != dataService.findCurrentPlayday()) {
            changed = true;
            notificationService.sendTopThree(currentPlayday);
        }

        return changed;
    }

    public void setPlayoffTeams() {
        final Settings settings = dataService.findSettings();
        if (settings.isPlayoffs()) {
            Team homeTeam = null;
            Team awayTeam = null;

            final List<Bracket> brackets = dataService.findAllBrackets();
            for (final Bracket bracket : brackets) {
                if (commonService.allGamesEnded(bracket)) {
                    final int number = bracket.getNumber();
                    final String bracketString = "B-" + number + "%";
                    final List<Game> games = dataService.findReferencedGames(bracketString);
                    for (final Game game : games) {
                        homeTeam = commonService.getTeamByReference(game.getHomeReference());
                        awayTeam = commonService.getTeamByReference(game.getAwayReference());
                        game.setHomeTeam(homeTeam);
                        game.setAwayTeam(awayTeam);
                        dataService.save(game);
                    }
                }
            }

            final List<Game> playoffGames = dataService.findGamesByPlayoffAndEndedAndBracket();
            for (final Game game : playoffGames) {
                homeTeam = commonService.getTeamByReference(game.getHomeReference());
                awayTeam = commonService.getTeamByReference(game.getAwayReference());
                game.setHomeTeam(homeTeam);
                game.setAwayTeam(awayTeam);
                dataService.save(game);
            }
        }
    }

    private void setTeamPlaces() {
        final List<Bracket> brackets = dataService.findAllUpdatableBrackets();
        for (final Bracket bracket : brackets) {
            final List<Team> teams = dataService.findTeamsByBracketOrdered(bracket);
            int place = 1;
            for (final Team team : teams) {
                team.setPreviousPlace(team.getPlace());
                team.setPlace(place);
                dataService.save(team);
                place++;
            }
        }
    }

    public void setGameScore(final String gameId, final String homeScore, final String awayScore, final String extratime, final String homeScoreExtratime, final String awayScoreExtratime) {
        if (validationService.isValidScore(homeScore, awayScore)) {
            final Game game = dataService.findGameById(gameId);
            if (game != null) {
                dataService.saveScore(game, homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);
            }
        }
    }

    public Map<String, Integer> getScores(final Playday playday) {
        final Map<String, Integer> scores = new HashMap<String, Integer>();

        final List<Game> games = playday.getGames();
        for (final Game game : games) {
            final List<GameTip> gameTips = dataService.findGameTipByGame(game);
            for (final GameTip gameTip : gameTips) {
                final String score = gameTip.getHomeScore() + ":" + gameTip.getAwayScore();
                if (!scores.containsKey(score)) {
                    scores.put(score, 1);
                } else {
                    scores.put(score, scores.get(score) + 1);
                }
            }
        }
        return scores;
    }

    public void setGameScoreFromWebService(final Game game, final WSResults wsResults) {
        final Map<String, WSResult> wsResult = wsResults.getWsResult();

        String homeScore = null;
        String awayScore = null;
        String homeScoreExtratime = null;
        String awayScoreExtratime = null;
        String extratime = null;

        if (wsResult.containsKey("90")) {
            homeScore = wsResult.get("90").getHomeScore();
            awayScore = wsResult.get("90").getAwayScore();
        }

        if (wsResult.containsKey("121")) {
            homeScoreExtratime = wsResult.get("121").getHomeScore();
            awayScoreExtratime = wsResult.get("121").getAwayScore();
            extratime = "ie";
        } else if (wsResult.containsKey("120")) {
            homeScoreExtratime = wsResult.get("120").getHomeScore();
            awayScoreExtratime = wsResult.get("120").getAwayScore();
            extratime = "nv";
        }

        LOG.info("Recieved from WebService - HomeScore: " + homeScore + " AwayScore: " + awayScore);
        LOG.info("Recieved from WebService - HomeScoreExtra: " + homeScoreExtratime + " AwayScoreExtra: " + awayScoreExtratime + " (" + extratime + ")");
        LOG.info("Updating results from WebService. " + game);
        setGameScore(String.valueOf(game.getId()), homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);
        calculations();
    }

    public int getPointsToFirstPlace(User connectedUser) {
        final User user = dataService.findUserByPlace(1);

        int pointsDiff = 0;
        if (user != null && connectedUser != null) {
            pointsDiff = user.getPoints() - connectedUser.getPoints();
        }

        return pointsDiff;
    }
}