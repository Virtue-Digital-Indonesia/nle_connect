package com.nle.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DepoOwnerAccountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DepoOwnerAccountDTO.class);
        DepoOwnerAccountDTO depoOwnerAccountDTO1 = new DepoOwnerAccountDTO();
        depoOwnerAccountDTO1.setId(1L);
        DepoOwnerAccountDTO depoOwnerAccountDTO2 = new DepoOwnerAccountDTO();
        assertThat(depoOwnerAccountDTO1).isNotEqualTo(depoOwnerAccountDTO2);
        depoOwnerAccountDTO2.setId(depoOwnerAccountDTO1.getId());
        assertThat(depoOwnerAccountDTO1).isEqualTo(depoOwnerAccountDTO2);
        depoOwnerAccountDTO2.setId(2L);
        assertThat(depoOwnerAccountDTO1).isNotEqualTo(depoOwnerAccountDTO2);
        depoOwnerAccountDTO1.setId(null);
        assertThat(depoOwnerAccountDTO1).isNotEqualTo(depoOwnerAccountDTO2);
    }
}
