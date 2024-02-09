package com.kernel360.commoncode.service;

import com.kernel360.commoncode.command.CommonCodeCommand;
import com.kernel360.commoncode.entity.CommonCode;
import com.kernel360.commoncode.reader.CommonCodeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService {
    private final CommonCodeReader commonCodeReader;

    public List<CommonCodeCommand> getCodes(String codeName) {

        List<CommonCode> codes = commonCodeReader.getCodes(codeName);

        return codes.stream().map(CommonCodeCommand::from).toList();
    }

    @Override
    public List<CommonCodeCommand> getCodesWithMapper(String codeName) {
        List<CommonCode> codes = commonCodeReader.getCodesWithMapper(codeName);

        return codes.stream().map(CommonCodeCommand::from).toList();
    }
}
