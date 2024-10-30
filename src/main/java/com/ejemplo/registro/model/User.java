package com.ejemplo.registro.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_user")
    private int ID_user;

    @Column(name = "nombre_user")
    private String nombre_user;

    @Column(name = "password_user", nullable = true) // Permitir nulo
    private String password_user;

    @Column(name = "`correo_user`")
    private String correoUser;

    @Column(name = "fecha_registro")
    private Date fecha_registro;

    @Column(name = "Ultima_sesion")
    private Date Ultima_sesion;

    @Column(name = "`RedSocial_ID_Social`")
    private int RedSocial_ID_Social;

    // Getters y setters
    public int getID_user() {
        return ID_user;
    }

    public void setID_user(int ID_user) {
        this.ID_user = ID_user;
    }

    public String getNombre_user() {
        return nombre_user;
    }

    public void setNombre_user(String nombre_user) {
        this.nombre_user = nombre_user;
    }

    public String getPassword_user() {
        return password_user;
    }

    public void setPassword_user(String password_user) {
        this.password_user = password_user;
    }

    public String getCorreo_user() {
        return correoUser;
    }

    public void setCorreo_user(String correo_user) {
        this.correoUser = correo_user;
    }

    public Date getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public int getRedSocial_ID_Social() {
        return RedSocial_ID_Social;
    }

    public void setRedSocial_ID_Social(int redSocial_ID_Social) {
        this.RedSocial_ID_Social = redSocial_ID_Social;
    }
}
