package com.kernel360.member.service;

import com.kernel360.Auth.AuthService;
import com.kernel360.auth.entity.Auth;
import com.kernel360.commoncode.service.CommonCodeService;
import com.kernel360.exception.BusinessException;
import com.kernel360.member.code.MemberErrorCode;
import com.kernel360.member.command.MemberCommand;
import com.kernel360.member.entity.Member;
import com.kernel360.member.enumSet.Age;
import com.kernel360.member.enumSet.Gender;
import com.kernel360.member.reader.MemberReader;
import com.kernel360.member.store.MemberStore;
import com.kernel360.utils.ConvertSHA256;
import com.kernel360.utils.JWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final JWT jwt;
    private final MemberStore memberStore;
    private final MemberReader memberReader;
    private final AuthService authService;
    private final CommonCodeService commonCodeService;


    @Transactional
    public void joinMember(MemberCommand requestDto) {

        Member entity = getNewJoinMemberEntity(requestDto);
        if (entity == null) {   throw new BusinessException(MemberErrorCode.FAILED_GENERATE_JOIN_MEMBER_INFO);    }
        memberStore.saveMember(entity);
    }

    protected Member getNewJoinMemberEntity(MemberCommand requestDto) {

        String encodePassword = ConvertSHA256.convertToSHA256(requestDto.password());
        int genderOrdinal;
        int ageOrdinal;

        try {
            genderOrdinal = Gender.valueOf(requestDto.gender()).ordinal();
            ageOrdinal = Age.valueOf(requestDto.age()).ordinal();
        } catch (Exception e) {
            throw new BusinessException(MemberErrorCode.FAILED_NOT_MAPPING_ENUM_VALUEOF);
        }

        return Member.createJoinMember(requestDto.id(), requestDto.email(), encodePassword, genderOrdinal, ageOrdinal);
    }

    @Transactional
    public MemberCommand login(MemberCommand loginDto) {

        Member loginEntity = getReqeustLoginEntity(loginDto);
        if (loginEntity == null) {  throw new BusinessException(MemberErrorCode.FAILED_GENERATE_LOGIN_REQUEST_INFO);    }

        Member memberEntity = memberReader.findOneByIdAndPassword(loginEntity.getId(), loginEntity.getPassword());
        if (memberEntity == null) { throw new BusinessException(MemberErrorCode.FAILED_REQUEST_LOGIN);  }

        String token = jwt.generateToken(memberEntity.getId());

        String encryptToken = ConvertSHA256.convertToSHA256(token);

        Auth authJwt = authService.findOneByMemberNo(memberEntity.getMemberNo());

        //결과 없으면 entity로 신규 생성
        authJwt = Optional.ofNullable(authJwt)
                .map(modifyAuth -> authService.modifyAuthJwt(modifyAuth, encryptToken))
                .orElseGet(() -> authService.createAuthJwt(memberEntity.getMemberNo(), encryptToken));

        authService.saveAuth(authJwt);

        return MemberCommand.login(memberEntity, token);
    }


    private Member getReqeustLoginEntity(MemberCommand loginDto) {
        String encodePassword = ConvertSHA256.convertToSHA256(loginDto.password());

        return Member.loginMember(loginDto.id(), encodePassword);
    }

    @Transactional(readOnly = true)
    public boolean idDuplicationCheck(String id) {
        Member member = memberReader.findOneById(id);

        return member != null;
    }

    @Transactional(readOnly = true)
    public boolean emailDuplicationCheck(String email) {
        Member member = memberReader.findOneByEmail(email);

        return member != null;
    }

//
//    public <T> MemberCommand findMemberByToken(RequestEntity<T> request) {
//        String token = request.getHeaders().getFirst("Authorization");
//        String id = JWT.ownerId(token);
//
//        return MemberCommand.from(memberReader.findOneById(id));
//    }
//
//    public <T> CarInfo findCarInfoByToken(RequestEntity<T> request) {
//        MemberCommand memberCommand = findMemberByToken(request);
//
//        return memberCommand.from().getCarInfo();
//    }
//
//    @Transactional
//    public void deleteMember(String id) {
//        Member member = memberReader.findOneById(id);
//
//        memberStore.deleteMember(member);
//    }
//
//
//    @Transactional
//    public void changePassword(MemberInfo memberinfo) {
//        Member member = memberReader.findOneById(memberinfo.id());
//
//        memberStore.saveMember(Member.of(member.getMemberNo(), member.getId(),
//                member.getEmail(), memberinfo.password(),
//                member.getGender(), member.getAge()
//        ));
//    }

    @Transactional
    public void updateMember(MemberCommand memberCommand) {
        Member member =
                Member.of(memberCommand.memberNo(), memberCommand.id(), memberCommand.email(), memberCommand.password(),
                        Integer.parseInt(memberCommand.gender()), Integer.parseInt(memberCommand.age()));

        memberStore.saveMember(member);
    }
//
//    @Transactional(readOnly = true)
//    public <T> Map<String, Object> getCarInfo(RequestEntity<T> request) {
//        //        CarInfo carInfo = memberService.findCarInfoByToken(request);
//        //FIXME :: CarInfo Data 없어서 에러발생 주석 처리해둠
//
//        return Map.of(
////                "car_info", carInfo,
//                "segment_options", commonCodeService.getCodes("segment"),
//                "carType_options", commonCodeService.getCodes("cartype"),
//                "color_options", commonCodeService.getCodes("color"),
//                "driving_options", commonCodeService.getCodes("driving"),
//                "parking_options", commonCodeService.getCodes("parking")
//        );
//    }
}
