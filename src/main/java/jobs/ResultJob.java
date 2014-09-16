package jobs;

import java.util.Date;
import java.util.List;

import models.AbstractJob;
import models.Game;
import models.enums.Constants;
import models.ws.WSResults;
import ninja.mongodb.MongoDB;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.CalculationService;
import services.DataService;
import services.ResultService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class ResultJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(GameTipJob.class);

    @Inject
    private DataService dataService;
    
    @Inject
    private MongoDB mongoDB;

    @Inject
    private CalculationService calculationService;

    @Inject
    private ResultService resultService;

    public ResultJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (resultService.isJobInstance()) {
            AbstractJob job = dataService.findAbstractJobByName(Constants.RESULTJOB.get());
            if (job != null && job.isActive()) {
                LOG.info("Started Job: " + Constants.RESULTJOB.get());
                final List<Game> games = dataService.findAllGamesWithNoResult();
                for (final Game game : games) {
                    setGameScore(game);
                }
                
                job.setExecuted(new Date());
                mongoDB.save(job);
                LOG.info("Finished Job: " + Constants.RESULTJOB.get());
            }
        }
    }

    private void setGameScore(final Game game) {
        final WSResults wsResults = resultService.getResultsFromWebService(game);
        if ((wsResults != null) && wsResults.isUpdated()) {
            calculationService.setGameScoreFromWebService(game, wsResults);
        }
    }
}