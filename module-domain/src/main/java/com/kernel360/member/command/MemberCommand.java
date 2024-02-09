package com.kernel360.member.command;

import com.kernel360.member.entity.Member;
import com.kernel360.member.enumSet.Age;
import com.kernel360.member.enumSet.Gender;
import com.kernel360.member.service.MemberServiceImpl;

import java.time.LocalDate;

/**
 * Command for {@link com.kernel360.member.entity.Member}
 */
public record MemberCommand(Long memberNo,
                        String id,
                        String email,
                        String password,
                        String gender,
                        String age,
                        LocalDate createdAt,
                        String createdBy,
                        LocalDate modifiedAt,
                        String modifiedBy,
                        String jwtToken
) {

    public static MemberCommand of(
            Long memberNo,
            String id,
            String email,
            String password,
            String gender,
            String age,
            LocalDate createdAt,
            String createdBy,
            LocalDate modifiedAt,
            String modifiedBy,
            String jwtToken
    ) {
        return new MemberCommand(
                memberNo,
                id,
                email,
                password,
                gender,
                age,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy,
                jwtToken
        );
    }

    public static MemberCommand from(Member entity) {
        return MemberCommand.of(
                entity.getMemberNo(),
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                Gender.ordinalToName(entity.getGender()),
                Age.ordinalToValue(entity.getAge()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy(),
                null
        );
    }

    public Member toDto() {
        return Member.of(
                this.memberNo(),
                this.id(),
                this.email(),
                this.password(),
                Gender.valueOf(this.gender()).ordinal(),
                Age.valueOf(this.age()).ordinal()
        );
    }

    /** joinMember **/
    public static MemberCommand of(
            String id,
            String email,
            String password,
            String gender,
            String   age
    ){
        return new MemberCommand(
                null,
                id,
                email,
                password,
                gender,
                age,
                null,
                null,
                null,
                null,
                null
        );
    }

    /** Login Binding **/
    public static MemberCommand login(Member entity, String jwtToken) {
        return MemberCommand.of(
                entity.getMemberNo(),
                entity.getId(),
                entity.getEmail(),
                null,
                Gender.ordinalToName(entity.getGender()),
                Age.ordinalToValue(entity.getAge()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy(),
                jwtToken
        );
    }

    /** Request Login **/
    public static MemberCommand of(
            String id,
            String password
    ){
        return new MemberCommand(
                null,
                id,
                null,
                password,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}