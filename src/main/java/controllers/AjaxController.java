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
import ninja.mongodb.MongoDB;
import ninja.params.PathParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private MongoDB mongoDB;

    public Result webserviceid(@PathParam("gameId") String gameId, Context context) {
        Game game = mongoDB.findById(gameId, Game.class);
        if (game != null) {
            final String webserviceID = context.getParameter(VALUE);
            if (StringUtils.isNotBlank(webserviceID)) {
                game.setWebserviceID(webserviceID);
                mongoDB.save(game);

                return Results.noContent();
            }
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result kickoff(@PathParam("gameId") String gameId, Context context) {
        Game game = mongoDB.findById(gameId, Game.class);
        if (game != null) {
            final String kickoff = context.getParameter(VALUE);
            if (StringUtils.isNotBlank(kickoff)) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.ENGLISH);
                    game.setKickoff(simpleDateFormat.parse(kickoff));
                    game.setUpdatable(false);
                    mongoDB.save(game);

                    return Results.noContent();
                } catch (Exception e) {
                    LOG.error("Failed to save kickoff", e);
                }
            }
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result place(@PathParam("teamId") String teamId, Context context) {
        Team team = mongoDB.findById(teamId, Team.class);
        if (team != null) {
            final String place = context.getParameter(VALUE);
            if (StringUtils.isNotBlank(place)) {
                team.setPlace(Integer.valueOf(place));
                mongoDB.save(team);

                Bracket bracket = team.getBracket();
                bracket.setUpdatable(false);
                mongoDB.save(bracket);

                return Results.noContent();
            }
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result updatablegame(@PathParam("gameId") String gameId) {
        Game game = mongoDB.findById(gameId, Game.class);
        if (game != null) {
            game.setUpdatable(!game.isUpdatable());
            mongoDB.save(game);

            return Results.noContent();
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }

    public Result updatablebracket(@PathParam("bracketId") String bracketId) {
        Bracket bracket = mongoDB.findById(bracketId, Bracket.class);
        if (bracket != null) {
            bracket.setUpdatable(!bracket.isUpdatable());
            mongoDB.save(bracket);

            return Results.noContent();
        }
        
        return Results.badRequest().render(Result.NO_HTTP_BODY);
    }
}