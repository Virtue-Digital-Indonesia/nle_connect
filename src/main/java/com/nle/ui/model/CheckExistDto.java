package com.nle.ui.model;


import java.io.Serializable;

public class CheckExistDto implements Serializable {
    private Boolean exist;

    public CheckExistDto() {
    }

    public CheckExistDto(Boolean exist) {
        this.exist = exist;
    }

    public Boolean getExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }
}
