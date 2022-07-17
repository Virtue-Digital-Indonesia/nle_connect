package com.nle.service.gatemove;

import com.nle.controller.dto.GateMoveCreateDTO;
import com.nle.service.dto.GateMoveDTO;

public interface GateMoveService {
    GateMoveDTO createGateMove(GateMoveCreateDTO gateMoveCreateDTO);
}
