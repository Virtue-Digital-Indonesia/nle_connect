package com.nle.web.rest.vm;


import java.io.Serializable;

public class ActiveDto implements Serializable {
    private String activeStatus;

    public ActiveDto() {
    }

    public ActiveDto(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }
}
