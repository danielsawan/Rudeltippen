package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import main.TestBase;
import ninja.Result;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

public class TestAuthController extends TestBase {
    
    @Test
    public void testPassword() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testLogin() throws ClientProtocolException, IOException {
        HttpResponse response = getRequest("/auth/login", true);

        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
    }
    
    @Test
    public void testReset() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testConfirm() throws ClientProtocolException, IOException {
        //TODO Implement me
    }
    
    @Test
    public void testRegister() throws ClientProtocolException, IOException {
        HttpResponse response = getRequest("/auth/login", true);

        assertNotNull(response);
        assertEquals(Result.SC_200_OK, response.getStatusLine().getStatusCode());
    }
}