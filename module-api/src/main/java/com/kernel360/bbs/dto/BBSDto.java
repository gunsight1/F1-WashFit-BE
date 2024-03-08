package com.kernel360.bbs.dto;

import com.kernel360.bbs.entity.BBS;
import com.kernel360.member.entity.Member;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.kernel360.bbs.entity.BBS}
 */
public record BBSDto(
        Long bbsNo,
        Long upperNo,
        String type,
        String title,
        String contents,
        Boolean isVisible,
        LocalDate createdAt,
        String createdBy,
        LocalDate modifiedAt,
        String modifiedBy,
        Long viewCount,
        Member member
    ) {
    public static BBSDto of(
            Long bbsNo,
            Long upperNo,
            String type,
            String title,
            String contents,
            Boolean isVisible,
            LocalDate createdAt,
            String createdBy,
            LocalDate modifiedAt,
            String modifiedBy,
            Long viewConut,
            Member member
    ){
        return new BBSDto(
            bbsNo,
            upperNo,
            type,
            title,
            contents,
            isVisible,
            createdAt,
            createdBy,
            modifiedAt,
            modifiedBy,
            viewConut,
            member
        );
    }

    public static BBSDto from(BBS entity){
        return new BBSDto(
                entity.getBbsNo(),
                entity.getUpperNo(),
                entity.getType(),
                entity.getTitle(),
                entity.getContents(),
                entity.getIsVisible(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy(),
                entity.getViewConut(),
                entity.getMember()
        );
    }
}