package com.nle.controller.dto;


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
