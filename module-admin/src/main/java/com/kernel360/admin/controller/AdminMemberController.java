package com.kernel360.admin.controller;

import com.kernel360.admin.service.AdminMemberService;
import com.kernel360.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public String getAllMembers(Pageable pageable, Model model) {
        Page<MemberDto> members = adminMemberService.getMembers(pageable);
        model.addAttribute("members", members);
        return "admin/members";
    }
}
