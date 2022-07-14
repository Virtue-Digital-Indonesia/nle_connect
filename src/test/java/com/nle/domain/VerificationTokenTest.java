package com.nle.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VerificationTokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VerificationToken.class);
        VerificationToken verificationToken1 = new VerificationToken();
        verificationToken1.setId(1L);
        VerificationToken verificationToken2 = new VerificationToken();
        verificationToken2.setId(verificationToken1.getId());
        assertThat(verificationToken1).isEqualTo(verificationToken2);
        verificationToken2.setId(2L);
        assertThat(verificationToken1).isNotEqualTo(verificationToken2);
        verificationToken1.setId(null);
        assertThat(verificationToken1).isNotEqualTo(verificationToken2);
    }
}
