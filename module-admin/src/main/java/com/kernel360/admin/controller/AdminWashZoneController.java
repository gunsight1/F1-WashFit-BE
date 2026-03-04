package com.kernel360.admin.controller;

import com.kernel360.admin.service.AdminWashZoneService;
import com.kernel360.washzone.dto.WashZoneDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/washzones")
public class AdminWashZoneController {

    private final AdminWashZoneService adminWashZoneService;

    @GetMapping
    public String getAllWashZones(Pageable pageable, Model model) {
        Page<WashZoneDto> washZones = adminWashZoneService.getWashZones(pageable);
        model.addAttribute("washZones", washZones);
        return "admin/washzones";
    }
}
