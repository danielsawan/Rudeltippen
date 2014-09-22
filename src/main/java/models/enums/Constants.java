package models.enums;

/**
 * 
 * @author svenkubiak
 *
 */
public enum Constants {
    APPNAME("rudeltippen"),
    DEFAULT_DATETIMEFORMAT("dd.MM.yyyy kk:mm"),
    DEFAULT_DATEFORMAT("dd.MM.yyyy"),
    DEFAULT_TIMEFORMAT("kk:mm"),
    DEFAULT_TIMEZONE("Europe/Berlin"),
    ENCODING("UTF-8"),
    WS_COTENT_TYPE("application/soap+xml"),
    WS_URL("http://www.openligadb.de/Webservices/Sportsdata.asmx"),
    CONFIRMATIONPATTERN("\\w{8,8}-\\w{4,4}-\\w{4,4}-\\w{4,4}-\\w{12,12}"),
    EMAILPATTERN(".+@.+\\.[a-z]+"),
    USERNAMEPATTERN("[a-zA-Z0-9-_]+"),
    OBJECTIDPATTERN("^[0-9a-z]{24}$"),
    USERNAME("username"),
    COOKIENAME("rudelremember"),
    GAMETIPJOB("GameTipJob"),
    KICKOFFJOB("KickoffJob"),
    REMINDERJOB("ReminderJob"),
    RESULTJOB("ResultJob"),
    CONNECTEDUSER("connectedUser"),
    FLASHWARNING("warning"),
    SETTINGS("settings");

    private String value;

    Constants (String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}