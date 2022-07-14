package com.nle.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DepoOwnerAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DepoOwnerAccount.class);
        DepoOwnerAccount depoOwnerAccount1 = new DepoOwnerAccount();
        depoOwnerAccount1.setId(1L);
        DepoOwnerAccount depoOwnerAccount2 = new DepoOwnerAccount();
        depoOwnerAccount2.setId(depoOwnerAccount1.getId());
        assertThat(depoOwnerAccount1).isEqualTo(depoOwnerAccount2);
        depoOwnerAccount2.setId(2L);
        assertThat(depoOwnerAccount1).isNotEqualTo(depoOwnerAccount2);
        depoOwnerAccount1.setId(null);
        assertThat(depoOwnerAccount1).isNotEqualTo(depoOwnerAccount2);
    }
}
