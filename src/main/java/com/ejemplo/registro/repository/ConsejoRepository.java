package com.ejemplo.registro.repository;

import com.ejemplo.registro.model.Consejo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsejoRepository extends JpaRepository<Consejo, Integer> {
    // Puedes agregar métodos de consulta personalizados aquí si los necesitas
}
