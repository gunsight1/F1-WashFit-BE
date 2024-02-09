package com.kernel360.member.enumSet;

import com.kernel360.exception.BusinessException;
import com.kernel360.member.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {
    man(0),
    woman(1);

    private final int value;
    public static String ordinalToName(int value) {
        for (Gender gender : values()) {
            if (gender.ordinal() == value) {
                return gender.name();
            }
        }
        throw new BusinessException(MemberErrorCode.FAILED_NOT_MAPPING_ORDINAL_TO_NAME);
    }
}
