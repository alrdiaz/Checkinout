package com.co.nexdevus.checkinout.datos;

/***
 * Clase publica que permite REUSAR las variables que tengan como objetivo identificar o servir de
 * guia para el proyecto, por ende es importante usarlo de modo tal que si se realiza un cambio se realiza
 * en un solo documento y los cambios se acarrean a los demas usos
 */
public class Constantes {

    public static final String CORREO_ADMINISTRADOR = "admin@admin.com";//variable que determina el correo de administrador
    public static final String TAG_DEBUG = "CIO";//variable para Log.d()
    public static final String USUARIO = "USUARIO"; //variable usada para Bundle
    public static final String REGISTROS = "REGISTROS"; //variable usada para Bundle
    public static final String VACIO = ""; //variable usada para validar los edittext

    public static final String SIN_SELECCION = "Seleccione"; //variable NO usada

    ///region  PRIMER VALOR SPINNERS
    public static final String PROYECTO = "Proyecto";
    public static final String TURNO_TRABAJO = "Turno Trabajo";
    public static final String SITIO_TRABAJO = "Sitio Trabajo";
    public static final String TIPO = "Tipo";
    ///endregion PRIMER VALOR SPINNERS

    /***
     * Constructor publico
     */
    public Constantes(){ }

}
