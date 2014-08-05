package filters;

import models.User;
import models.enums.Constants;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;

/**
 * 
 * @author svenkubiak
 *
 */
public class AdminFilter implements Filter {
    
    @Override
    public Result filter(FilterChain filterChain, Context context) {
        User connectedUser = context.getAttribute(Constants.CONNECTEDUSER.get(), User.class);
        if (connectedUser == null || !connectedUser.isAdmin()) {
            return Results.redirect("/");
        }

        return filterChain.next(context);
    }
}