package controllers;

import java.util.List;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import ninja.utils.NinjaProperties;
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
    @Inject
    private DataService dataService;

    @Inject
    private ImportService importService;

    @Inject
    private SetupService setupService;
    
    @Inject
    private NinjaProperties ninjaProperties;
    
    public Result setup() {
        if (dataService.appIsInizialized()) {
            return Results.redirect("/");
        }
        dataService.dropDatabase();
        
        return Results.html();
    }

    public Result init(Session session, Context context) {
        if (!dataService.appIsInizialized()) {
            session.clear();
            
            importService.loadInitialData(context);

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