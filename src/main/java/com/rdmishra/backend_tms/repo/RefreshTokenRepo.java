package com.rdmishra.backend_tms.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rdmishra.backend_tms.model.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByJti(String jti);
}
