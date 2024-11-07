package com.ejemplo.registro.repository;

import com.ejemplo.registro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByCorreoUser(String correoUser);
    @Query("SELECT u FROM User u WHERE u.correoUser = :correo AND u.RedSocial_ID_Social = :redSocialId")
    Optional<User> findByCorreoUserAndRedSocial_ID_Social(@Param("correo") String correoUser, @Param("redSocialId") int RedSocial_ID_Social);
}

