package com.kernel360.bbs.code;

import com.kernel360.code.BusinessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BBSBusinessCode implements BusinessCode {

    SUCCESS_REQUEST_GET_BBS(HttpStatus.OK.value(), "BBC001", "게시판 목록 조회 성공"),
    SUCCESS_REQUEST_CREATED_BBS(HttpStatus.CREATED.value(), "BBC002", "게시글 작성 성공"),
    SUCCESS_REQUEST_MODIFIED_BBS(HttpStatus.OK.value(), "BBC003", "게시글 수정 성공"),
    SUCCESS_REQUEST_DELETE_BBS(HttpStatus.OK.value(), "BBC004", "게시글 삭제 성공");

    private final int status;
    private final String code;
    private final String message;

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
