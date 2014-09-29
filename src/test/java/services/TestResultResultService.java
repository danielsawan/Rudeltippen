package services;

import static org.junit.Assert.*;

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
        game.setWebserviceID("19357");
        final WSResults wsResults = injector.getInstance(ResultService.class).getResultsFromWebService(game);
        final Map<String, WSResult> wsResult = wsResults.getWsResult();

        assertNotNull(wsResults);
        assertNotNull(wsResult);
        assertTrue(wsResult.containsKey("90"));
        assertEquals(wsResult.get("90").getHomeScore(), "4");
        assertEquals(wsResult.get("90").getAwayScore(), "2");
    }
}