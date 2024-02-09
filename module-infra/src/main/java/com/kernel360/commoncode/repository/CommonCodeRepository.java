package com.kernel360.commoncode.repository;

import com.kernel360.commoncode.entity.CommonCode;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CommonCodeRepository extends JpaRepository<CommonCode, Id> {
    List<CommonCode> findAllByUpperNameAndIsUsed(String codeName, boolean isUsed);
}

