package services;

import java.util.List;

import models.Game;
import models.GameTip;
import models.Playday;
import models.User;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class NotificationService {
    private static final String POINTS = "points";

    @Inject
    private I18nService i18nService;

    @Inject
    private MailService mailService;

    @Inject
    private DataService dataService;

    /**
     * Generates a notifcation message for a given game
     *
     * @param game The game
     * @return The message
     */
    public String getEmailNotificationMessage(final User user, final Game game) {
        final StringBuilder buffer = new StringBuilder();
        final GameTip gameTip = dataService.findGameTipByGameAndUser(game, user);

        buffer.append(i18nService.get("helper.tweetscore"));
        buffer.append(" ");
        buffer.append(i18nService.get(game.getHomeTeam().getName()));
        buffer.append(" - ");
        buffer.append(i18nService.get(game.getAwayTeam().getName()));
        buffer.append(" ");
        if (game.isOvertime()) {
            buffer.append(game.getHomeScoreOT());
            buffer.append(":");
            buffer.append(game.getAwayScoreOT());
            buffer.append(" (" + i18nService.get(game.getOvertimeType()) + ")");
        } else {
            buffer.append(game.getHomeScore());
            buffer.append(":");
            buffer.append(game.getAwayScore());
        }
        buffer.append(" - " + i18nService.get(game.getPlayday().getName()));
        buffer.append("\n\n");

        if (gameTip != null) {
            buffer.append(i18nService.get("yourbet") + " " + gameTip.getHomeScore() + " : " + gameTip.getAwayScore());
        }

        return buffer.toString();
    }

    /**
     * Sends notification to every user who wants to be informed on new results
     * @param game The game object
     */
    public void sendNotfications(final Game game) {
        if (!game.isEnded()) {
            final List<User> users = dataService.findUsersByNotificationAndActive();
            for (final User user : users) {
                mailService.notifications(i18nService.get("mails.subject.notification"), getEmailNotificationMessage(user, game), user);
            }
        }
    }

    /**
     * Sends the top three users to every user who is active and has
     * the "top 3" notification enabled
     */
    public void sendTopThree(Playday playday) {
        String message = "";
        final Game game = dataService.findGameFirstGame();
        if ((game != null) && game.isEnded()) {
            int count = 1;
            final StringBuilder buffer = new StringBuilder();

            List<User> users = dataService.findTopThreeUsers();
            for (final User user : users) {
                if (count < 3) {
                    buffer.append(user.getUsername() + " (" + user.getPoints() + " " + i18nService.get(POINTS) + ")\n");
                } else {
                    buffer.append(user.getUsername() + " (" + user.getPoints() + " " + i18nService.get(POINTS) + ")");
                }
                count++;
            }

            message = i18nService.get("topthree.notification", new Object[]{i18nService.get(playday.getName())}) + ": \n" + buffer.toString();

            users = dataService.findSendableUsers();
            for (final User user : users) {
                mailService.notifications(i18nService.get("mails.top3.subject"), message, user);
            }
        }
    }
}