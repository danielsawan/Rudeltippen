package controllers;

import java.text.SimpleDateFormat;
import java.util.Locale;

import models.Bracket;
import models.Game;
import models.Team;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.DataService;

import com.google.inject.Inject;

import filters.AdminFilter;

/**
 * 
 * @author svenkubiak
 *
 */
@FilterWith(AdminFilter.class)
public class AjaxController extends RootController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private static final String VALUE = "value";

    @Inject
    private DataService dataService;

    public Result webserviceid(@PathParam("gameId") String gameId, Context context) {
        Game game = dataService.findGameById(gameId);
        if (game != null) {
            final String webserviceID = context.getParameter(VALUE);
            if (StringUtils.isNotBlank(webserviceID)) {
                game.setWebserviceID(webserviceID);
                dataService.save(game);

                return Results.noContent();
            }
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result kickoff(@PathParam("gameId") String gameId, Context context) {
        Game game = dataService.findGameById(gameId);
        if (game != null) {
            final String kickoff = context.getParameter(VALUE);
            if (StringUtils.isNotBlank(kickoff)) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.ENGLISH);
                    game.setKickoff(simpleDateFormat.parse(kickoff));
                    game.setUpdatable(false);
                    dataService.save(game);

                    return Results.noContent();
                } catch (Exception e) {
                    LOG.error("Failed to save kickoff", e);
                }
            }
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result place(@PathParam("teamId") String teamId, Context context) {
        Team team = dataService.findTeamById(teamId);
        if (team != null) {
            final String place = context.getParameter(VALUE);
            if (StringUtils.isNotBlank(place)) {
                team.setPlace(Integer.parseInt(place));
                dataService.save(team);

                Bracket bracket = team.getBracket();
                bracket.setUpdatable(false);
                dataService.save(bracket);

                return Results.noContent();
            }
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result updatablegame(@PathParam("gameId") String gameId) {
        Game game = dataService.findGameById(gameId);
        if (game != null) {
            game.setUpdatable(!game.isUpdatable());
            dataService.save(game);

            return Results.noContent();
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result updatablebracket(@PathParam("bracketId") String bracketId) {
        Bracket bracket = dataService.findBracketById(bracketId);
        if (bracket != null) {
            bracket.setUpdatable(!bracket.isUpdatable());
            dataService.save(bracket);

            return Results.noContent();
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }
}