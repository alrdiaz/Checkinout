package com.co.nexdevus.checkinout.basededatos;

import android.net.Uri;
import android.provider.BaseColumns;

public class BDContrato {
    //constructor
    public BDContrato(){ }

    //dueño del contenido
    public static final String AUTOR_CONTENIDO = "com.co.nexdevus.checkinout";
    //URI (por si es necesario)
    public static final Uri URI_CONTENT = Uri.parse("content://"+AUTOR_CONTENIDO);
    //rutas para tabla
    public static final String RUTA_REGISTROS_USUARIO = "registros_usuarios";
    public static final String RUTA_REGISTROS_FIREBASE = "registros_firebase";

    public static final class TablaRegistrosUsuarios implements BaseColumns
    {
        ///region NOMBRE COLUMNAS
        public static final String NOMBRE_TABLA = "REGISTROS_USUARIOS";
        public static final String ID = "ID";
        public static final String CORREO_USUARIO = "CORREO_USUARIO";
        public static final String NOMBRE = "NOMBRE";
        public static final String CEDULA = "CEDULA";
        public static final String HORA = "HORA";
        public static final String FECHA = "FECHA";
        public static final String TEMPERATURA = "TEMPERATURA";
        public static final String OBSERVACION = "OBSERVACION";
        public static final String PROYECTO = "PROYECTO";
        public static final String TURNO_TRABAJO = "TURNO_TRABAJO";
        public static final String SITIO_TRABAJO = "SITIO_TRABAJO";
        public static final String TIPO = "TIPO";
        public static final String FIRMA_ESCANER = "FIRMA_ESCANER";
        public static final String SINCRONIA_FIREBASE = "SINCRONIA_FIREBASE";
        ///endregion

        ///region VALORES SINCRONIA
        public static final int DATO_NO_SINCRONIZADO = -1;
        public static final int DATO_SINCRONIZADO = 1;
        ///endregion
    }

    public static final class TablaRegistrosFirebase implements BaseColumns
    {
        ///region NOMBRE COLUMNAS
        public static final String NOMBRE_TABLA = "REGISTROS_FIREBASE";
        public static final String ID = "ID";
        public static final String ID_REMOTO = "ID_REMOTO";
        public static final String CORREO_USUARIO = "CORREO_USUARIO";
        public static final String NOMBRE = "NOMBRE";
        public static final String CEDULA = "CEDULA";
        public static final String HORA = "HORA";
        public static final String FECHA = "FECHA";
        public static final String TEMPERATURA = "TEMPERATURA";
        public static final String OBSERVACION = "OBSERVACION";
        public static final String PROYECTO = "PROYECTO";
        public static final String TURNO_TRABAJO = "TURNO_TRABAJO";
        public static final String SITIO_TRABAJO = "SITIO_TRABAJO";
        public static final String TIPO = "TIPO";
        ///endregion
    }
}
