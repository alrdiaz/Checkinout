package com.co.nexdevus.checkinout.basededatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDManejador extends SQLiteOpenHelper {

    //NOMBRE DE LA BASE DE DATOS EN EL DISPOSITIVO
    private static final String NOMBRE_BASE_DE_DATOS = "CheckInOut_V0.db";

    //VERSION DE LA BASE DE DATOS
    private static final int VERSION_BASE_DE_DATOS = 1;

    //CONSTRUCTOR
    public BDManejador(Context context) { super(context, NOMBRE_BASE_DE_DATOS, null, VERSION_BASE_DE_DATOS); }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ///region TABLA REGISTROS USUARIO
        String SQL_CREAR_TABLA_REGISTROS_USUARIO = "CREATE TABLE "  + BDContrato.TablaRegistrosUsuarios.NOMBRE_TABLA
                + " ("
                + BDContrato.TablaRegistrosUsuarios.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.NOMBRE + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.CEDULA + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.HORA + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.FECHA + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.TEMPERATURA + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.OBSERVACION + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.PROYECTO + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.TURNO_TRABAJO + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.SITIO_TRABAJO + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.TIPO + " TEXT, "
                + BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE + " INTEGER "
                + ");";
        sqLiteDatabase.execSQL(SQL_CREAR_TABLA_REGISTROS_USUARIO);
         ///endregion

        //region TABLA REGISTROS FIREBASE
        String SQL_CREAR_TABLA_REGISTROS_FIREBASE = "CREATE TABLE "  + BDContrato.TablaRegistrosFirebase.NOMBRE_TABLA
                + " ("
                + BDContrato.TablaRegistrosFirebase.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BDContrato.TablaRegistrosFirebase.ID_REMOTO + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.CORREO_USUARIO + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.NOMBRE + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.CEDULA + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.HORA + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.FECHA + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.TEMPERATURA + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.OBSERVACION + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.PROYECTO + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.TURNO_TRABAJO + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.SITIO_TRABAJO + " TEXT, "
                + BDContrato.TablaRegistrosFirebase.TIPO + " TEXT "
                + ");";
        sqLiteDatabase.execSQL(SQL_CREAR_TABLA_REGISTROS_FIREBASE);
        ///endregion
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //NADA QUE HACER DE MOMENTO
    }
}
