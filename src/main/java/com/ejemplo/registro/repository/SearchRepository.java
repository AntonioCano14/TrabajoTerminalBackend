package com.ejemplo.registro.repository;

import com.ejemplo.registro.model.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Integer> {
    Page<Search> findByUserId(int userId, Pageable pageable);
}
