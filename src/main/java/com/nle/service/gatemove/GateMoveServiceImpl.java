package com.nle.service.gatemove;


import com.nle.controller.dto.GateMoveCreateDTO;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.entity.GateMove;
import com.nle.mapper.GateMoveMapper;
import com.nle.repository.GateMoveRepository;
import com.nle.service.dto.GateMoveDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Transactional
public class GateMoveServiceImpl implements GateMoveService {
    private final GateMoveRepository gateMoveRepository;
    private final GateMoveMapper gateMoveMapper;

    @Override
    public GateMoveDTO createGateMove(GateMoveCreateDTO gateMoveCreateDTO) {
        GateMoveDTO gateMoveDTO = new GateMoveDTO();
        BeanUtils.copyProperties(gateMoveCreateDTO, gateMoveDTO);
        GateMove gateMove = gateMoveMapper.toEntity(gateMoveDTO);
        gateMove = gateMoveRepository.save(gateMove);
        return gateMoveMapper.toDto(gateMove);
    }

    @Override
    public PagingResponseModel<GateMoveDTO> findAll(Pageable pageable) {
        Page<GateMove> gateMoves = gateMoveRepository.findAll(pageable);
        return new PagingResponseModel<>(gateMoves.map(this::convertToDto));
    }

    private GateMoveDTO convertToDto(GateMove gateMove) {
        GateMoveDTO gateMoveDTO = new GateMoveDTO();
        BeanUtils.copyProperties(gateMove, gateMoveDTO);
        if (gateMove.getDateManufactured() != null) {
            String formattedDate = gateMove.getDateManufactured().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            gateMoveDTO.setDateManufactured(formattedDate);
        }
        return gateMoveDTO;
    }
}
