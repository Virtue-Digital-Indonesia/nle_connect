package com.nle.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VerificationTokenMapperTest {

    private VerificationTokenMapper verificationTokenMapper;

    @BeforeEach
    public void setUp() {
        verificationTokenMapper = new VerificationTokenMapperImpl();
    }
}
