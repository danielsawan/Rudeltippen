package jobs;

import java.util.ArrayList;
import java.util.List;

import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.User;
import models.enums.Constants;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.CommonService;
import services.DataService;
import services.MailService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class ReminderJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(GameTipJob.class);

    @Inject
    private DataService dataService;
    
    @Inject
    private MailService mailService;

    @Inject
    private CommonService commonService;

    public ReminderJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (commonService.isJobInstance()) {
            LOG.info("Started Job: " + Constants.REMINDERJOB.asString());
            final List<Extra> nextExtras = dataService.findAllExtrasEnding();
            final List<Game> nextGames = dataService.findAllGamesEnding();
            final List<User> users = dataService.findAllRemindableUsers();

            for (final User user : users) {
                final List<Game> reminderGames = new ArrayList<Game>();
                final List<Extra> reminderBonus = new ArrayList<Extra>();

                for (final Game game : nextGames) {
                    final GameTip gameTip = dataService.findGameTipByGameAndUser(game, user);
                    if (gameTip == null) {
                        reminderGames.add(game);
                    }
                }

                for (final Extra extra : nextExtras) {
                    final ExtraTip extraTip = dataService.findExtraTipByExtraAndUser(extra, user);
                    if (extraTip == null) {
                        reminderBonus.add(extra);
                    }
                }

                sendNotification(user, reminderGames, reminderBonus);
            }

            disableReminder(nextExtras, nextGames);
            LOG.info("Finshed Job: " + Constants.REMINDERJOB.asString());
        }
    }

    private void sendNotification(final User user, final List<Game> reminderGames, final List<Extra> reminderBonus) {
        if (!reminderGames.isEmpty() || !reminderBonus.isEmpty()) {
            mailService.reminder(user, reminderGames, reminderBonus);
            LOG.info("Reminder send to: " + user.getEmail());
        }
    }

    private void disableReminder(final List<Extra> nextExtras, final List<Game> nextGames) {
        for (final Game game : nextGames) {
            game.setReminder(true);
            dataService.save(game);
        }

        for (final Extra extra : nextExtras) {
            extra.setReminder(true);
            dataService.save(extra);
        }
    }
}