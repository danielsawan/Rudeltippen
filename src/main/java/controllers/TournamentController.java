package controllers;

import java.util.List;

import models.Bracket;
import models.Playday;
import models.pagination.Pagination;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import services.CommonService;
import services.DataService;

import com.google.inject.Inject;

/**
 * 
 * @author svenkubiak
 *
 */
public class TournamentController extends RootController {

    @Inject
    private DataService dataService;
    
    @Inject
    private CommonService commonService;

    public Result brackets() {
        List<Bracket> brackets = dataService.findAllTournamentBrackets();
        return Results.html().render("brackets", brackets);
    }

    public Result playday(@PathParam("number") long number) {
        final Pagination pagination = commonService.getPagination(number, "/tournament/playday/", dataService.findAllPlaydaysOrderByNumber().size());
        final Playday playday = dataService.findPlaydaybByNumber(pagination.getNumberAsInt());

        return Results.html().render("playday", playday).render("pagination", pagination);
    }
}