package jobs;

import java.util.List;

import models.Game;
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
public class GameTipJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(GameTipJob.class);

    @Inject
    private DataService dataService;
    
    @Inject
    private MailService mailService;

    @Inject
    private CommonService commonService;

    public GameTipJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (commonService.isJobInstance()) {
            LOG.info("Started Job: " + Constants.GAMETIPJOB.get());
            final List<Game> games = dataService.findAllNotifiableGames();

            if (games != null && !games.isEmpty()) {
                final List<User> users = dataService.findAllNotifiableUsers();
                for (final User user : users) {
                    mailService.gametips(user, games);
                }

                for (final Game game : games) {
                    game.setInformed(true);
                    dataService.save(game);
                }
            }
            LOG.info("Finished Job: " + Constants.GAMETIPJOB.get());
        }
    }
}