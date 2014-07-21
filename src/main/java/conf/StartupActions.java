package conf;

import javax.inject.Singleton;

import jobs.GameTipJob;
import jobs.KickoffJob;
import jobs.ReminderJob;
import jobs.ResultJob;
import models.enums.Constants;
import ninja.NinjaScheduler;
import ninja.lifecycle.Start;
import ninja.utils.NinjaConstant;

import com.google.inject.Inject;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class StartupActions {
    private static final String RESULTSCRON = "0 */4 * * * ?";
    private static final String REMINDERCRON = "0 0 */1 * * ?";
    private static final String KICKOFFCRON = "0 0 4 * * ?";
    private static final String GAMETIPCRON = "0 */1 * * * ?";
    private static final String TRIGGER_GROUP = "triggerGroup";
    private static final String JOB_GROUP = "jobGroup";

    @Inject
    private NinjaScheduler ninjaScheduler;
    
    @Start(order=100)
    public void startup() {
        if (NinjaConstant.MODE_TEST.equals(System.getProperty(NinjaConstant.MODE_KEY_NAME))) {
            return;
        }

        ninjaScheduler.schedule(ninjaScheduler.getJobDetail(GameTipJob.class, Constants.GAMETIPJOB.get(), JOB_GROUP), ninjaScheduler.getTrigger("gameTipJobTrigger", GAMETIPCRON, TRIGGER_GROUP));
        ninjaScheduler.schedule(ninjaScheduler.getJobDetail(KickoffJob.class, Constants.KICKOFFJOB.get(), JOB_GROUP), ninjaScheduler.getTrigger("kickoffJobTrigger", KICKOFFCRON, TRIGGER_GROUP));
        ninjaScheduler.schedule(ninjaScheduler.getJobDetail(ReminderJob.class, Constants.REMINDERJOB.get(), JOB_GROUP), ninjaScheduler.getTrigger("reminderJobTrigger", REMINDERCRON, TRIGGER_GROUP));
        ninjaScheduler.schedule(ninjaScheduler.getJobDetail(ResultJob.class, Constants.RESULTJOB.get(), JOB_GROUP), ninjaScheduler.getTrigger("resultsJobTrigger", RESULTSCRON, TRIGGER_GROUP));
        ninjaScheduler.start();
    }
}