package filters;

import services.DataService;
import services.I18nService;

import com.google.inject.Inject;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;
import ninja.utils.NoHttpBody;

/**
 * 
 * @author svenkubiak
 *
 */
public class AppFilter implements Filter {

    @Inject
    private DataService dataService;

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

        return result;
    }
}