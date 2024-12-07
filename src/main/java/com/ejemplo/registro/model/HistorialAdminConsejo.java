package com.ejemplo.registro.model;

import jakarta.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "historial_admin_consejos")
public class HistorialAdminConsejo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_historial_consejo")
    private int idHistorialConsejo;

    @Column(name = "fecha_accion")
    private Date fechaAccion;

    @Column(name = "accion")
    private String accion;

    @Column(name = "hora")
    private Time hora;

    @Column(name = "admin_ID_admin")
    private int adminId;

    @Column(name = "Consejos_ID_consejos")
    private int consejoId;

    // Getters y Setters
    public int getIdHistorialConsejo() {
        return idHistorialConsejo;
    }

    public void setIdHistorialConsejo(int idHistorialConsejo) {
        this.idHistorialConsejo = idHistorialConsejo;
    }

    public Date getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(Date fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getConsejoId() {
        return consejoId;
    }

    public void setConsejoId(int consejoId) {
        this.consejoId = consejoId;
    }
}
