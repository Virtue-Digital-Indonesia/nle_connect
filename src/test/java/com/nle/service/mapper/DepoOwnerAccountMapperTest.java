package com.nle.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepoOwnerAccountMapperTest {

    private DepoOwnerAccountMapper depoOwnerAccountMapper;

    @BeforeEach
    public void setUp() {
        depoOwnerAccountMapper = new DepoOwnerAccountMapperImpl();
    }
}
