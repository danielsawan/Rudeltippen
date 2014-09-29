package services;

import static org.junit.Assert.assertEquals;
import main.TestBase;
import models.User;

import org.junit.Test;

public class TestViewService extends TestBase {

    @Test
    public void testGetPlaceTrend() {
        CommonService commonService = getInjector().getInstance(CommonService.class);

        final User user = new User();
        user.setPlace(1);
        user.setPreviousPlace(0);

        assertEquals("", commonService.getPlaceTrend(user));

        user.setPlace(2);
        user.setPreviousPlace(1);

        assertEquals("<span class=\"glyphicon glyphicon-arrow-down red\"></span> (1)", commonService.getPlaceTrend(user));

        user.setPlace(1);
        user.setPreviousPlace(2);

        assertEquals("<span class=\"glyphicon glyphicon-arrow-up green\"></span> (2)", commonService.getPlaceTrend(user));

        user.setPreviousPlace(1);

        assertEquals("<span class=\"glyphicon glyphicon-minus black\"></span> (1)", commonService.getPlaceTrend(user));
    }

    @Test
    public void testGetPlaceName() {
        CommonService commonService = getInjector().getInstance(CommonService.class);

        assertEquals(commonService.getPlaceName(-5), "");
        assertEquals(commonService.getPlaceName(11), "");
        assertEquals(commonService.getPlaceName(1), "Erster");
    }
}