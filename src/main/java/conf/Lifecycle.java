package conf;

import javax.inject.Singleton;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import jobs.GameTipJob;
import jobs.KickoffJob;
import jobs.ReminderJob;
import jobs.ResultJob;
import models.enums.Constants;
import ninja.NinjaScheduler;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaConstant;
import services.I18nService;

import com.google.inject.Inject;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class Lifecycle {
    private static final String RESULTSCRON = "0 0/5 * * * ?";
    private static final String REMINDERCRON = "0 0 0/1 * * ?";
    private static final String KICKOFFCRON = "0 0 4 * * ?";
    private static final String GAMETIPCRON = "0 0/1 * * * ?";
    private static final String TRIGGER_GROUP = "triggerGroup";
    private static final String JOB_GROUP = "jobGroup";

    @Inject
    private NinjaScheduler ninjaScheduler;
    
    @Inject
    private I18nService i18nService;
    
    @Start(order = 90)
    public void startup() {
        if (NinjaConstant.MODE_TEST.equals(System.getProperty(NinjaConstant.MODE_KEY_NAME))) {
            return;
        }
        
        JobDetail gameTipJob = ninjaScheduler.getJobDetail(GameTipJob.class, Constants.GAMETIPJOB.asString(), JOB_GROUP);
        Trigger gameTipTrigger = ninjaScheduler.getTrigger("gameTipJobTrigger", GAMETIPCRON, TRIGGER_GROUP, i18nService.get("job.gametipjob.description"));
        
        JobDetail kickoffJob = ninjaScheduler.getJobDetail(KickoffJob.class, Constants.KICKOFFJOB.asString(), JOB_GROUP);
        Trigger kickoffTrigger = ninjaScheduler.getTrigger("kickoffJobTrigger", KICKOFFCRON, TRIGGER_GROUP, i18nService.get("job.resultsjob.descrption"));

        JobDetail reminderJob = ninjaScheduler.getJobDetail(ReminderJob.class, Constants.REMINDERJOB.asString(), JOB_GROUP);
        Trigger reminderTrigger = ninjaScheduler.getTrigger("reminderJobTrigger", REMINDERCRON, TRIGGER_GROUP, i18nService.get("job.reminderjob.description"));
        
        JobDetail resultJob = ninjaScheduler.getJobDetail(ResultJob.class, Constants.RESULTJOB.asString(), JOB_GROUP);
        Trigger resultTrigger = ninjaScheduler.getTrigger("resultsJobTrigger", RESULTSCRON, TRIGGER_GROUP, i18nService.get("job.resultsjob.descrption"));
        
        ninjaScheduler.schedule(gameTipJob, gameTipTrigger);
        ninjaScheduler.schedule(kickoffJob, kickoffTrigger);
        ninjaScheduler.schedule(reminderJob, reminderTrigger);
        ninjaScheduler.schedule(resultJob, resultTrigger);
        ninjaScheduler.start();
    }
    
    @Dispose(order = 90)
    public void shutdown() {
        if (NinjaConstant.MODE_TEST.equals(System.getProperty(NinjaConstant.MODE_KEY_NAME))) {
            return;
        }
        
        ninjaScheduler.shutdown();
    }
}