package com.seohamin.moonbangoo.domain.user.repository;

import com.seohamin.moonbangoo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
