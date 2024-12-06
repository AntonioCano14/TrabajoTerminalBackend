package com.ejemplo.registro.repository;

import com.ejemplo.registro.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {
    AdminUser findByNombre(String nombre);
}
