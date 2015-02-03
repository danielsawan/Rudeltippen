package services;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Bracket;
import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.Playday;
import models.Team;
import models.User;
import models.enums.Avatar;
import models.pagination.Pagination;
import ninja.utils.NinjaProperties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

@Singleton
public class CommonService extends ViewService {
    private static final Logger LOG = LoggerFactory.getLogger(CommonService.class);
    private static final int TIMEOUT = 4000;
    private static final int VERSION_21 = 21;
    private static final int VERSION_3 = 3;
    private static final int VERSION_2 = 2;
    
    @Inject
    private I18nService i18nService;

    @Inject
    private DataService dataService;
    
    @Inject
    private NinjaProperties ninjaProperties;

    /**
     * Checks if all games in given list have ended
     *
     * @param games The games to check
     * @return true if all games have ended, false otherwise
     */
    public boolean allReferencedGamesEnded(final List<Game> games) {
        boolean ended = true;
        if (games != null && !games.isEmpty()) {
            for (final Game game : games) {
                if (!game.isEnded()) {
                    ended = false;
                    break;
                }
            }
        }

        return ended;
    }

    /**
     * Calculates the pagination
     * 
     * @param number The number of pages
     * @param url The urls to set to the links
     * @param totalPlaydays The total number of playdays
     * @return Pagination object conatining the ready computed pagination
     */
    public Pagination getPagination(long number, final String url, long totalPlaydays) {
        final Pagination pagination = new Pagination();

        final long offsetEnd = totalPlaydays;
        long offsetStart = number - 3;
        long offset = number + 3;

        if (offsetStart <= 0) {
            offsetStart = 1;
        }

        if (offset > offsetEnd) {
            offset = offsetEnd;
        }

        pagination.setNumber(number);
        pagination.setOffsetStart(offsetStart);
        pagination.setOffset(offset);
        pagination.setOffsetEnd(offsetEnd);
        pagination.setUrl(url);

        return pagination;
    }

