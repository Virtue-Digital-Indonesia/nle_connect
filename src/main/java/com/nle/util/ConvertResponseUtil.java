package com.nle.util;

import com.nle.io.entity.DepoFleet;
import com.nle.ui.model.response.DepoFleetResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ConvertResponseUtil {
    public static DepoFleetResponse convertDepoFleetToResponse (DepoFleet depoFleet) {
        DepoFleetResponse depoFleetResponse = new DepoFleetResponse();
        BeanUtils.copyProperties(depoFleet.getFleet(), depoFleetResponse);
        depoFleetResponse.setDepo_fleet_id(depoFleet.getId());
        depoFleetResponse.setName(depoFleet.getName());
        return depoFleetResponse;
    }
}
