package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import main.TestBase;
import ninja.Result;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

public class TestAjaxController extends TestBase {
    
    @Test
    public void testWebserviceid() throws ClientProtocolException, IOException {
        String url = "/ajax/game/webserviceid/foo";
        
        HttpResponse response = postRequest(url, true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = postRequest(url, true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response = postRequest(url, false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testKickoff() throws ClientProtocolException, IOException {
        String url = "/ajax/game/kickoff/foo";
        
        HttpResponse response = postRequest(url, true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = postRequest(url, true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response = postRequest(url, false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testPlace() throws ClientProtocolException, IOException {
        String url = "/ajax/bracket/place/foo";

        HttpResponse response = postRequest(url, true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = postRequest(url, true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response = postRequest(url, false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testUpdatablegame() throws ClientProtocolException, IOException {
        String url = "/ajax/game/updatable/foo";
        
        HttpResponse response = getRequest(url, true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = getRequest(url, true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response = getRequest(url, false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testUpdatablebracket() throws ClientProtocolException, IOException {
        String url = "/ajax/bracket/updatable/foo";
        
        HttpResponse response = getRequest(url, true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = getRequest(url, true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response = getRequest(url, false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
}