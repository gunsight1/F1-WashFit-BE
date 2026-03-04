package com.kernel360.admin.security;

import com.kernel360.member.entity.Member;
import com.kernel360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findOneById(username);
        if (member == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // TODO: 권한 관리 로직 추가 필요 (현재는 ROLE_ADMIN으로 고정)
        return new User(member.getId(), member.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
