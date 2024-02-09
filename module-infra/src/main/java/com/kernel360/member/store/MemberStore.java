package com.kernel360.member.store;

import com.kernel360.member.entity.Member;

public interface MemberStore {

    void deleteMember(Member member);

    void saveMember(Member entity);
}
