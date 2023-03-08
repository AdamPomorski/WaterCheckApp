package com.example.watercheckapp.alarms;


public class Alarm {

    private AlarmTypes t;
    private String id;
    private String value;


    public Alarm(AlarmTypes t, String id, String value) {
        this.t = t;
        this.id = id;
        this.value = value;
    }

    public AlarmTypes getT() {
        return t;
    }

    public void setT(AlarmTypes t) {
        this.t = t;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
