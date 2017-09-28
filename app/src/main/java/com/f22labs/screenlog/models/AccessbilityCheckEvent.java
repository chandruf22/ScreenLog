package com.f22labs.screenlog.models;

/**
 * Created by Chandru on 28/09/17.
 */

public class AccessbilityCheckEvent {


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;


    public AccessbilityCheckEvent(int status) {
        this.status = status;
    }

    public AccessbilityCheckEvent() {
    }

}

