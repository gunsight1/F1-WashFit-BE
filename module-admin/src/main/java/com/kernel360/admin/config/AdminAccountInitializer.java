package com.kernel360.admin.config;

import com.kernel360.member.entity.Member;
import com.kernel360.member.enumset.AccountType;
import com.kernel360.member.enumset.Age;
import com.kernel360.member.enumset.Gender;
import com.kernel360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminId = "washfit_admin";
        if (memberRepository.findOneById(adminId) == null) {
            Member admin = Member.createJoinMember(
                    adminId,
                    "admin@washfit.com",
                    passwordEncoder.encode("1q2w3e4r!@"),
                    Gender.MALE.ordinal(),
                    Age.AGE_30.ordinal(),
                    AccountType.PLATFORM.name()
            );
            memberRepository.save(admin);
        }
    }
}
