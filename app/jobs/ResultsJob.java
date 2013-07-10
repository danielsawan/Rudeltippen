package jobs;

import java.util.List;

import models.Game;
import models.WSResults;
import play.Logger;
import play.jobs.Every;
import utils.AppUtils;
import utils.WSUtils;

@Every("1min")
public class ResultsJob extends AppJob {

    public ResultsJob() {
        this.setDescription("Checks if games have started and if results for this games are available.");
        this.setExecuted("Runs every minute");
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
			Logger.info("Started Job: ResultsJob");
			final List<Game> games = Game.find("SELECT g FROM Game g WHERE ended != 1 AND ( TIMESTAMPDIFF(MINUTE,kickoff,now()) > 90 ) AND homeTeam_id != '' AND awayTeam_id != '' AND webserviceID != ''").fetch();
			for (final Game game : games) {
				final WSResults wsResults = WSUtils.getResultsFromWebService(game);
				if ((wsResults != null) && wsResults.isUpdated()) {
					AppUtils.setGameScoreFromWebService(game, wsResults);
				}
			}
			Logger.info("Finished Job: ResultsJob");
        }
    }
}