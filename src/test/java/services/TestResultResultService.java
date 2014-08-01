package services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import main.TestBase;
import models.Game;
import models.ws.WSResult;
import models.ws.WSResults;

import org.junit.Test;

import com.google.inject.Injector;

public class TestResultResultService extends TestBase {

    @Test
    public void testWebServiceUpdate() {
        Injector injector = getInjector();

        final Game game = new Game();
        game.setWebserviceID("19218");
        final WSResults wsResults = injector.getInstance(ResultService.class).getResultsFromWebService(game);
        final Map<String, WSResult> wsResult = wsResults.getWsResult();

//        assertNotNull(wsResults);
//        assertNotNull(wsResult);
//        assertTrue(wsResult.containsKey("90"));
//        assertTrue(wsResult.containsKey("120"));
//        assertTrue(wsResult.containsKey("121"));
//        assertEquals(wsResult.get("90").getHomeScore(), "0");
//        assertEquals(wsResult.get("90").getAwayScore(), "0");
//        assertEquals(wsResult.get("120").getHomeScore(), "0");
//        assertEquals(wsResult.get("120").getAwayScore(), "0");
//        assertEquals(wsResult.get("121").getHomeScore(), "3");
//        assertEquals(wsResult.get("121").getAwayScore(), "4");
    }
}