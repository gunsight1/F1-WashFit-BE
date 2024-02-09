package com.kernel360.member.reader;

import com.kernel360.member.entity.Member;
import com.kernel360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberReaderImpl implements MemberReader {

    private final MemberRepository memberRepository;

    @Override
    public Member findOneByIdAndPassword(String id, String password) {
        return memberRepository.findOneByIdAndPassword(id, password);
    }

    @Override
    public Member findOneById(String id) {
        return memberRepository.findOneById(id);
    }

    @Override
    public Member findOneByEmail(String email) {
        return memberRepository.findOneByEmail(email);
    }
}
