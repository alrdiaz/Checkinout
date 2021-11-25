package com.co.nexdevus.checkinout.basededatos.objetos;

import java.io.Serializable;


/***
 * Clase que permite almacenar los valores obtenidos desde Firebase
 * esta clase es serializable para permitir transmitir sus parametros meddiante Bundle.
 */
public class RegistroFirebase implements Serializable {

    ///region DECLARACION DE VARIABLES
    private String id, correoUsuario, nombre, cedula, hora, fecha,
            temperatura, observacion, proyecto, turnoTrabajo,
            sitioTrabajo, tipo, firmaEscaner;
    ///endregion DECLARACION DE VARIABLES

    /***
     * Constructor
     */
    public RegistroFirebase(){ }

    ///region DECLARACION DE SETTERS
    public void setId(String id) { this.id = id; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setHora(String hora) { this.hora = hora; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setTemperatura(String temperatura) { this.temperatura = temperatura; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public void setProyecto(String proyecto) { this.proyecto = proyecto; }
    public void setTurnoTrabajo(String turnoTrabajo) { this.turnoTrabajo = turnoTrabajo; }
    public void setSitioTrabajo(String sitioTrabajo) { this.sitioTrabajo = sitioTrabajo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    ///endregion DECLARACION DE SETTERS

    ///region DECLARACION DE GETTERS
    public String getId() { return this.id; }
    public String getCorreoUsuario() { return this.correoUsuario; }
    public String getNombre() { return this.nombre; }
    public String getCedula() { return this.cedula; }
    public String getHora() { return this.hora; }
    public String getFecha() { return this.fecha; }
    public String getTemperatura() { return this.temperatura; }
    public String getObservacion() { return this.observacion; }
    public String getProyecto() { return this.proyecto; }
    public String getTurnoTrabajo() { return this.turnoTrabajo; }
    public String getSitioTrabajo() { return this.sitioTrabajo; }
    public String getTipo() { return this.tipo; }
    ///endregion DECLARACION DE GETTERS
}
