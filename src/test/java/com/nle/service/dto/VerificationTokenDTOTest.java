package com.nle.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VerificationTokenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VerificationTokenDTO.class);
        VerificationTokenDTO verificationTokenDTO1 = new VerificationTokenDTO();
        verificationTokenDTO1.setId(1L);
        VerificationTokenDTO verificationTokenDTO2 = new VerificationTokenDTO();
        assertThat(verificationTokenDTO1).isNotEqualTo(verificationTokenDTO2);
        verificationTokenDTO2.setId(verificationTokenDTO1.getId());
        assertThat(verificationTokenDTO1).isEqualTo(verificationTokenDTO2);
        verificationTokenDTO2.setId(2L);
        assertThat(verificationTokenDTO1).isNotEqualTo(verificationTokenDTO2);
        verificationTokenDTO1.setId(null);
        assertThat(verificationTokenDTO1).isNotEqualTo(verificationTokenDTO2);
    }
}
