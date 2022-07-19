package com.nle.service.shippingline;

import com.nle.entity.ShippingLine;
import com.nle.exception.ResourceNotFoundException;
import com.nle.mapper.ShippingLineMapper;
import com.nle.repository.ShippingLineRepository;
import com.nle.service.dto.ShippingLineDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ShippingLineServiceImpl implements ShippingLineService {
    private final ShippingLineRepository shippingLineRepository;
    private final ShippingLineMapper shippingLineMapper;

    @Override
    public ShippingLineDTO findByCode(String code) {
        Optional<ShippingLine> shippingLineOptional = shippingLineRepository.findByCode(code);
        if (shippingLineOptional.isEmpty()) {
            throw new ResourceNotFoundException("ShippingLine with code: '" + code + "' doesn't exist");
        }
        return shippingLineMapper.toDto(shippingLineOptional.get());
    }

    @Override
    public List<ShippingLineDTO> findAll() {
        List<ShippingLine> shippingLineList = shippingLineRepository.findAll();
        return shippingLineList.stream().map(shippingLineMapper::toDto)
            .collect(Collectors.toList());
    }
}
