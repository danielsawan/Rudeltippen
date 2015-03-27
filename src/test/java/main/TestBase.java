package main;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;
import models.enums.Avatar;
import ninja.NinjaTest;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.CommonService;
import services.DataService;
import services.ImportService;
import de.svenkubiak.embeddedmongodb.EmbeddedMongo;
import de.svenkubiak.ninja.auth.services.Authentications;

public class TestBase extends NinjaTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestBase.class);
    private static DataService dataService;
    private static Authentications authentications;
    
    public static final String ADMIN = "admin";
    public static final String USER = "user";
    
    public TestBase() {
        EmbeddedMongo.DB.start();
    }

    @Before
    public void init() {
        try {
            authentications = getInjector().getInstance(Authentications.class);
            
            dataService = getInjector().getInstance(DataService.class);
            dataService.setMongoClient(EmbeddedMongo.DB.getMongoClient());
        } catch (Exception e) {
            LOG.error("Failed to start in memory mongodb for testing", e);
        }

        User user = new User();
        user.setEmail("user@foo.bar");
        user.setUsername(USER);
        user.setUserpass(authentications.getHashedPassword(USER));
        user.setRegistered(new Date());
        user.setExtraPoints(0);
        user.setTipPoints(0);
        user.setPoints(0);
        user.setActive(true);
        user.setAdmin(false);
        user.setReminder(true);
        user.setNotification(true);
        user.setSendGameTips(true);
        user.setSendStandings(true);
        user.setCorrectResults(0);
        user.setCorrectDifferences(0);
        user.setCorrectTrends(0);
        user.setCorrectExtraTips(0);
        user.setPicture(getInjector().getInstance(CommonService.class).getUserPictureUrl(Avatar.GRAVATAR, user));
        user.setAvatar(Avatar.GRAVATAR);
        dataService.save(user); 
        
        if (dataService.findSettings() == null) {
            getInjector().getInstance(ImportService.class).loadSettingsAndAdmin(); 
        }
    }

    public void doLogin(String username, String userpass) {
        Map<String, String> formParameters = new HashMap<String, String>();
        formParameters.put("username", username);
        formParameters.put("userpass", username);
        ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/auth/authenticate", new HashMap<String, String>(), formParameters);
    }
    
    public void doLogout() {
        ninjaTestBrowser.makeRequest(getServerAddress() + "/auth/logout");
    }

    public BasicCookieStore getCookies() {
        BasicCookieStore cookieStore = new BasicCookieStore();
        List<Cookie> cookies = ninjaTestBrowser.getCookies();
        for (Cookie cookie : cookies) {
            cookieStore.addCookie(cookie);
        }
        
        return cookieStore;
    }

    public HttpResponse getRequest(String url, boolean disableRedirects) throws IOException, ClientProtocolException {
        HttpClient httpclient = null;
        
        if (disableRedirects) {
            httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        } else {
            httpclient = HttpClientBuilder.create().build();
        }
        
        return httpclient.execute(new HttpGet(getServerAddress() + url));
    }

    public HttpResponse postRequest(String url, boolean disableRedirects) throws IOException, ClientProtocolException {
        HttpClient httpclient = null;
        
        if (disableRedirects) {
            httpclient = HttpClientBuilder.create().disableRedirectHandling().build();
        } else {
            httpclient = HttpClientBuilder.create().build();
        }
        
        return httpclient.execute(new HttpPost(getServerAddress() + url));
    }
}