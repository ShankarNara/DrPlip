package com.example.drplip;

import com.google.cloud.Timestamp;

public class Convo {
    Timestamp time;
    String uid,message,response;

    public Convo(Timestamp time, String uid, String message, String response) {
        this.time = time;
        this.uid = uid;
        this.message = message;
        this.response = response;
    }

    public Convo() {
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
