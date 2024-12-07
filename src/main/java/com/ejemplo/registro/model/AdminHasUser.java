package com.ejemplo.registro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_has_user")
public class AdminHasUser {

    @EmbeddedId
    private AdminHasUserId id;

    @Column(name = "accion")
    private String accion;

    // Getters y setters
    public AdminHasUserId getId() {
        return id;
    }

    public void setId(AdminHasUserId id) {
        this.id = id;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}
