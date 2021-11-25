package com.co.nexdevus.checkinout.basededatos.objetos;

/***
 * Clase que el almacenamiento de datos provenientes de la base de datos local en forma de objeto
 * este objeto tiene la ventaja de poder reutilizarse y usarse como parametro en otras estructuras
 */
public class RegistroUsuario {

    ///region DECLARACION DE VARIABLES LOCALES
    private int id, sincronizacionFirebase;
    private String correoUsuario, nombre, cedula, hora, fecha,
            temperatura, observacion, proyecto, turnoTrabajo,
            sitioTrabajo, tipo, firmaEscaner;
    //endregion DECLARACION DE VARIABLES GLOBALES

    /***
     * Constructor
     */
    public RegistroUsuario(){ }

    ///region DECLARACION DE SETTERS
    public void setId(int id) { this.id = id; }
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
    public void setFirmaEscaner(String firmaEscaner) { this.firmaEscaner = firmaEscaner; }
    public void setSincronizacionFirebase(int sincronizacionFirebase) { this.sincronizacionFirebase = sincronizacionFirebase; }
    ///endregion DECLARACION DE SETTERS

    ///region DECLARACION DE GETTERS
    public int getId() { return this.id; }
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
    public String getFirmaEscaner() { return this.firmaEscaner; }
    public int getSincronizacionFirebase() { return this.sincronizacionFirebase; }
    ///endregion DECLARACION DE GETTERS
}
