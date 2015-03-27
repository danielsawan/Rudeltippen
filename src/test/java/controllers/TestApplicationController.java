package controllers;

import java.io.IOException;

import main.TestBase;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

public class TestApplicationController extends TestBase {
    @Test
    public void testIndex() throws ClientProtocolException, IOException {
//        String url = getServerAddress() + "/";
//        
//        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
//        HttpResponse response = httpclient.execute(new HttpGet(url));
//
//        assertNotNull(response);
//        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
//        
//        doLogin(USER, USER);
//        
//        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
//        response = httpclient.execute(new HttpGet(url));
//        
//        assertNotNull(response);
//        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
//        
//        doLogout();
    }
}