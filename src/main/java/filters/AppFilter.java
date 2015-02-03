package filters;

import org.apache.commons.lang.StringUtils;

import models.User;
import models.enums.Constants;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;
import ninja.utils.NoHttpBody;
import services.DataService;
import services.I18nService;
import services.ViewService;

import com.google.inject.Inject;

import de.svenkubiak.ninja.auth.services.Authentications;

/**
 * 
 * @author svenkubiak
 *
 */
public class AppFilter implements Filter {

    @Inject
    private DataService dataService;
    
    @Inject
    private Authentications authentications;
    
    @Inject
    private ViewService viewService;

    @Inject
    private Lang lang;

    @Inject
    private I18nService i18nService;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        Result result = filterChain.next(context);
        
        if (result.getRenderable() != null && !(result.getRenderable() instanceof NoHttpBody)) {
            lang.setLanguage(i18nService.getDefaultLanguage(), result);
        }
        
        if (!dataService.appIsInizialized()) {
            return Results.redirect("/system/setup");
        }
        
        String authenticatedUser = authentications.getAuthenticatedUser(context);
        if (StringUtils.isNotBlank(authenticatedUser) && result.getRenderable() != null && !(result.getRenderable() instanceof NoHttpBody)) {
            User connectedUser = dataService.findUserByUsernameOrEmail(authenticatedUser);
            if (result.getRenderable() != null && !(result.getRenderable() instanceof NoHttpBody)) {
                result.render(Constants.CONNECTEDUSER.asString(), connectedUser);
                result.render("ViewService", viewService);
                result.render("currentPlayday", dataService.findCurrentPlayday()); 
                result.render("location", context.getRequestPath());
            }
            
            return result;
        }

        return result;
    }
}