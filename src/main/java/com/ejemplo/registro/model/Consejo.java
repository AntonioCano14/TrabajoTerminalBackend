package com.ejemplo.registro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "consejos")
public class Consejo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremento
    @Column(name = "ID_consejos")
    private int idConsejo;

    @Column(name = "Descripcion_consejo")
    private String descripcionConsejo;

    @Column(name = "tipoConsejo")
    private String tipoConsejo;

    // Getters y Setters
    public int getIdConsejo() {
        return idConsejo;
    }

    public void setIdConsejo(int idConsejo) {
        this.idConsejo = idConsejo;
    }

    public String getDescripcionConsejo() {
        return descripcionConsejo;
    }

    public void setDescripcionConsejo(String descripcionConsejo) {
        this.descripcionConsejo = descripcionConsejo;
    }

    public String getTipoConsejo() {
        return tipoConsejo;
    }

    public void setTipoConsejo(String tipoConsejo) {
        this.tipoConsejo = tipoConsejo;
    }
}

