package com.kernel360.commoncode.reader;

import com.kernel360.commoncode.entity.CommonCode;
import com.kernel360.commoncode.mapper.CommonCodeMapper;
import com.kernel360.commoncode.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommonCodeReaderImpl implements CommonCodeReader {

    private final CommonCodeRepository commonCodeRepository;
    private final CommonCodeMapper commonCodeMapper;
    public List<CommonCode> getCodes(String codeName) {

        return commonCodeRepository.findAllByUpperNameAndIsUsed(codeName,true);
    }

    public List<CommonCode> getCodesWithMapper(String codeName) {

        return commonCodeMapper.findAllByUpperNameAndIsUsed(codeName,true);
    }
}
