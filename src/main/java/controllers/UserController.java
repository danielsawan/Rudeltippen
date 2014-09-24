package controllers;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.Confirmation;
import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.Settings;
import models.User;
import models.enums.ConfirmationType;
import models.enums.Constants;
import models.statistic.UserStatistic;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.session.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.AuthService;
import services.CommonService;
import services.DataService;
import services.I18nService;
import services.MailService;
import services.ValidationService;

import com.google.inject.Inject;

/**
 * 
 * @author svenkubiak
 *
 */
public class UserController extends RootController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private static final String CONFIRM_MESSAGE = "confirm.message";
    private static final String USERS_PROFILE = "/users/myprofile";
    private static final String USERNAME = "username";

    @Inject
    private DataService dataService;

    @Inject
    private MailService mailService;

    @Inject
    private I18nService i18nService;

    @Inject
    private AuthService authService;

    @Inject
    private ValidationService validationService;
    
    @Inject
    private CommonService commonService;

    public Result show(@PathParam(USERNAME) String username) {
        final User user = dataService.findUserByUsername(username);

        if (user != null) {
            final Map<String, Integer> statistics = new HashMap<String, Integer>();
            final List<ExtraTip> extraTips = dataService.findExtraTipsByUser(user);
            final List<GameTip> tips = dataService.findGameTipsByUser(user);
            final long extra = dataService.countAll(Extra.class);
            final int sumAllTipps = tips.size();
            final int correctTipps = user.getCorrectResults();
            final int correctTrend = user.getCorrectTrends();
            final int correctDifference = user.getCorrectDifferences();
            final DecimalFormat df = new DecimalFormat( "0.00" );

            statistics.put("sumGames", (int) dataService.countAll(Game.class));
            statistics.put("sumTipps", sumAllTipps);
            statistics.put("correctTipps", correctTipps);
            statistics.put("correctTrend", correctTrend);
            statistics.put("correctDifference", correctDifference);
            statistics.put("extraTips", (int) extra);
            statistics.put("correctExtraTips", extraTips.size());

            int tippedGames = 0;
            for (final GameTip tip : tips) {
                if (tip.getGame().isEnded()) {
                    tippedGames++;
                }
            }

            String tippQuote = "0 %";
            if (tippedGames > 0) {
                final double quote = (100.00 / tippedGames) * correctTipps;
                tippQuote = df.format( quote );
            }

            final float pointsTipp = (float) user.getPoints() / (float) tippedGames;
            String pointsPerTipp = "0";
            if (pointsTipp > 0) {
                pointsPerTipp = df.format( pointsTipp );
            }

            final List<UserStatistic> userStatistics = dataService.findUserStatisticByUser(user);
            final int users = dataService.findAllActiveUsers().size();
            final int usersScale = users + 1;

            return Results.html()
                    .render("statistics", statistics)
                    .render("pointsPerTipp", pointsPerTipp)
                    .render("tippQuote", tippQuote)
                    .render("tippedGames", tippedGames)
                    .render("userStatistics", userStatistics)
                    .render("user", user)
                    .render("usersScale", usersScale);
        } else {
            return Results.redirect("/");
        }
    }
    
    public Result profile(Context context) {
        final User user = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
        final Settings settings = dataService.findSettings();
        
        return Results.html().render("user", user).render(settings);
    }

    public Result changepicture(@PathParam("avatar") String avatar, Context context) {
        final User user = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
        user.setPicture(commonService.getUserPictureUrl(commonService.getAvatarFromString(avatar), user));
        user.setAvatar(commonService.getAvatarFromString(avatar));
        dataService.save(user);
        
        return Results.redirect(USERS_PROFILE);
    }

    public Result updateusername(Session session, Context context, FlashScope flashScope) {
        String username = context.getParameter(USERNAME);

        if (!validationService.isValidUsername(username)) {
            flashScope.error(i18nService.get("controller.users.invalidusername"));
        } else if (validationService.usernameExists(username)) {
            flashScope.error(i18nService.get("controller.users.usernamexists"));
        } else {
            final User user = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
            user.setUsername(username);
            dataService.save(user);

            flashScope.success(i18nService.get("controller.profile.updateusername"));
            LOG.info("username updated: " + user.getEmail() + " / " + username);

            session.put(USERNAME, username);
        }

        return Results.redirect(USERS_PROFILE);
    }

    public Result updateemail(Context context, FlashScope flashScope) {
        String email = context.getParameter("email");
        String emailConfirmation = context.getParameter("emailConfirmation");

        if (!validationService.isValidEmail(email)) {
            flashScope.error(i18nService.get("validation.email.invalid"));
        } else if (validationService.emailExists(email)) {
            flashScope.error(i18nService.get("controller.users.emailexists"));
        } else if (!email.equalsIgnoreCase(emailConfirmation)) {
            flashScope.error(i18nService.get("validation.email.notmatch"));
        } else {
            final String token = UUID.randomUUID().toString();
            final User user = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
            if (user != null) {
                final ConfirmationType confirmationType = ConfirmationType.CHANGEUSERNAME;
                final Confirmation confirmation = new Confirmation();
                confirmation.setConfirmationType(confirmationType);
                confirmation.setConfirmValue(authService.encryptAES(email));
                confirmation.setCreated(new Date());
                confirmation.setToken(token);
                confirmation.setUser(user);
                dataService.save(confirmation);
                mailService.confirm(user, token, confirmationType);
                flashScope.success(i18nService.get(CONFIRM_MESSAGE));
            }
        }

        return Results.redirect(USERS_PROFILE);
    }

    public Result updatepassword(Context context, FlashScope flashScope) {
        String userpass = context.getParameter("userpass");
        String userpassConfirmation = context.getParameter("userpassConfirmation");

        if (!validationService.isValidPassword(userpass)) {
            flashScope.error(i18nService.get("controller.users.passwordisinvalid"));
        } else if (!userpass.equals(userpassConfirmation)) {
            flashScope.error(i18nService.get("validation.password.notmatch"));
        } else {
            final String token = UUID.randomUUID().toString();
            final User user = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
            if (user != null) {
                final ConfirmationType confirmationType = ConfirmationType.CHANGEUSERPASS;
                final Confirmation confirm = new Confirmation();
                confirm.setConfirmationType(confirmationType);
                confirm.setConfirmValue(authService.encryptAES(authService.hashPassword(userpass, user.getSalt())));
                confirm.setCreated(new Date());
                confirm.setToken(token);
                confirm.setUser(user);
                dataService.save(confirm);
                mailService.confirm(user, token, confirmationType);
                flashScope.success(i18nService.get(CONFIRM_MESSAGE));
                LOG.info("Password updated: " + user.getEmail());
            }
        }

        return Results.redirect(USERS_PROFILE);
    }

    public Result updatenotifications(Context context, FlashScope flashScope) {
        final User user = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);

        String reminder = context.getParameter("reminder");
        String notification = context.getParameter("notification");
        String sendstandings = context.getParameter("sendstandings");
        String sendgametips = context.getParameter("sendgametips");

        user.setReminder(("1").equals(reminder));
        user.setNotification(("1").equals(notification));
        user.setSendStandings(("1").equals(sendstandings));
        user.setSendGameTips(("1").equals(sendgametips));
        dataService.save(user);

        flashScope.success(i18nService.get("controller.profile.notifications"));
        LOG.info("Notifications updated: " + user.getEmail());

        return Results.redirect(USERS_PROFILE);
    }
}