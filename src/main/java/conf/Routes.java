package conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.AdminController;
import controllers.AjaxController;
import controllers.ApplicationController;
import controllers.AuthController;
import controllers.SystemController;
import controllers.TipController;
import controllers.TournamentController;
import controllers.UserController;

/**
 * 
 * @author svenkubiak
 *
 */
public class Routes implements ApplicationRoutes {
    private static final String PLAYDAY = "playday";
    private static final String SERVE_STATIC = "serveStatic";

    @Override
    public void init(Router router) {
        /** Auth Controller **/
        router.GET().route("/auth/login").with(AuthController.class, "login");
        router.GET().route("/auth/confirm/{token}").with(AuthController.class, "confirm");
        router.GET().route("/auth/logout").with(AuthController.class, "logout");
        router.GET().route("/auth/register").with(AuthController.class, "register");
        router.GET().route("/auth/forgotten").with(AuthController.class, "forgotten");
        router.GET().route("/auth/password/{token}").with(AuthController.class, "password");
        router.POST().route("/auth/renew").with(AuthController.class, "renew");
        router.POST().route("/auth/reset").with(AuthController.class, "reset");
        router.POST().route("/auth/authenticate").with(AuthController.class, "authenticate");
        router.POST().route("/auth/create").with(AuthController.class, "create");
        
        /** Admin Controller **/
        router.GET().route("/admin/rudelmail").with(AdminController.class, "rudelmail");
        router.GET().route("/admin/settings").with(AdminController.class, "settings");
        router.GET().route("/admin/users").with(AdminController.class, "users");
        router.GET().route("/admin/jobs").with(AdminController.class, "jobs");
        router.GET().route("/admin/changeactive/{userid}").with(AdminController.class, "changeactive");
        router.GET().route("/admin/changeadmin/{userid}").with(AdminController.class, "changeadmin");
        router.GET().route("/admin/deleteuser/{userid}").with(AdminController.class, "deleteuser");
        router.GET().route("/admin/results/{number}").with(AdminController.class, "results");
        router.GET().route("/admin/tournament").with(AdminController.class, "tournament");
        router.GET().route("/admin/calculations").with(AdminController.class, "calculations");
        router.GET().route("/admin/jobstatus/{name}").with(AdminController.class, "jobstatus");
        router.GET().route("/admin/executejob/{name}").with(AdminController.class, "executeJob");
        router.POST().route("/admin/updatesettings").with(AdminController.class, "updatesettings");
        router.POST().route("/admin/storeresults").with(AdminController.class, "storeresults");
        router.POST().route("/admin/reset").with(AdminController.class, "reset");
        
        /** Tips Controller **/
        router.GET().route("/tips/overview/{number}").with(TipController.class, "overview");
        router.GET().route("/tips/extras").with(TipController.class, "extras");
        router.GET().route("/tips/rules").with(TipController.class, "rules");
        router.GET().route("/tips/playday/{number}").with(TipController.class, PLAYDAY);
        router.GET().route("/tips/standings").with(TipController.class, "standings");
        router.GET().route("/tips/statistics").with(TipController.class, "statistics");
        router.POST().route("/tips/storetips").with(TipController.class, "storetips");
        
        /** Tournament Controller **/
        router.GET().route("/tournament/playday/{number}").with(TournamentController.class, PLAYDAY);
        router.GET().route("/tournament/brackets").with(TournamentController.class, "brackets");
        
        /** User Controller **/
        router.GET().route("/users/myprofile").with(UserController.class, "profile");
        router.GET().route("/users/show/{username}").with(UserController.class, "show");
        router.GET().route("/users/changepicture/{avatar}").with(UserController.class, "changepicture");
        router.POST().route("/users/updateusername").with(UserController.class, "updateusername");
        router.POST().route("/users/updatepassword").with(UserController.class, "updatepassword");
        router.POST().route("/users/updateemail").with(UserController.class, "updateemail");
        router.POST().route("/users/updatenotifications").with(UserController.class, "updatenotifications");
        
        /** Ajax Controller **/
        router.GET().route("/ajax/bracket/updatable/{bracketId}").with(AjaxController.class, "updatablebracket");
        router.GET().route("/ajax/game/updatable/{gameId}").with(AjaxController.class, "updatablegame");
        router.POST().route("/ajax/bracket/place/{teamId}").with(AjaxController.class, "place");
        router.POST().route("/ajax/game/kickoff/{gameId}").with(AjaxController.class, "kickoff");
        router.POST().route("/ajax/game/webserviceid/{gameId}").with(AjaxController.class, "webserviceid");

        /** System Controller **/
        router.GET().route("/system/setup").with(SystemController.class, "setup");
        router.GET().route("/system/data").with(SystemController.class, "data");
        router.GET().route("/system/init").with(SystemController.class, "init");
        
        /** Application Controller **/
        router.GET().route("/").with(ApplicationController.class, "index");
        
        /** Assets Controller **/
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, SERVE_STATIC);
        router.GET().route("/robots.txt").with(AssetsController.class, SERVE_STATIC);
    }
}