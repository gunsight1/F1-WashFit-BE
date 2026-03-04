package com.kernel360.admin.service;

import com.kernel360.washzone.dto.WashZoneDto;
import com.kernel360.washzone.repository.WashZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminWashZoneService {

    private final WashZoneRepository washZoneRepository;

    @Transactional(readOnly = true)
    public Page<WashZoneDto> getWashZones(Pageable pageable) {
        return washZoneRepository.findAll(pageable).map(WashZoneDto::from);
    }
}
