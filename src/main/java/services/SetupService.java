package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import models.enums.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class SetupService {
    private static final String MATCH_DATE_TIME = "matchDateTime";
    private static final Logger LOG = LoggerFactory.getLogger(SetupService.class);

    public List<String> getGamesFromWebService(final int playdays, final String leagueShortcut, final String leagueSaison) {
        final Map<String, String> teams = getBundesligaTeams();

        int number = 1;
        final List<String> games = new ArrayList<String>();
        for (int k=1; k <= playdays; k++) {
            final Document document = getDocumentFromWebService(String.valueOf(k), leagueShortcut, leagueSaison);
            final NodeList nodeList = document.getElementsByTagName("Matchdata");
            for (int i=0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final NodeList childs = node.getChildNodes();

                String webserviceID = null;
                String kickoff = null;
                String homeTeam = null;
                String awayTeam = null;

                for (int j=0; j < childs.getLength(); j++) {
                    final Node childNode = childs.item(j);
                    final String name = childNode.getNodeName();
                    String value = childNode.getTextContent();

                    if ("matchID".equals(name)) {
                        webserviceID = value;
                    } else if (MATCH_DATE_TIME.equals(name)) {
                        value = value.replace("T", " ");
                        value = value.replace("Z", "");
                        kickoff = value;
                    } else if (("idTeam1").equals(name)) {
                        homeTeam = teams.get(value);
                    } else if (("idTeam2").equals(name)) {
                        awayTeam = teams.get(value);
                    }
                }
                
                StringBuffer buffer = new StringBuffer();
                buffer.append("{");
                buffer.append("\"number\":");
                buffer.append(number);
                buffer.append(",");
                buffer.append("\"homeTeam\":");
                buffer.append("\"$" + homeTeam + "\"");
                buffer.append(",");
                buffer.append("\"awayTeam\":");
                buffer.append("\"$" + awayTeam + "\"");
                buffer.append(",");
                buffer.append("\"kickoff\":");
                buffer.append("\"" + kickoff + "\"");
                buffer.append(",");
                buffer.append("\"playday\":");
                buffer.append("\"$P" + k + "\"");
                buffer.append(",");
                buffer.append("\"bracket\":");
                buffer.append("\"$B1\"");
                buffer.append(",");
                buffer.append("\"playoff\":");
                buffer.append("false");  
                buffer.append(",");
                buffer.append("\"ended\":");
                buffer.append("false"); 
                buffer.append(",");
                buffer.append("\"updatable\":");
                buffer.append("true"); 
                buffer.append(",");
                buffer.append("\"webserviceID\":");
                buffer.append("\"" + webserviceID + "\"");
                buffer.append("}");
                buffer.append("<br>");
                number++;
                
                games.add(buffer.toString());
            }
        }

        return games;
    }

    public static Map<String, String> getBundesligaTeams() {
        final Map<String, String> teams = new HashMap<String, String>();
        teams.put("7", "BVB");
        teams.put("134", "SWB");
        teams.put("87", "BMG");
        teams.put("123", "TSG");
        teams.put("16", "VFB");
        teams.put("131", "VFL");
        teams.put("55", "H96");
        teams.put("9", "S04");
        teams.put("112", "SCF");
        teams.put("81", "M05");
        teams.put("95", "FCA");
        teams.put("185", "FD");
        teams.put("100", "HSV");
        teams.put("79", "FCN");
        teams.put("115", "SGF");
        teams.put("40", "FCB");
        teams.put("91", "EF");
        teams.put("6", "B04");
        teams.put("74", "EB");
        teams.put("54", "BSC");
        teams.put("31", "SCP");
        teams.put("65", "FCK");

        return teams;
    }

    public Document getDocumentFromWebService(final String group, final String leagueShortcut, final String leagueSaison) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
        buffer.append("<soap12:Body>");
        buffer.append("<GetMatchdataByGroupLeagueSaison xmlns=\"http://msiggi.de/Sportsdata/Webservices\">");
        buffer.append("<groupOrderID>" + group + "</groupOrderID>");
        buffer.append("<leagueShortcut>" + leagueShortcut + "</leagueShortcut>");
        buffer.append("<leagueSaison>" + leagueSaison + "</leagueSaison>");
        buffer.append("</GetMatchdataByGroupLeagueSaison>");
        buffer.append("</soap12:Body>");
        buffer.append("</soap12:Envelope>");

        Document document = null;
        try {
            HttpResponse httpResponse = Request
                    .Post(Constants.WS_URL.get())
                    .setHeader("Content-Type", Constants.WS_COTENT_TYPE.get())
                    .setHeader("charset", Constants.ENCODING.get())
                    .bodyString(buffer.toString(), ContentType.TEXT_XML)
                    .execute()
                    .returnResponse();
                
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                document = builder.parse(httpResponse.getEntity().getContent());
        } catch (final Exception e) {
            LOG.error("Failed to get league data from webservice", e);
        }

        return document;
    }

    public Date getKickoffFromDocument(final Document document) {
        Date date = new Date();
        if (document != null) {
            final NodeList nodeList = document.getElementsByTagName(MATCH_DATE_TIME);
            if ((nodeList != null) && (nodeList.getLength() > 0)) {
                String kickoff = nodeList.item(0).getTextContent();
                kickoff = kickoff.replace("T", " ");
                kickoff = kickoff.replace("Z", "");
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    date = df.parse(kickoff);
                } catch (final ParseException e) {
                    LOG.error("Failed to parse Date for kickoff update", e);
                }
            }
        }

        return date;
    }
}