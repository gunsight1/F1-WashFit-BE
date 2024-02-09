package com.kernel360.commoncode.reader;

import com.kernel360.commoncode.entity.CommonCode;

import java.util.List;

public interface CommonCodeReader {
    List<CommonCode> getCodes(String codeName);

    List<CommonCode> getCodesWithMapper(String codeName);
}
