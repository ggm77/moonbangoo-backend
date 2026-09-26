package com.seohamin.moonbangoo.domain.user.repository.oauth;

import com.seohamin.moonbangoo.domain.user.entity.oauth.UserOauth2Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauth2AccountsRepository extends JpaRepository<UserOauth2Accounts, Long> {
    Optional<UserOauth2Accounts> findByProviderAndProviderUserId(String provider, String providerUserId);
}
