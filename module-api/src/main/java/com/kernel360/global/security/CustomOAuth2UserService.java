package com.kernel360.global.security;

import com.kernel360.member.entity.Member;
import com.kernel360.member.enumset.AccountType;
import com.kernel360.member.enumset.Age;
import com.kernel360.member.enumset.Gender;
import com.kernel360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 카카오의 경우 attributes 구조가 다름
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String id = String.valueOf(attributes.get("id")); // 카카오 ID

        Member member = saveOrUpdate(id, email, registrationId);

        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        customUserDetails.setAttributes(attributes);

        return customUserDetails;
    }

    private Member saveOrUpdate(String id, String email, String registrationId) {
        Member member = memberRepository.findOneById(id);
        if (member == null) {
            member = Member.createJoinMember(id, email, registrationId, Gender.OTHERS.ordinal(),
                    Age.AGE_99.ordinal(), AccountType.KAKAO.name());
            memberRepository.save(member);
        }
        return member;
    }
}