    public Map<String, String> convertParamaters(Map<String, String[]> parameters) {
        Map<String, String> map = new HashMap<String, String>();
        Iterator<Map.Entry<String, String[]>> entries = parameters.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String[]> entry = entries.next();
            if (entry.getValue() != null && entry.getValue().length > 0) {
                map.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        return map;
    }

    public String getProcessedTemplate(String name, Map<String, Object> content) {
        Writer writer = new StringWriter();
        Configuration configuration = new Configuration(new Version(VERSION_2, VERSION_3, VERSION_21));
        configuration.setClassForTemplateLoading(this.getClass(), "/views/mails/");

        try {
            Template template = configuration.getTemplate(name);
            template.process(content, writer);
        } catch (IOException | TemplateException e) {
            LOG.error("Failed to create template for: " + name, e);
        }

        return writer.toString();
    }

    public boolean extraIsTipable(Extra extra) {
        if (extra.getEnding() != null && (new Date().getTime() >= extra.getEnding().getTime())) {
            return false;
        }

        return true;
    }

    public String homeReferenceName (final Game game) {
        return getReference(game.getHomeReference());
    }

    public String awayReferenceName (final Game game) {
        return getReference(game.getAwayReference());
    }

    public String getPoints(final Game game, User user) {
        String points = "-";
        final List<GameTip> gameTips = game.getGameTips();

        for (final GameTip gameTip : gameTips) {
            if (gameTip.getGame().isEnded() && gameTip.getUser().equals(user)) {
                points = String.valueOf(gameTip.getPoints());
                break;
            }
        }

        return points;
    }

    private String getReference(final String reference) {
        final String [] references = reference.split("-");
        String message = "";
        if (("G").equals(references[0])) {
            if ("W".equals(references[2])) {
                message = i18nService.get("model.game.winnergame") + " " + references[1];
            } else if (("L").equals(references[2])) {
                message = i18nService.get("model.game.losergame") + " " + references[1];
            }
        } else if (("B").equals(references[0])) {
            final Bracket bracket = dataService.findBracketByNumber(references[1]);
            final String groupName = bracket.getName();
            final String placeName = getPlaceName(Integer.parseInt(references[2]));

            message = placeName + " " + i18nService.get(groupName);
        }

        return message;
    }

    public String getPlaceName(final int place) {
        String message = "";

        if (place == 1) {
            message = i18nService.get("helper.first");
        } else if (place == 2){
            message = i18nService.get("helper.second");
        } else if (place == 3){
            message = i18nService.get("helper.third");
        } else if (place == 4){
            message = i18nService.get("helper.fourth");
        } else if (place == 5){
            message = i18nService.get("helper.fifth");
        } else if (place == 6){
            message = i18nService.get("helper.six");
        } else if (place == 7){
            message = i18nService.get("helper.seventh");
        } else if (place == 8){
            message = i18nService.get("helper.eight");
        } else if (place == 9){
            message = i18nService.get("helper.ninth");
        } else if (place == 10){
            message = i18nService.get("helper.tenth");
        }

        return message;
    }

    public String getExtraTip(final Extra extra, User user) {
        final ExtraTip extraTip = dataService.findExtraTipByExtraAndUser(extra, user);
        String id = null;

        if ((extraTip != null) && (extraTip.getAnswer() != null)) {
            id = extraTip.getAnswer().getId().toString();
        }

        return id;
    }

    public String getExtraTipAnswer(final ExtraTip extraTip) {
        String answer = "-";
        if (extraTip.getAnswer() != null) {
            if (extraTip.getExtra().getEnding().getTime() < new Date().getTime()) {
                answer = i18nService.get(extraTip.getAnswer().getName());
            } else {
                answer = i18nService.get("model.user.tipped");
            }
        }

        return answer;
    }

    public String getExtraTipPoints(final ExtraTip extraTip) {
        String points = "";
        if ((extraTip != null) && (extraTip.getExtra() != null) && (extraTip.getExtra().getAnswer() != null)) {
            points = " ("+ extraTip.getPoints() + ")";
        }

        return points;
    }

    public String htmlUnescape(final String html) {
        return StringEscapeUtils.unescapeHtml(html);
    }

    public boolean allGamesEnded(Playday playday) {
        for (final Game game : playday.getGames()) {
            if (!game.isEnded()) {
                return false;
            }
        }
        return true;
    }

    public boolean allGamesEnded(Bracket bracket) {
        for (final Game game : bracket.getGames()) {
            if (!game.isEnded()) {
                return false;
            }
        }

        return true;
    }

    public Team getTeamByPlace(final int place, Bracket bracket) {
        int i = 1;
        for (final Team team : bracket.getTeams()) {
            if  (i == place) {
                return team;
            }
            i++;
        }

        return null;
    }

    public Team getWinner(Game game) {
        String home;
        String away;
        
        if (game.isOvertime()) {
            home = game.getHomeScoreOT();
            away = game.getAwayScoreOT();
        } else {
            home = game.getHomeScore();
            away = game.getAwayScore();
        }

        if (StringUtils.isNotBlank(home) && StringUtils.isNotBlank(away)) {
            final int homeScore = Integer.parseInt(home);
            final int awayScore = Integer.parseInt(away);
            if (homeScore > awayScore) {
                return game.getHomeTeam();
            } else {
                return game.getAwayTeam();
            }
        }

        return null;
    }

    public Team getLoser(Game game) {
        String home;
        String away;
        
        if (game.isOvertime()) {
            home = game.getHomeScoreOT();
            away = game.getAwayScoreOT();
        } else {
            home = game.getHomeScore();
            away = game.getAwayScore();
        }

        if (StringUtils.isNotBlank(home) && StringUtils.isNotBlank(away)) {
            final int homeScore = Integer.parseInt(home);
            final int awayScore = Integer.parseInt(away);
            if (homeScore > awayScore) {
                return game.getHomeTeam();
            } else {
                return game.getAwayTeam();
            }
        }

        return null;
    }

    /**
     * Checks if at least one extra tip in given list is tipable
     *
     * @param extras List of extra tips
     * @return true if at least one extra tip is tipable, false otherwise
     */
    public boolean extrasAreTipable(final List<Extra> extras) {
        boolean tippable = false;
        for (final Extra extra : extras) {
            if (extraIsTipable(extra)) {
                tippable = true;
                break;
            }
        }

        return tippable;
    }

    public Team getTeamByReference(final String reference) {
        Team team = null;
        final String[] references = reference.split("-");
        if (references != null && references.length == 3) {
            if (("B").equals(references[0])) {
                final Bracket bracket = dataService.findBracketByNumber(references[1]);
                if (bracket != null) {
                    team = getTeamByPlace(Integer.parseInt(references[2]), bracket);
                }
            }

            if (("G").equals(references[0])) {
                final Game game = dataService.findGameByNumber(Integer.parseInt(references[1]));
                if (game != null && game.isEnded()) {
                    if (("W").equals(references[2])) {
                        team = getWinner(game);
                    } else if (("L").equals(references[2])) {
                        team = getLoser(game);
                    }
                }
            }
        }

        return team;
    }

    public String getUserPictureUrl(Avatar avatar, User user) {
        String finalUrl = null;
        try {
            String param = user.getUsername();
            if (Avatar.GRAVATAR.equals(avatar)) {
                param = user.getEmail();
            }
            
            RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT)
                    .setConnectTimeout(TIMEOUT)
                    .build();
            
            HttpClient httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config)
                    .build();
            
            HttpGet httpGet = new HttpGet(avatar.get() + param + "?size=large");
            HttpContext httpContext = new BasicHttpContext(); 
            httpClient.execute(httpGet, httpContext); 

            HttpUriRequest currentReq = (HttpUriRequest) httpContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
            HttpHost currentHost = (HttpHost)  httpContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            
            finalUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI()); 
        } catch (Exception e) {
            LOG.error("Failed to get user picture url", e);
        }
        
        if (StringUtils.isNotBlank(finalUrl)) {
            finalUrl = finalUrl.replace("http:", "");
            finalUrl = finalUrl.replace("https:", "");
        }
        
        return (StringUtils.isNotBlank(finalUrl)) ? finalUrl : avatar.get();
    }
    
    public Avatar getAvatarFromString(String string) {
        Avatar avatar = null;
        if (Avatar.FACEBOOK.get().contains(string)) {
            avatar = Avatar.FACEBOOK;
        } else if (Avatar.GRAVATAR.get().contains(string)) {
            avatar = Avatar.GRAVATAR;
        } else if (Avatar.INSTAGRAM.get().contains(string)) {
            avatar = Avatar.INSTAGRAM;
        } else if (Avatar.TWITTER.get().contains(string)) {
            avatar = Avatar.TWITTER;
        } else {
            avatar = Avatar.GRAVATAR;
        }
        
        return avatar;
    }

    public boolean isJobInstance() {
        boolean isInstance = false;
        final String jobInstance = ninjaProperties.get("rudeltippen.jobinstance");
        if (("true").equalsIgnoreCase(jobInstance)) {
            isInstance = true;
        }

        return isInstance;
    }
}