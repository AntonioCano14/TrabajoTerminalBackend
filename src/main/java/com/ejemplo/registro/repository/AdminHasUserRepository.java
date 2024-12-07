package com.ejemplo.registro.repository;

import com.ejemplo.registro.model.AdminHasUser;
import com.ejemplo.registro.model.AdminHasUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminHasUserRepository extends JpaRepository<AdminHasUser, AdminHasUserId> {
}


