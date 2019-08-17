package com.example.atrue.bluelocking_final;

/**
 * Created by Yun on 2016-12-03.
 */

public class TableBowl {

    private String deviceKey;
    private String pcAddress;
    private String act;
    private String time;

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public void setPcAddress(String pcAddress) {
        this.pcAddress = pcAddress;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPcAddress() {

        return pcAddress;
    }

    public String getAct() {
        return act;
    }

    public String getTime() {
        return time;
    }

    public String getDeviceKey() {

        return deviceKey;
    }
}
