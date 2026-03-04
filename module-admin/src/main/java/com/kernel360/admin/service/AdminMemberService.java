package com.kernel360.admin.service;

import com.kernel360.member.dto.MemberDto;
import com.kernel360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<MemberDto> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::from);
    }
}
