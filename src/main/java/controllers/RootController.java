package controllers;

import ninja.FilterWith;
import de.svenkubiak.ninja.auth.filters.AuthenticationFilter;
import filters.AppFilter;

/**
 * 
 * @author svenkubiak
 *
 */
@FilterWith({AppFilter.class, AuthenticationFilter.class})
public class RootController {
}