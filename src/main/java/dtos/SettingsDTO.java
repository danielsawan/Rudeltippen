package dtos;

/**
 * 
 * @author svenkubiak
 * 
 */
public class SettingsDTO {
    private String name;
    private int pointsTip;
    private int pointsTipDiff;
    private int pointsTipTrend;
    private int minutesBeforeTip;
    private boolean informOnNewTipper;
    private boolean enableRegistration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPointsTip() {
        return pointsTip;
    }

    public void setPointsTip(int pointsTip) {
        this.pointsTip = pointsTip;
    }

    public int getPointsTipDiff() {
        return pointsTipDiff;
    }

    public void setPointsTipDiff(int pointsTipDiff) {
        this.pointsTipDiff = pointsTipDiff;
    }

    public int getPointsTipTrend() {
        return pointsTipTrend;
    }

    public void setPointsTipTrend(int pointsTipTrend) {
        this.pointsTipTrend = pointsTipTrend;
    }

    public int getMinutesBeforeTip() {
        return minutesBeforeTip;
    }

    public void setMinutesBeforeTip(int minutesBeforeTip) {
        this.minutesBeforeTip = minutesBeforeTip;
    }

    public boolean isInformOnNewTipper() {
        return informOnNewTipper;
    }

    public void setInformOnNewTipper(boolean informOnNewTipper) {
        this.informOnNewTipper = informOnNewTipper;
    }

    public boolean isEnableRegistration() {
        return enableRegistration;
    }

    public void setEnableRegistration(boolean enableRegistration) {
        this.enableRegistration = enableRegistration;
    }
}