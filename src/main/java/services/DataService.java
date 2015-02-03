package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.Bracket;
import models.Confirmation;
import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.Playday;
import models.Settings;
import models.Team;
import models.User;
import models.enums.ConfirmationType;
import models.enums.Constants;
import models.statistic.GameStatistic;
import models.statistic.GameTipStatistic;
import models.statistic.PlaydayStatistic;
import models.statistic.ResultStatistic;
import models.statistic.UserStatistic;

import org.joda.time.DateTime;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.svenkubiak.ninja.mongodb.MongoDB;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataService {
    private static final Logger LOG = LoggerFactory.getLogger(DataService.class);
    private static final String ID = "_id";
    private static final String TOTAL = "total";
    private static final String POINTS = "points";
    private static final String GAME_RESULT = "gameResult";
    private static final String KICKOFF = "kickoff";
    private static final String ENDED = "ended";
    private static final String CURRENT = "current";
    private static final String GAME = "game";
    private static final String REMINDER = "reminder";
    private static final String PLAYOFF = "playoff";
    private static final String USERNAME = "username";
    private static final String PLACE = "place";
    private static final String BRACKET = "bracket";
    private static final String NUMBER = "number";
    private static final String EMAIL = "email";
    private static final String USER = "user";
    private static final String PLAYDAY = "playday";
    private static final String ACTIVE = "active";
    private Datastore datastore;
    private MongoDB mongoDB;

    @Inject
    private ResultService resultService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ValidationService validationService;

    @Inject
    private CommonService commonService;

    @Inject
    private DataService(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
        this.datastore = mongoDB.getDatastore();
    }
    
    public void setMongoClient(MongoClient mongoClient) {
        this.mongoDB.setMongoClient(mongoClient);
        this.datastore = this.mongoDB.getDatastore();
    }

    public List<User> findAllNotifiableUsers() {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).field("sendGameTips").equal(true).asList();
    }

    public List<Game> findAllNotifiableGames() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusMinutes(1);
        
        return this.datastore.find(Game.class)
                .field("informed")
                .equal(false)
                .field(KICKOFF)
                .lessThanOrEq(dateTime.toDate())
                .asList();
    }

    public Playday findPlaydaybByNumber(int number) {
        return this.datastore.find(Playday.class).field(NUMBER).equal(number).get();
    }

    public List<Game> findAllGamesWithNoResult() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.minusMinutes(90);

        return this.datastore.find(Game.class)
                .field(ENDED).equal(false)
                .field("webserviceID").exists()
                .field(KICKOFF).lessThanOrEq(dateTime.toDate())
                .asList();
    }

    public List<Extra> findAllExtrasEnding() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);

        return this.datastore.find(Extra.class)
                .field(REMINDER).equal(false)
                .field("ending").lessThanOrEq(dateTime.toDate())
                .asList();
    }

    public List<Game> findAllGamesEnding() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
 
        return this.datastore.find(Game.class)
                .field(REMINDER).equal(false)
                .field(KICKOFF).lessThanOrEq(dateTime.toDate())
                .asList();
    }

    public List<User> findAllRemindableUsers() {
        return this.datastore.find(User.class).field(REMINDER).equal(true).field(ACTIVE).equal(true).asList();
    }

    public GameTip findGameTipByGameAndUser(Game game, User user) {
        return this.datastore.find(GameTip.class).field(GAME).equal(game).field(USER).equal(user).get();
    }

    public ExtraTip findExtraTipByExtraAndUser(Extra extra, User user) {
        return this.datastore.find(ExtraTip.class).field("extra").equal(extra).field(USER).equal(user).get();
    }

    public List<User> findAllAdmins() {
        return this.datastore.find(User.class).field("admin").equal(true).asList();
    }

    public List<User> findUsersByNotificationAndActive() {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).field("notification").equal(true).asList();
    }

    public Game findGameFirstGame() {
        return this.datastore.find(Game.class).field(NUMBER).equal(1).get();
    }

    public List<User> findTopThreeUsers() {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).order(PLACE).limit(3).asList();
    }

    public List<User> findSendableUsers() {
        return this.datastore.find(User.class).field("sendStandings").equal(true).asList();
    }

    public List<GameTip> findGameTipsByUser(User user) {
        return this.datastore.find(GameTip.class).field(USER).equal(user).asList();
    }

    public ResultStatistic findResultStatisticByUserAndResult(User user, String score) {
        return this.datastore.find(ResultStatistic.class).field(USER).equal(user).field("result").equal(score).get();
    }

    public GameStatistic findGameStatisticByPlaydayAndResult(Playday playday, Object key) {
        return this.datastore.find(GameStatistic.class).field(PLAYDAY).equal(playday).field(GAME_RESULT).equal(key).get();
    }

    public GameTipStatistic findGameTipStatisticByPlayday(Playday playday) {
        return this.datastore.find(GameTipStatistic.class).field(PLAYDAY).equal(playday).get();
    }

    public UserStatistic findUserStatisticByPlaydayAndUser(Playday playday, User user) {
        return this.datastore.find(UserStatistic.class).field(PLAYDAY).equal(playday).field(USER).equal(user).get();
    }

    public List<UserStatistic> findUserStatisticByPlaydayOrderByPlaydayPoints(Playday playday) {
        return this.datastore.find(UserStatistic.class).field(PLAYDAY).equal(playday).order("playdayPoints").asList();
    }

    public List<UserStatistic> findUserStatisticByPlaydayOrderByPoints(Playday playday) {
        return this.datastore.find(UserStatistic.class).field(PLAYDAY).equal(playday).order(POINTS).asList();
    }

    public List<GameTip> findGameTipByGame(Game game) {
        return this.datastore.find(GameTip.class).field(GAME).equal(game).asList();
    }

    public PlaydayStatistic findPlaydayStatisticByPlaydayAndResult(Playday playday, Object key) {
        return this.datastore.find(PlaydayStatistic.class).field(PLAYDAY).equal(playday).field(GAME_RESULT).equal(key).get();
    }

    public User findUserByEmail(String email) {
        return this.datastore.find(User.class).field(EMAIL).equal(email).get();
    }

    public User findUserByUsername(String username) {
        return this.datastore.find(User.class).field(USERNAME).equal(username).get();
    }

    public List<Confirmation> findAllConfirmation() {
        return this.datastore.find(Confirmation.class).asList();
    }

    public Settings findSettings() {
        return this.datastore.find(Settings.class).field("appName").equal(Constants.APPNAME.asString()).get();
    }

    public User findUserByPlace(int place) {
        return this.datastore.find(User.class).field(PLACE).equal(place).get();
    }

    public Game findGameByNumber(int number) {
        return this.datastore.find(Game.class).field(NUMBER).equal(number).get();
    }

    public Bracket findBracketByNumber(String number) {
        return this.datastore.find(Bracket.class).field(NUMBER).equal(Integer.valueOf(number)).get();
    }

    public void saveScore(final Game game, final String homeScore, final String awayScore, final String extratime, String homeScoreExtratime, String awayScoreExtratime) {
        final int[] points = resultService.getPoints(Integer.parseInt(homeScore), Integer.parseInt(awayScore));
        game.setHomePoints(points[0]);
        game.setAwayPoints(points[1]);
        game.setHomeScore(homeScore);
        game.setAwayScore(awayScore);
        if (validationService.isValidScore(homeScoreExtratime, awayScoreExtratime )) {
            game.setOvertimeType(extratime);
            game.setHomeScoreOT(homeScoreExtratime);
            game.setAwayScoreOT(awayScoreExtratime);
            game.setOvertime(true);
        } else {
            game.setOvertime(false);
        }

        if (!game.isEnded()) {
            notificationService.sendNotfications(game);
            game.setEnded(true);
        }
        
        this.datastore.save(game);
    }

    public void saveGameTip(final Game game, final int homeScore, final int awayScore, User user) {
        GameTip gameTip = findGameTipByGameAndUser(game, user);
        if (commonService.gameIsTippable(game) && validationService.isValidScore(String.valueOf(homeScore), String.valueOf(awayScore))) {
            if (gameTip == null) {
                gameTip = new GameTip();
                gameTip.setGame(game);
                gameTip.setUser(user);
            }
            gameTip.setPlaced(new Date());
            gameTip.setHomeScore(homeScore);
            gameTip.setAwayScore(awayScore);
            this.datastore.save(gameTip);
            LOG.info("Tipp placed - " + user.getEmail() + " - " + gameTip);
        }
    }

    public List<Map<User, List<GameTip>>> findPlaydayTips(final Playday playday, final List<User> users) {
        final List<Map<User, List<GameTip>>> tips = new ArrayList<Map<User, List<GameTip>>>();

        for (final User user : users) {
            final Map<User, List<GameTip>> userTips = new HashMap<User, List<GameTip>>();
            final List<GameTip> gameTips = new ArrayList<GameTip>();
            for (final Game game : playday.getGames()) {
                GameTip gameTip = findGameTipByGameAndUser(game, user);
                if (gameTip == null) {
                    gameTip = new GameTip();
                }
                gameTips.add(gameTip);
            }
            userTips.put(user, gameTips);
            tips.add(userTips);
        }

        return tips;
    }
    public List<Map<User, List<ExtraTip>>> findExtraTips(final List<User> users, final List<Extra> extras) {
        final List<Map<User, List<ExtraTip>>> tips = new ArrayList<Map<User, List<ExtraTip>>>();

        for (final User user : users) {
            final Map<User, List<ExtraTip>> userTips = new HashMap<User, List<ExtraTip>>();
            final List<ExtraTip> extraTips = new ArrayList<ExtraTip>();
            for (final Extra extra : extras) {
                ExtraTip extraTip = findExtraTipByExtraAndUser(extra, user);
                if (extraTip == null) {
                    extraTip = new ExtraTip();
                }
                extraTips.add(extraTip);
            }
            userTips.put(user, extraTips);
            tips.add(userTips);
        }

        return tips;
    }

    public Playday findCurrentPlayday () {
        Playday playday = this.datastore.find(Playday.class).field(CURRENT).equal(true).get();
        if (playday == null) {
            playday = this.datastore.find(Playday.class).field(NUMBER).equal(1).get();
        }

        return playday;
    }

    public void saveExtraTip(final Extra extra, final Team team, User user) {
        if (team != null) {
            ExtraTip extraTip = findExtraTipByExtraAndUser(extra, user);
            if (extraTip == null) {
                extraTip = new ExtraTip();
            }

            extraTip.setUser(user);
            extraTip.setExtra(extra);
            extraTip.setAnswer(team);
            this.datastore.save(extraTip);
            LOG.info("Stored extratip - " + user.getEmail() + " - " + extraTip);
        }
    }

    public boolean appIsInizialized() {
        return findSettings() != null ? true : false;
    }

    public List<User> findAllActiveUsers() {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).asList();
    }

    public List<Playday> findAllPlaydaysOrderByNumber() {
        return this.datastore.find(Playday.class).order(NUMBER).asList();
    }

    public List<Game> findGamesByHomeTeam(Team team) {
        return this.datastore.find(Game.class).field("homeTeam").equal(team).asList();
    }

    public List<Game> findGamesByAwayTeam(Team team) {
        return this.datastore.find(Game.class).field("awayTeam").equal(team).asList();
    }

    public List<User> findAllActiveUsersOrdered() {
        return this.datastore.find(User.class)
                .field(ACTIVE).equal(true)
                .order("points, correctResults, correctDifferences, correctTrends, correctExtraTips")
                .asList();
    }

    public List<Bracket> findAllUpdatableBrackets() {
        return this.datastore.find(Bracket.class).field("updatable").equal(true).asList();
    }

    public List<Team> findTeamsByBracketOrdered(Bracket bracket) {
        return this.datastore.find(Team.class).field(BRACKET).equal(bracket).order("-points, -goalsDiff, -goalsFor").asList();
    }

    public List<Team> findTeamsByBracket(Bracket bracket) {
        return this.datastore.find(Team.class).field(BRACKET).equal(bracket).asList();
    }

    public List<Game> findGamesByPlayoffAndEndedAndBracket() {
        return this.datastore.find(Game.class).field(PLAYOFF).equal(true).field(ENDED).equal(false).field(BRACKET).equal(null).asList();
    }

    public List<Game> findReferencedGames(String bracketString) {
        Pattern pattern = Pattern.compile(bracketString);
        List<Game> games = this.datastore.find(Game.class).filter("homeReference", pattern).asList();
        games.addAll(this.datastore.find(Game.class).filter("awayReference", pattern).asList());

        return games;
    }

    public List<Game> findAllNonPlayoffGames() {
        return this.datastore.find(Game.class).field(PLAYOFF).equal(false).asList();
    }

    public List<Game> findAllPlayoffGames() {
        return this.datastore.find(Game.class).field(PLAYOFF).equal(true).asList();
    }

    public List<User> findActiveUsers(int limit) {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).order(PLACE).limit(limit).asList();
    }

    public List<User> findAllActiveUsersOrderedByPlace() {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).order(PLACE).asList();
    }

    public List<User> findUsersOrderByUsername() {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).order(USERNAME).asList();
    }

    public Confirmation findConfirmationByTypeAndUser(ConfirmationType confirmationType, User user) {
        return this.datastore.find(Confirmation.class).field("confirmationType").equal(confirmationType).field(USER).equal(user).get();
    }

    public Confirmation findConfirmationByToken(String token) {
        return this.datastore.find(Confirmation.class).field("token").equal(token).get();
    }

    public User findUserByEmailAndActive(String email) {
        return this.datastore.find(User.class).field(ACTIVE).equal(true).field(EMAIL).equal(email).get();
    }

    public User findUserByUsernameOrEmail(String username) {
        Query<User> query = this.datastore.find(User.class);
        query.or(query.criteria(USERNAME).equal(username), query.criteria(EMAIL).equal(username));
        query.and(query.criteria(ACTIVE).equal(true));

        return query.get();
    }

    public List<Game> findGamesByBracket(Bracket bracket) {
        return this.datastore.find(Game.class).field(BRACKET).equal(bracket).asList();
    }

    public List<Game> findGamesByPlayday(Playday playday) {
        return this.datastore.find(Game.class).field(PLAYDAY).equal(playday).order("kickoff").order("number").asList();
    }

    public List<ExtraTip> findExtraTipsByUser(User user) {
        return this.datastore.find(ExtraTip.class).field(USER).equal(user).field(POINTS).greaterThan(0).asList();
    }

    public List<UserStatistic> findUserStatisticByUser(User user) {
        return this.datastore.find(UserStatistic.class).field(USER).equal(user).order(PLAYDAY).asList();
    }

    public void deleteResultsStatisticByUser(User user) {
        List<ResultStatistic> resultStatistics = this.datastore.find(ResultStatistic.class).field(USER).equal(user).asList();
        if (!resultStatistics.isEmpty()) {
            for (ResultStatistic resultStatistic : resultStatistics) {
                this.datastore.delete(resultStatistic);
            }
        }
    }

    public List<GameStatistic> findAllGameStatistics() {
        return this.datastore.find(GameStatistic.class).asList();
    }

    public List<GameTipStatistic> findGameTipStatisticsOrderByPlayday() {
        return this.datastore.find(GameTipStatistic.class).order(PLAYDAY).asList();
    }

    public void deleteConfirmationsByUser(User user) {
        this.datastore.delete(this.datastore.find(Confirmation.class).field(USER).equal(user).asList());
    }

    public List<Map<String, String>> findResultsStatistic() {
        DB db = this.mongoDB.getDatastore().getDB();
        DBCollection collection = db.getCollection("playdaystatistics");
        
        DBObject groupFields = new BasicDBObject();
        groupFields.put(ID, "$gameResult");
        groupFields.put(TOTAL, new BasicDBObject("$sum", "$resultCount"));
        
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject(TOTAL, -1));
        
        List<DBObject> pipeline = Arrays.asList(group, sort);
        AggregationOutput output = collection.aggregate(pipeline);

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        for (DBObject dbObject : output.results()) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(dbObject.get(ID).toString(), dbObject.get(TOTAL).toString());
            results.add(result);
        }
        
        return results;
    }

    public Map<String, String> findStatisticsForPlayday(Playday playday) {
        int points = 0, tips = 0, diffs = 0, trends = 0;
        List<UserStatistic> userStatistics = this.datastore.find(UserStatistic.class).field("playday").equal(playday).asList();
        for (UserStatistic userStatistic : userStatistics) {
            points = points + userStatistic.getPlaydayPoints();
            tips = tips + userStatistic.getPlaydayCorrectTips();
            diffs = diffs + userStatistic.getPlaydayCorrectDiffs();
            trends = trends + userStatistic.getPlaydayCorrectTrends();
        }
        
        Map<String, String> statistics = new HashMap<String, String>();
        if (!userStatistics.isEmpty()) {
            statistics.put("points", String.valueOf(points));
            statistics.put("tips", String.valueOf(tips));
            statistics.put("diffs", String.valueOf(diffs));
            statistics.put("trends", String.valueOf(trends));
            statistics.put("avgPoints", String.valueOf(points / userStatistics.size()));   
        }
        
        return statistics;
    }

    public List<Map<String, String>> findGameStatistics() {
        DB db = this.mongoDB.getDatastore().getDB();
        DBCollection collection = db.getCollection("gamestatistics");
        
        DBObject groupFields = new BasicDBObject();
        groupFields.put(ID, "$gameResult");
        groupFields.put(TOTAL, new BasicDBObject("$sum", "$resultCount"));
        
        DBObject group = new BasicDBObject("$group", groupFields);
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject(TOTAL, -1));
        
        List<DBObject> pipeline = Arrays.asList(group, sort);
        AggregationOutput output = collection.aggregate(pipeline);

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        for (DBObject dbObject : output.results()) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(dbObject.get(ID).toString(), dbObject.get(TOTAL).toString());
            results.add(result);
        }
        
        return results;
    }

    public List<Bracket> findAllTournamentBrackets() {
        List<Bracket> brackets = this.datastore.find(Bracket.class).asList();
        for (Bracket bracket : brackets) {
            List<Team> teams = this.datastore.find(Team.class).field("bracket").equal(bracket).order("place").asList();
            bracket.setTeams(teams);
        }
        
        return brackets;
    }
    
    public void dropDatabase() {
        this.datastore.getDB().dropDatabase();
    }

    public void save(final Object object) {
        this.mongoDB.getDatastore().save(object);
    }

    public void deleteUser(User user) {
        this.datastore.delete(this.datastore.find(GameTip.class).field("user").equal(user));
        this.datastore.delete(this.datastore.find(ExtraTip.class).field("user").equal(user));
        this.datastore.delete(this.datastore.find(Confirmation.class).field("user").equal(user));
        this.datastore.delete(this.datastore.find(UserStatistic.class).field("user").equal(user));
        this.datastore.delete(this.datastore.find(ResultStatistic.class).field("user").equal(user));
        this.datastore.delete(user);
    }

    public List<Playday> findNextPlaydays() {
        List<Playday> playdays = new ArrayList<Playday>();
        int number = findCurrentPlayday().getNumber();
        for (int i=0; i <= 3; i++) {
            playdays.add(findPlaydaybByNumber(number));
            number++;
        }
        
        return playdays;
    }

    public List<User> findAllUsers() {
        return this.mongoDB.findAll(User.class);
    }

    public Game findGameById(String id) {
        return this.mongoDB.findById(id, Game.class);
    }

    public User findUserById(String id) {
        return this.mongoDB.findById(id, User.class);
    }

    public void delete(Object object) {
        this.datastore.delete(object);
    }

    public List<Bracket> findAllBrackets() {
        return this.mongoDB.findAll(Bracket.class);
    }

    public List<Game> findAllGames() {
        return this.mongoDB.findAll(Game.class);
    }

    public Team findTeamById(String id) {
        return this.mongoDB.findById(id, Team.class);
    }

    public Bracket findBracketById(String id) {
        return this.mongoDB.findById(id, Bracket.class);
    }

    public long countAll(Class clazz) {
        return this.mongoDB.countAll(clazz);
    }

    public List<Extra> findAllExtras() {
        return this.mongoDB.findAll(Extra.class);
    }

    public Extra findExtraById(String id) {
        return this.mongoDB.findById(id, Extra.class);
    }

    public List<Team> findAllTeams() {
        return this.mongoDB.findAll(Team.class);
    }

    public List<Game> findAllGamesOrderByNumber() {
        return this.datastore.find(Game.class).order("number").asList();
    }
}