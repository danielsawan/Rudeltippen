package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import models.AbstractJob;
import models.Game;
import models.Playday;
import models.enums.Constants;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
public class KickoffJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(KickoffJob.class);
    private static final String KICKOFF_FORMAT = "yyyy-MM-dd kk:mm:ss";

    @Inject
    private DataService dataService;
    
    @Inject
    private ResultService resultService;

    public KickoffJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        AbstractJob job = dataService.findAbstractJobByName(Constants.KICKOFFJOB.get());
        if (job != null && job.isActive() && resultService.isJobInstance()) {
            LOG.info("Started Job: " + Constants.KICKOFFJOB.get());
            List<Playday> playdays = dataService.findNextPlaydays();
            for (Playday playday : playdays) {
                for (final Game game : playday.getGames()) {
                    updateKickoff(game, game.getWebserviceID());
                }
            }
            
            job.setExecuted(new Date());
            dataService.save(job);
            LOG.info("Finished Job: " + Constants.KICKOFFJOB.get());
        }
    }

    private void updateKickoff(final Game game, final String matchID) {
        if (StringUtils.isNotBlank(matchID) && game.isUpdatable()) {
            final Document document = resultService.getDocumentFromWebService(matchID);
            final String kickoff = getKickoffFromDocument(document);
            
            if (document != null && StringUtils.isNotBlank(kickoff)) {
                final SimpleDateFormat df = new SimpleDateFormat(KICKOFF_FORMAT);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                try {
                    Date newKickoff = df.parse(kickoff);
                    if (isValidKickoff(newKickoff, game.getKickoff())) {
                        game.setKickoff(newKickoff);
                        dataService.save(game);

                        LOG.info("Updated Kickoff of game number: " + game.getNumber() + " to " + newKickoff);  
                    }
                } catch (Exception e) {
                    LOG.error("Failed to parse date from openligadb for kickoff update", e);
                }
            }
        }
    }
    
    private boolean isValidKickoff(Date newKickoff, Date currentKickoff) {
        boolean valid = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
            if (sdf.format(newKickoff).equals(sdf.format(currentKickoff))) {
                valid = true;
            }
        } catch (Exception e) {
            LOG.error("Failed to check if new kickoff date is valid", e);
        }
        
        return valid;
    }

    private static String getKickoffFromDocument(final Document document) {
        String kickoff = null;
        if (document != null) {
            final NodeList nodeList = document.getElementsByTagName("matchDateTimeUTC");
            if ((nodeList != null) && (nodeList.getLength() > 0)) {
                kickoff = nodeList.item(0).getTextContent();
                kickoff = kickoff.replace("T", " ");
                kickoff = kickoff.replace("Z", "");
            }
        }

        return kickoff;
    }
}