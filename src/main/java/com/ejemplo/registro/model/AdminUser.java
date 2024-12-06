package com.ejemplo.registro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_admin")
    private int id;

    @Column(name = "nombre_admin")
    private String nombre;

    @Column(name = "password_admin")
    private String password;

    // Constructor para crear un objeto AdminUser
    public AdminUser(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }

    // Constructor para la respuesta con ID y nombre
    public AdminUser(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public AdminUser() {

    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}