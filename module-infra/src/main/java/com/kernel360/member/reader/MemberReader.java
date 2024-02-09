package com.kernel360.member.reader;

import com.kernel360.commoncode.entity.CommonCode;
import com.kernel360.member.entity.Member;

import java.util.List;

public interface MemberReader {

    Member findOneByIdAndPassword(String id, String password);

    Member findOneById(String id);

    Member findOneByEmail(String email);
}
