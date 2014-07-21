package models.enums;

public enum Avatar {
    GRAVATAR("https://avatars.io/email/"),
    TWITTER("https://avatars.io/twitter/"),
    FACEBOOK("https://avatars.io/facebook/"),
    INSTAGRAM("https://avatars.io/instagram/");

    private String value;

    Avatar (String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}