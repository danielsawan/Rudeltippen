package controllers;

import java.util.List;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.cache.NinjaCache;
import ninja.session.Session;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.DataService;
import services.ImportService;
import services.SetupService;

import com.google.inject.Inject;

/**
 * 
 * @author svenkubiak
 *
 */
public class SystemController {
    private static final Logger LOG = LoggerFactory.getLogger(SystemController.class);

    @Inject
    private DataService dataService;

    @Inject
    private ImportService importService;

    @Inject
    private SetupService setupService;
    
    @Inject
    private NinjaProperties ninjaProperties;
    
    @Inject
    private NinjaCache ninjaCache;

    public Result setup() {
        if (dataService.appIsInizialized()) {
            return Results.redirect("/");
        }
        dataService.dropDatabase();
        
        return Results.html();
    }

    public Result init(Session session, Context context) {
        if (!dataService.appIsInizialized()) {
            ninjaCache.clear();
            session.clear();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOG.error("Failed while trying to sleep in system/init", e);
            }

            importService.loadInitialData();

            return Results.ok().render(Result.NO_HTTP_BODY);
        }

        return Results.redirect("/");
    }

    public Result data() {
        if (("true").equals(ninjaProperties.get("rudeltippen.data.generator"))) {
            final List<String> games = setupService.getGamesFromWebService(34, "bl1", "2014");
            return Results.html().render("games", games);
        }

        return Results.redirect("/");
    }
}