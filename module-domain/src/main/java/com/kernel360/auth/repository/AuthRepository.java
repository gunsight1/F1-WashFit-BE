package com.kernel360.auth.repository;

import com.kernel360.auth.entity.Auth;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository <Auth, Id> {

    Auth findOneByMemberNo(Long memberNo);

    Auth findOneByJwtToken(String jwtToken);

    @Query("SELECT a FROM Auth a JOIN Member m ON a.memberNo = m.memberNo WHERE m.id = :memberId")
    Optional<Auth> findOneByMemberId(@Param("memberId") String memberId);
}
