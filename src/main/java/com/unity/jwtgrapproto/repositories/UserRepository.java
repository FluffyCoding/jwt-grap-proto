package com.unity.jwtgrapproto.repositories;

import com.unity.jwtgrapproto.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(@NonNull String username);

    Optional<User> findByEmail(@NotNull String email);

}
