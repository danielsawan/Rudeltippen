package jobs;

import java.util.Date;
import java.util.List;

import models.AbstractJob;
import models.Game;
import models.User;
import models.enums.Constants;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.DataService;
import services.MailService;
import services.ResultService;

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
    private ResultService resultService;

    public GameTipJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        AbstractJob job = dataService.findAbstractJobByName(Constants.GAMETIPJOB.get());
        if (job != null && job.isActive() && resultService.isJobInstance()) {
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

            job.setExecuted(new Date());
            dataService.save(job);
            LOG.info("Finished Job: " + Constants.GAMETIPJOB.get());
        }
    }
}