package controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.AbstractJob;
import models.Bracket;
import models.Confirmation;
import models.Game;
import models.Playday;
import models.Settings;
import models.User;
import models.enums.ConfirmationType;
import models.enums.Constants;
import models.pagination.Pagination;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.cache.NinjaCache;
import ninja.mongodb.MongoDB;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.CalculationService;
import services.CommonService;
import services.DataService;
import services.I18nService;
import services.MailService;
import services.ValidationService;

import com.google.inject.Inject;

import dtos.SettingsDTO;
import filters.AdminFilter;

/**
 * 
 * @author svenkubiak
 *
 */
@FilterWith(AdminFilter.class)
public class AdminController extends RootController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
    private static final String ERROR_LOADING_USER = "error.loading.user";
    private static final String ADMIN_USERS = "/admin/users";
    private static final String AWAY_SCORE_ET = "_awayScore_et";
    private static final String HOME_SCORE_ET = "_homeScore_et";
    private static final String GAME = "game_";
    private static final String AWAY_SCORE = "_awayScore";
    private static final String HOME_SCORE = "_homeScore";
    private static final String ADMIN_RESULTS = "/admin/results/";

    @Inject
    private DataService dataService;
    
    @Inject
    private MongoDB mongoDB;

    @Inject
    private CalculationService calculationService;

    @Inject
    private MailService mailService;

    @Inject
    private I18nService i18nService;

    @Inject
    private ValidationService validationService;

    @Inject
    private CommonService commonService;
    
    @Inject
    private NinjaCache ninjaCache;

    public Result results(@PathParam("number") long number) {
        final Pagination pagination = commonService.getPagination(number, ADMIN_RESULTS, dataService.findAllPlaydaysOrderByNumber().size());
        final Playday playday = dataService.findPlaydaybByNumber(pagination.getNumberAsInt());

        return Results.html().render("playday", playday).render("pagination", pagination);
    }

    public Result users() {
        final List<User> users = mongoDB.findAll(User.class);
        return Results.html().render("users", users);
    }

    public Result storeresults(Context context, FlashScope flashScope) {
        final Map<String, String> map = commonService.convertParamaters(context.getParameters());
        final Set<String> keys = new HashSet<String>();
        for (final Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.isNotBlank(key) && key.contains(GAME) && (key.contains(HOME_SCORE) || key.contains(AWAY_SCORE))) {
                key = key.replace(GAME, "")
                        .replace(HOME_SCORE, "")
                        .replace(AWAY_SCORE, "")
                        .replace(HOME_SCORE_ET, "")
                        .replace(AWAY_SCORE_ET, "")
                        .trim();
                keys.add(key);
            }
        }

        String gamekey = setGameScore(map, keys);
        int playday = 1;
        if (keys != null && !keys.isEmpty() && StringUtils.isNotBlank(gamekey)) {
            gamekey = gamekey.replace("_et", "");
            final Game game = mongoDB.findById(gamekey, Game.class);
            if (game != null && game.getPlayday() != null) {
                playday = game.getPlayday().getNumber();
            }
        }

        calculationService.calculations();
        flashScope.success(i18nService.get("controller.games.tippsstored"));
        
        return Results.redirect(ADMIN_RESULTS + playday);
    }

    private String setGameScore(final Map<String, String> map, final Set<String> keys) {
        String gamekey = null;
        for (final String key : keys) {
            gamekey = key;
            final String homeScore = map.get(GAME + key + HOME_SCORE);
            final String awayScore = map.get(GAME + key + AWAY_SCORE);
            final String extratime = map.get("extratime_" + key);
            final String homeScoreExtratime = map.get(GAME + key + HOME_SCORE_ET);
            final String awayScoreExtratime = map.get(GAME + key + AWAY_SCORE_ET);
            calculationService.setGameScore(key, homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);
        }
        
        return gamekey;
    }

    public Result updatesettings (FlashScope flashScope, @JSR303Validation SettingsDTO settingsDTO, Validation validation) {
        validationService.validateSettingsDTO(settingsDTO, validation);
        
        if (validation.hasBeanViolations()) {
            return Results.html().render("settingsDTO", settingsDTO).render("validation", validation).template("/views/AdminController/settings.ftl.html");
        }

        if (!validation.hasBeanViolations()) {
            final Settings settings = dataService.findSettings();
            settings.setGameName(settingsDTO.getName());
            settings.setPointsTip(settingsDTO.getPointsTip());
            settings.setPointsTipDiff(settingsDTO.getPointsTipDiff());
            settings.setPointsTipTrend(settingsDTO.getPointsTipTrend());
            settings.setMinutesBeforeTip(settingsDTO.getMinutesBeforeTip());
            settings.setInformOnNewTipper(settingsDTO.isInformOnNewTipper());
            settings.setEnableRegistration(settingsDTO.isEnableRegistration());
            mongoDB.save(settings);

            ninjaCache.delete(Constants.SETTINGS.get());
            flashScope.success(i18nService.get("setup.saved"));
        }

        return Results.redirect("/admin/settings");
    }

    public Result settings(FlashScope flashScope) {
        final Settings settings = dataService.findSettings();

        flashScope.put("name", settings.getGameName());
        flashScope.put("pointsTip", settings.getPointsTip());
        flashScope.put("pointsTipDiff", settings.getPointsTipDiff());
        flashScope.put("pointsTipTrend", settings.getPointsTipTrend());
        flashScope.put("minutesBeforeTip", settings.getMinutesBeforeTip());
        flashScope.put("informOnNewTipper", settings.isInformOnNewTipper());
        flashScope.put("enableRegistration", settings.isEnableRegistration());

        return Results.html().render(settings);
    }

    public Result changeactive(@PathParam("userid") String userId, Context context, FlashScope flashScope) {
        final User connectedUser = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
        final User user = mongoDB.findById(userId, User.class);

        if (user != null) {
            if (!connectedUser.equals(user)) {
                String message;
                String activate;
                if (user.isActive()) {
                    user.setActive(false);
                    activate = "deactivated";
                    message = i18nService.get("info.change.deactivate");
                } else {
                    final Confirmation confirmation = dataService.findConfirmationByTypeAndUser(ConfirmationType.ACTIVATION, user);
                    if (confirmation != null) {
                        mongoDB.delete(confirmation);
                    }
                    user.setActive(true);
                    activate = "activated";
                    message = i18nService.get("info.change.activate", new Object[]{user.getEmail()});
                }
                mongoDB.save(user);
                flashScope.success(message);
                LOG.info("User " + user.getEmail() + " " + activate + " - by " + connectedUser.getEmail());
            } else {
                flashScope.put(Constants.FLASHWARNING.get(), i18nService.get("warning.change.active"));
            }
        } else {
            flashScope.error(i18nService.get(ERROR_LOADING_USER));
        }

        return Results.redirect(ADMIN_USERS);
    }

    public Result changeadmin(@PathParam("userid") String userId, FlashScope flashScope, Context context) {
        final User connectedUser = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
        final User user = mongoDB.findById(userId, User.class);

        if (user != null) {
            if (!connectedUser.equals(user)) {
                String message;
                String admin;
                if (user.isAdmin()) {
                    message = i18nService.get("info.change.deadmin", new Object[]{user.getEmail()});
                    admin = "is now admin";
                    user.setAdmin(false);
                } else {
                    message = i18nService.get("info.change.admin", new Object[]{user.getEmail()});
                    admin = "is not admin anymore";
                    user.setAdmin(true);
                }
                mongoDB.save(user);
                flashScope.success(message);
                LOG.info(user.getEmail() + " " + admin + " - " + connectedUser.getEmail());
            } else {
                flashScope.put(Constants.FLASHWARNING.get(), i18nService.get("warning.change.admin"));
            }
        } else {
            flashScope.error(i18nService.get(ERROR_LOADING_USER));
        }

        return Results.redirect(ADMIN_USERS);
    }

    public Result deleteuser(@PathParam("userid") String userId, FlashScope flashScope, Context context) {
        final User connectedUser = context.getAttribute("connectedUser", User.class);
        final User user = mongoDB.findById(userId, User.class);

        if (user != null && !user.equals(connectedUser)) {
            final String username = user.getEmail();
            dataService.deleteConfirmationsByUser(user);
            mongoDB.delete(user);
            
            flashScope.success(i18nService.get("info.delete.user", new Object[]{username}));
            LOG.info(username + " deleted - " + connectedUser.getEmail());

            calculationService.calculations();
        } else {
            flashScope.error(i18nService.get(ERROR_LOADING_USER));
        }

        return Results.redirect(ADMIN_USERS);
    }

    public Result rudelmail() {
        return Results.html().render("");
    }

    public Result tournament() {
        List<Bracket> brackets = mongoDB.findAll(Bracket.class);
        List<Game> games = mongoDB.findAll(Game.class);

        return Results.html().render("brackets", brackets).render("games", games);
    }

    public Result send(FlashScope flashScope, Context context) {
        String subject = context.getParameter("subject");
        String message = context.getParameter("message");

        if (StringUtils.isNotBlank(subject) && StringUtils.isNotBlank(message)) {
            final List<String> recipients = new ArrayList<String>();
            final List<User> users = dataService.findAllActiveUsers();
            for (final User user : users) {
                recipients.add(user.getEmail());
            }

            String[] recipientsArray = new String[users.size()];
            recipientsArray = recipients.toArray(recipientsArray);

            User connectedUser = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
            mailService.rudelmail(subject, message, recipientsArray, connectedUser.getEmail());
            flashScope.success(i18nService.get("info.rudelmail.send"));
        } else {
            flashScope.error(i18nService.get("error.rudelmail.send"));
        }

        return Results.redirect("/rudelmail");
    }

    public Result jobstatus(@PathParam("name") String name) {
        if (StringUtils.isNotBlank(name)) {
            AbstractJob abstractJob = dataService.findAbstractJobByName(name);
            abstractJob.setActive(!abstractJob.isActive());
            mongoDB.save(abstractJob);
        }

        return Results.redirect("/admin/jobs");
    }

    public Result jobs() {
        List<AbstractJob> jobs = mongoDB.findAll(AbstractJob.class);

        return Results.html().render("jobs", jobs);
    }

    public Result calculations() {
        calculationService.calculations();

        return Results.redirect("/admin/tournament");
    }
    
    public Result reset(Context context, Session session) throws InterruptedException {
        String confirm = context.getParameter("confirm");
        
        if (("rudeltippen").equalsIgnoreCase(confirm)) {
            mongoDB.dropDatabase();
            ninjaCache.clear();
            session.clear();
            
            return Results.redirect("/");
        }
        
        return Results.redirect("/admin/settings");
    }
}