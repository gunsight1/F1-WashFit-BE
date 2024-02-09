package com.kernel360.member.store;

import com.kernel360.member.entity.Member;
import com.kernel360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberStoreImpl implements MemberStore {

    private final MemberRepository memberRepository;
    @Override
    public void joinMember(Member entity) {
        memberRepository.save(entity);
    }
}
