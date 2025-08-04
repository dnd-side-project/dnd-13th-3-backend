package org.minu.dnd13th3backend.user.repository;

import org.minu.dnd13th3backend.user.entity.OauthInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthInfoRepository extends JpaRepository<OauthInfo, Long> {
    
    Optional<OauthInfo> findByOauthIdAndProvider(String oauthId, String provider);
}