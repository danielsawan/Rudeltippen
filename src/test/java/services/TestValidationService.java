package services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import main.TestBase;

import org.junit.Test;

public class TestValidationService extends TestBase {

    @Test
    public void testIsValidEmail() {
        ValidationService validationService = getInjector().getInstance(ValidationService.class);

        assertTrue(validationService.isValidEmail("sk@svenkubiak.de"));
        assertTrue(validationService.isValidEmail("peter.pong@plong.com"));
        assertTrue(validationService.isValidEmail("han.solo.senior@sub.domain.com"));
        assertFalse(validationService.isValidEmail("sk"));
        assertFalse(validationService.isValidEmail("sk@"));
        assertFalse(validationService.isValidEmail("@"));
        assertFalse(validationService.isValidEmail("@com.de"));
        assertFalse(validationService.isValidEmail("sk@.de"));
    }

    @Test
    public void testUsernameAndEmail() {
        ValidationService validationService = getInjector().getInstance(ValidationService.class);

        assertTrue(validationService.isValidUsername("ahf_bA-SS747"));
        assertFalse(validationService.isValidUsername("ahf_bA-SS 747"));
        assertFalse(validationService.isValidUsername("ahf_bA-SS/747"));

        assertTrue(validationService.isValidEmail("user1@rudeltippen.de"));
        assertFalse(validationService.emailExists("foobar555@bar.com"));
    }

    @Test
    public void testValidScore() {
        ValidationService validationService = getInjector().getInstance(ValidationService.class);

        assertTrue(validationService.isValidScore("0", "0"));
        assertTrue(validationService.isValidScore("99", "99"));
        assertFalse(validationService.isValidScore("-1", "-1"));
        assertFalse(validationService.isValidScore("a", "b"));
        assertFalse(validationService.isValidScore("100", "1"));
        assertFalse(validationService.isValidScore("1", "100"));
        assertFalse(validationService.isValidScore("-1", "1"));
        assertFalse(validationService.isValidScore("1", "-51"));
    }
}