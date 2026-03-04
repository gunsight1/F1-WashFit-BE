package com.kernel360.member.service;

import com.kernel360.auth.service.AuthService;
import com.kernel360.carinfo.entity.CarInfo;
import com.kernel360.carinfo.repository.CarInfoRepository;
import com.kernel360.commoncode.service.CommonCodeService;
import com.kernel360.exception.BusinessException;
import com.kernel360.global.jwt.JwtTokenProvider;
import com.kernel360.global.security.CustomUserDetails;
import com.kernel360.member.code.MemberErrorCode;
import com.kernel360.member.dto.*;
import com.kernel360.member.entity.Member;
import com.kernel360.member.entity.WithdrawMember;
import com.kernel360.member.enumset.AccountType;
import com.kernel360.member.enumset.Age;
import com.kernel360.member.enumset.Gender;
import com.kernel360.member.repository.MemberRepository;
import com.kernel360.member.repository.WithdrawMemberRepository;
import com.kernel360.washinfo.entity.WashInfo;
import com.kernel360.washinfo.repository.WashInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final CommonCodeService commonCodeService;
    private final CarInfoRepository carInfoRepository;
    private final WashInfoRepository washInfoRepository;
    private final WithdrawMemberRepository withdrawMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void joinMember(MemberDto requestDto) {
        Member entity = getNewJoinMemberEntity(requestDto);
        if (entity == null) {   throw new BusinessException(MemberErrorCode.FAILED_GENERATE_JOIN_MEMBER_INFO);  }
        // TODO :: ControllerAdvice 추가 고민해보기
        if(memberRepository.findOneById(entity.getId()) != null){ throw new BusinessException(MemberErrorCode.FAILED_DUPLICATED_JOIN_MEMBER_INFO);}
        memberRepository.save(entity);
    }

    protected Member getNewJoinMemberEntity(MemberDto requestDto) {
        String encodePassword = passwordEncoder.encode(requestDto.password());
        int genderOrdinal;
        int ageOrdinal;

        try {
            genderOrdinal = Gender.valueOf(requestDto.gender()).ordinal();
            ageOrdinal = Age.valueOf(requestDto.age()).ordinal();
        } catch (Exception e) {
            throw new BusinessException(MemberErrorCode.FAILED_NOT_MAPPING_ENUM_VALUE_OF);
        }

        return Member.createJoinMember(requestDto.id(), requestDto.email(), encodePassword, genderOrdinal, ageOrdinal, AccountType.PLATFORM.name());
    }

    @Transactional
    public MemberDto login(MemberDto loginDto, HttpServletRequest request) {
        // AuthenticationManager를 사용하여 인증 수행
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.id(), loginDto.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Member memberEntity = ((CustomUserDetails) authentication.getPrincipal()).getMember();

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // Refresh Token 저장
        authService.saveRefreshToken(memberEntity, refreshToken, request);

        return MemberDto.login(memberEntity, accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public boolean idDuplicationCheck(String id) {
        Member member = memberRepository.findOneByIdForAccountTypeByPlatform(id);

        return member != null;
    }

    @Transactional(readOnly = true)
    public boolean emailDuplicationCheck(String email) {
        Member member = memberRepository.findOneByEmailForAccountTypeByPlatform(email);

        return Objects.nonNull(member);
    }

    public MemberDto findMemberByToken(String token) {
        String id = jwtTokenProvider.getSubject(token);

        return MemberDto.from(memberRepository.findOneById(id));
    }

    public CarInfo findCarInfoByToken(@RequestHeader("Authorization") String authToken) {
        MemberDto memberDto = findMemberByToken(authToken);

        return memberDto.toEntity().getCarInfo();
    }

    @Transactional
    public void deleteMember(String id) {
        Member member = memberRepository.findOneById(id);

        memberRepository.delete(member);
    }

    @Transactional
    public void deleteMemberByToken(String accessToken) {
        Member member = memberRepository.findOneById(jwtTokenProvider.getSubject(accessToken));
        withdrawMemberRepository.save(WithdrawMember.of(member)); //of 받는식을 변경했습니다. 이 방식으로 리팩터를 하면 코드가 깔끔하네요.
        memberRepository.delete(member);
        log.info("{} 회원 탈퇴 처리 완료", accessToken);
    }

    @Transactional
    public void changePassword(String password, String token) {
        String id = jwtTokenProvider.getSubject(token);
        Member member = memberRepository.findOneByIdForAccountTypeByPlatform(id);

        if (passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessException(MemberErrorCode.WRONG_PASSWORD_REQUEST);
        }

        member.updatePassword(passwordEncoder.encode(password));
        log.info("{} 회원의 비밀번호가 변경되었습니다.", id);
    }

    @Transactional
    public void updateMember(MemberDto updateMember, String token) {
        String id = jwtTokenProvider.getSubject(token);
        Member existingMember = memberRepository.findOneById(id);
        existingMember.updateFromInfo( Gender.valueOf(updateMember.gender()).ordinal(), Age.valueOf(updateMember.age()).ordinal());

        memberRepository.save(existingMember);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCarInfo(String token) {
        String id = jwtTokenProvider.getSubject(token);
        Member member = memberRepository.findOneById(id);
        if(member.getCarInfo() == null){
            throw new BusinessException(MemberErrorCode.FAILED_FIND_MEMBER_CAR_INFO);
        }
        CarInfoDto carInfoDto = CarInfoDto.from(member.getCarInfo());

        return Map.of(
                "car_info", carInfoDto,
                "segment_options", commonCodeService.getCodes("segment"),
                "carType_options", commonCodeService.getCodes("cartype"),
                "color_options", commonCodeService.getCodes("color"),
                "driving_options", commonCodeService.getCodes("driving"),
                "parking_options", commonCodeService.getCodes("parking")
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getWashInfo(String token) {
        String id = jwtTokenProvider.getSubject(token);
        Member member = memberRepository.findOneById(id);
        if (member == null) {
            throw new BusinessException(MemberErrorCode.FAILED_FIND_MEMBER_INFO);
        }
        WashInfo washInfo = member.getWashInfo();
        if(washInfo == null){
            throw new BusinessException(MemberErrorCode.FAILED_FIND_MEMBER_WASH_INFO);
        }

        WashInfoDto washInfoDto = WashInfoDto.from(washInfo);
        return Map.of(
                "wash_info", washInfoDto,
                "frequency_options", commonCodeService.getCodes("frequency"),
                "cost_options", commonCodeService.getCodes("cost"),
                "interest_options", commonCodeService.getCodes("interest")
        );
    }

    @Transactional
    public void saveWashInfo(WashInfoDto washInfoDto, String token) {
        String id = jwtTokenProvider.getSubject(token);
        Member member = memberRepository.findOneById(id);
        WashInfo washInfo = washInfoRepository.findWashInfoByMember(member);

        if (washInfo == null) { // 세차 정보가 없는 경우
            WashInfo newWashInfo = WashInfo.of( washInfoDto.washCount(), washInfoDto.monthlyExpense(), washInfoDto.interest());
            newWashInfo.settingMember(member); // FIXME :: newWashInfo.settingMember(member); 가 아니라 생성자를 호출할 때 Member 를 넣어줘도 될 거 같습니다.
            washInfoRepository.save(newWashInfo);
            return;
        }
        // 세차 정보가 있는 경우
        washInfo.updateWashInfo( washInfoDto.washCount(), washInfoDto.monthlyExpense(), washInfoDto.interest());
    }

    @Transactional
    public void saveCarInfo(CarInfoDto carInfoDto, String token) {
        String id = jwtTokenProvider.getSubject(token);
        Member member = memberRepository.findOneById(id);
        CarInfo carInfo = carInfoRepository.findCarInfoByMember(member);

        if (carInfo == null) { // 차량 정보가 없는 경우
            CarInfo newCarInfo = CarInfo.of(carInfoDto.carType(), carInfoDto.carSize(), carInfoDto.carColor(),
                    carInfoDto.drivingEnv(), carInfoDto.parkingEnv());
            newCarInfo.settingMember(member);// FIXME :: newCarInfo.settingMember(member); 가 아니라 생성자를 호출할 때 Member 를 넣어줘도 될 거 같습니다.
            carInfoRepository.save(newCarInfo);
            return;
        }
        // 세차 정보가 있는 경우
        carInfo.updateCarInfo(carInfoDto.carType(), carInfoDto.carSize(), carInfoDto.carColor(),
                carInfoDto.drivingEnv(), carInfoDto.parkingEnv());
    }

    @Transactional(readOnly = true)
    public MemberDto findByEmail(String email) {
        Member member = memberRepository.findOneByEmail(email);
        if (member == null) {
            throw new BusinessException(MemberErrorCode.FAILED_FIND_MEMBER_INFO);
        }

        return MemberDto.from(member);
    }

    @Transactional(readOnly = true)
    public MemberDto findOneByIdForAccountTypeByPlatform(String memberId) {
        Member member = memberRepository.findOneByIdForAccountTypeByPlatform(memberId);
        if (member == null) {
            throw new BusinessException(MemberErrorCode.FAILED_FIND_MEMBER_INFO);
        }

        return MemberDto.from(member);
    }

    @Transactional
    public void resetPasswordByMemberId(String memberId, String newPassword) {
        Member member = memberRepository.findOneByIdForAccountTypeByPlatform(memberId);
        if (member == null) {
            throw new BusinessException(MemberErrorCode.FAILED_FIND_MEMBER_INFO);
        }

        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional(readOnly = true)
    public boolean validatePassword(String password, String token) {
        String id = jwtTokenProvider.getSubject(token);
        Member member = memberRepository.findOneById(id);

        return passwordEncoder.matches(password, member.getPassword());
    }

    public MemberDto findByMemberId(String id) {
        Member member = memberRepository.findOneById(id);
        return MemberDto.from(member);
    }
}
