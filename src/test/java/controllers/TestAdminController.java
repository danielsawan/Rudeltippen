package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import main.TestBase;
import ninja.Result;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

public class TestAdminController extends TestBase {

    @Test
    public void testUsers() throws ClientProtocolException, IOException {
        HttpResponse response = getRequest("/admin/users", true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = getRequest("/admin/users", true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response = getRequest("/admin/users", false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testResults() throws ClientProtocolException, IOException {
        HttpResponse response = getRequest("/admin/results/1", true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = getRequest("/admin/results/1", true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response =  getRequest("/admin/results/1", false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testStoreResults() throws ClientProtocolException, IOException {
        HttpResponse response = postRequest("/admin/storeresults", true);

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        response = postRequest("/admin/storeresults", true);
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        response = postRequest("/admin/storeresults", false);
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());

        doLogout();
    }
    
    @Test
    public void testSettings() throws ClientProtocolException, IOException {
        String url = getServerAddress() + "/admin/settings";
        
        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).disableRedirectHandling().build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testUpdatesettings() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testChangeactive() throws ClientProtocolException, IOException {
        String url = getServerAddress() + "/admin/changeactive/51e1eefc065f908c10000411";
        
        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).disableRedirectHandling().build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testChangeadmin() throws ClientProtocolException, IOException {
        String url = getServerAddress() + "/admin/changeadmin/51e1eefc065f908c10000411";
        
        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).disableRedirectHandling().build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testDeleteuser() throws ClientProtocolException, IOException {
        String url = getServerAddress() + "/admin/deleteuser/51e1eefc065f908c10000411";
        
        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).disableRedirectHandling().build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testRudelmail() throws ClientProtocolException, IOException {
        String url = getServerAddress() + "/admin/rudelmail";
        
        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).disableRedirectHandling().build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testTournament() throws ClientProtocolException, IOException {
        String url = getServerAddress() + "/admin/tournament";
        
        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).disableRedirectHandling().build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
    
    @Test
    public void testSend() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testJobstatus() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testCalculations() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testReset() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testJobs() throws ClientProtocolException, IOException {
        String url = getServerAddress() + "/admin/jobs";
        
        HttpClient httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogin(USER, USER);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).disableRedirectHandling().build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_303_SEE_OTHER, response.getStatusLine().getStatusCode());
        
        doLogout();
        doLogin(ADMIN, ADMIN);
        
        httpclient = HttpClientBuilder.create().setDefaultCookieStore(getCookies()).build();
        response = httpclient.execute(new HttpGet(url));
        
        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
        
        doLogout();
    }
}