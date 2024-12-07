package com.ejemplo.registro.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Embeddable
public class AdminHasUserId implements Serializable {

    private int admin_ID_admin;
    private int user_ID_user;
    private Date fecha_accion;
    private Time hora;

    // Getters y setters
    public int getAdmin_ID_admin() {
        return admin_ID_admin;
    }

    public void setAdmin_ID_admin(int admin_ID_admin) {
        this.admin_ID_admin = admin_ID_admin;
    }

    public int getUser_ID_user() {
        return user_ID_user;
    }

    public void setUser_ID_user(int user_ID_user) {
        this.user_ID_user = user_ID_user;
    }

    public Date getFecha_accion() {
        return fecha_accion;
    }

    public void setFecha_accion(Date fecha_accion) {
        this.fecha_accion = fecha_accion;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    // Equals y hashCode (necesarios para que JPA funcione correctamente)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminHasUserId that = (AdminHasUserId) o;
        return admin_ID_admin == that.admin_ID_admin &&
                user_ID_user == that.user_ID_user &&
                Objects.equals(fecha_accion, that.fecha_accion) &&
                Objects.equals(hora, that.hora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admin_ID_admin, user_ID_user, fecha_accion, hora);
    }
}
