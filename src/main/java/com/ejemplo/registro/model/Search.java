package com.ejemplo.registro.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial")
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Historial")
    private int idHistorial;

    @Column(name="busqueda", nullable = false)
    private String busqueda;

    @Column(name="url",nullable = false)
    private String url;

    @Column(name = "fecha_busqueda", nullable = false)
    private LocalDateTime fechaBusqueda;

    @Column(name = "User_ID_user", nullable = false)
    private int userId;

    // Getters y Setters
    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getFechaBusqueda() {
        return fechaBusqueda;
    }

    public void setFechaBusqueda(LocalDateTime fechaBusqueda) {
        this.fechaBusqueda = fechaBusqueda;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
