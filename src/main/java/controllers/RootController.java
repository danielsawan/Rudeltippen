package controllers;

import ninja.FilterWith;
import filters.AppFilter;
import filters.AuthenticationFilter;

/**
 * 
 * @author svenkubiak
 *
 */
@FilterWith({AppFilter.class, AuthenticationFilter.class})
public class RootController {
}