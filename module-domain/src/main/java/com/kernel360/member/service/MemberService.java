package com.kernel360.member.service;

import com.kernel360.member.command.MemberCommand;

public interface MemberService {
    void joinMember(MemberCommand joinRequestDto);

    MemberCommand login(MemberCommand loginDto);

    boolean idDuplicationCheck(String id);

    boolean emailDuplicationCheck(String email);

}
