package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import models.Game;
import models.Settings;
import models.User;
import models.enums.Constants;
import models.ws.WSResult;
import models.ws.WSResults;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class ResultService {
    private static final Logger LOG = LoggerFactory.getLogger(ResultService.class);
    private static final String MATCH_IS_FINISHED = "matchIsFinished";

    @Inject
    private MailService mailService;

    @Inject
    private DataService dataService;

    public WSResults getResultsFromWebService(final Game game) {
        WSResults wsResults = new WSResults();
        wsResults.setUpdated(false);
        final String matchID = game.getWebserviceID();
        if (StringUtils.isNotBlank(matchID)) {
            final Document document = getDocumentFromWebService(matchID);
            if ((document != null) && (document.getElementsByTagName(MATCH_IS_FINISHED).getLength() > 0)) {
                final String matchIsFinished = document.getElementsByTagName(MATCH_IS_FINISHED).item(0).getTextContent();
                if (("true").equalsIgnoreCase(matchIsFinished)) {
                    wsResults = getEndResult(wsResults, document);
                    wsResults.setUpdated(true);
                }
            }
        }
        return wsResults;
    }

    public Document getDocumentFromWebService(final String matchID) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
        buffer.append("<soap12:Body>");
        buffer.append("<GetMatchByMatchID xmlns=\"http://msiggi.de/Sportsdata/Webservices\">");
        buffer.append("<MatchID>" + matchID + "</MatchID>");
        buffer.append("</GetMatchByMatchID>");
        buffer.append("</soap12:Body>");
        buffer.append("</soap12:Envelope>");

        Document document = null;
        try {
            HttpResponse httpResponse = Request
                    .Post(Constants.WS_URL.asString())
                    .setHeader("Content-Type", Constants.WS_COTENT_TYPE.asString())
                    .setHeader("charset", Constants.ENCODING.asString())
                    .bodyString(buffer.toString(), ContentType.TEXT_XML)
                    .execute()
                    .returnResponse();

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(httpResponse.getEntity().getContent());
        } catch (final Exception e) {
            final List<User> users = dataService.findAllAdmins();
            for (final User user : users) {
                mailService.error(e.getMessage(), user.getEmail());
            }

            LOG.error("Updating of results from WebService failed", e);
        }

        return document;
    }

    private WSResults getEndResult(final WSResults wsResults, final Document document) {
        final Map<String, WSResult> resultsMap = new HashMap<String, WSResult>();
        final Node matchResults = document.getElementsByTagName("matchResults").item(0);
        final NodeList matchResult = matchResults.getChildNodes();

        for (int i=0; i < matchResult.getLength(); i++) {
            final NodeList singleResults = matchResult.item(i).getChildNodes();
            final String name = singleResults.item(0).getTextContent();

            if (StringUtils.isBlank(name)) {
                continue;
            }

            final WSResult wsResult = new WSResult();
            String key = null;
            if (("Endergebnis").equalsIgnoreCase(name)) {
                key = "90";
            } else if (("Verlängerung").equalsIgnoreCase(name)) {
                key = "120";
            } else if (("Elfmeterschiessen").equalsIgnoreCase(name)) {
                key = "121";
            }

            if (StringUtils.isNotBlank(key)) {
                wsResult.setHomeScore(singleResults.item(1).getTextContent());
                wsResult.setAwayScore(singleResults.item(2).getTextContent());
                resultsMap.put(key, wsResult);
            }
        }
        wsResults.setWsResult(resultsMap);

        return wsResults;
    }


    public int getTipPoints(final int homeScore, final int awayScore, final int homeScoreTipp, final int awayScoreTipp) {
        final Settings settings = dataService.findSettings();
        int points = 0;

        if ((homeScore == homeScoreTipp) && (awayScore == awayScoreTipp)) {
            points = settings.getPointsTip();
        } else if ((homeScore - awayScore) == (homeScoreTipp - awayScoreTipp)) {
            points = settings.getPointsTipDiff();
        } else if ((awayScore - homeScore) == (awayScoreTipp - homeScoreTipp)) {
            points = settings.getPointsTipDiff();
        } else {
            points = getTipPointsTrend(homeScore, awayScore, homeScoreTipp, awayScoreTipp);
        }

        return points;
    }

    public int getTipPointsTrend(final int homeScore, final int awayScore, final int homeScoreTipp, final int awayScoreTipp) {
        final Settings settings = dataService.findSettings();
        int points = 0;

        if ((homeScore > awayScore) && (homeScoreTipp > awayScoreTipp)) {
            points = settings.getPointsTipTrend();
        } else if ((homeScore < awayScore) && (homeScoreTipp < awayScoreTipp)) {
            points = settings.getPointsTipTrend();
        }

        return points;
    }

    public int getTipPointsOvertime(final int homeScore, final int awayScore, final int homeScoreOT, final int awayScoreOT, final int homeScoreTipp, final int awayScoreTipp) {
        final Settings settings = dataService.findSettings();
        int points = 0;

        if ((homeScore == awayScore) && (homeScore == homeScoreTipp) && (awayScore == awayScoreTipp)) {
            points = settings.getPointsTip();
        } else if ((homeScore == awayScore) && (homeScoreTipp == awayScoreTipp)) {
            points = settings.getPointsTipDiff();
        }

        return points;
    }

    public int[] getPoints(final int homeScore, final int awayScore) {
        final Settings settings = dataService.findSettings();
        final int[] points = new int[2];

        if (homeScore == awayScore) {
            points[0] = settings.getPointsGameDraw();
            points[1] = settings.getPointsGameDraw();
        } else if (homeScore > awayScore) {
            points[0] = settings.getPointsGameWin();
            points[1] = 0;
        } else if (homeScore < awayScore) {
            points[0] = 0;
            points[1] = settings.getPointsGameWin();
        }

        return points;
    }
}