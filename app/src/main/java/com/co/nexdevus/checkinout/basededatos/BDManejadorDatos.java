package com.co.nexdevus.checkinout.basededatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.co.nexdevus.checkinout.basededatos.objetos.RegistroUsuario;

import java.util.ArrayList;

public class BDManejadorDatos {
    Context mContext;
    private SQLiteDatabase mBaseDeDatos_escritura, mBaseDeDatos_lectura;
    private BDManejador mBDManejador;

    //Constructor para la clase
    public BDManejadorDatos(Context context)
    {
        this.mContext = context;
        this.mBDManejador = new BDManejador(context);
        mBaseDeDatos_escritura = mBDManejador.getWritableDatabase();
        mBaseDeDatos_lectura = mBDManejador.getReadableDatabase();
    }

    /***
     * Procedimiento que Inserta un nuevo registro en la base de datos
     * @param nombre nombre del sujeto escaneado
     * @param cedula cedula del sujeto escaneado
     * @param hora hora del evento
     * @param fecha fecha del evento
     * @param temperatura temperatura del sujeto
     * @param observacion observacion del operador
     * @param proyecto proyecto del operador
     * @param sitioTrabajo sitio del operador
     * @param turnoTrabajo turno del operador
     * @param tipo tipo de operador
     * @return devuelve la cantidad de filas insertadas debe ser mayor a cero (0) si realmente se inserto el dato
     */
    public int NuevoRegistroUsuario
            (String correo, String nombre, String cedula, String hora,
             String fecha, String temperatura, String observacion,
             String proyecto, String turnoTrabajo, String sitioTrabajo,
             String tipo)
    {
        ContentValues valores = new ContentValues();
        valores.put(BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO, correo);
        valores.put(BDContrato.TablaRegistrosUsuarios.NOMBRE, nombre);
        valores.put(BDContrato.TablaRegistrosUsuarios.CEDULA, cedula);
        valores.put(BDContrato.TablaRegistrosUsuarios.HORA, hora);
        valores.put(BDContrato.TablaRegistrosUsuarios.FECHA, fecha);
        valores.put(BDContrato.TablaRegistrosUsuarios.TEMPERATURA, temperatura);
        valores.put(BDContrato.TablaRegistrosUsuarios.OBSERVACION, observacion);
        valores.put(BDContrato.TablaRegistrosUsuarios.PROYECTO, proyecto);
        valores.put(BDContrato.TablaRegistrosUsuarios.TURNO_TRABAJO, turnoTrabajo);
        valores.put(BDContrato.TablaRegistrosUsuarios.SITIO_TRABAJO, sitioTrabajo);
        valores.put(BDContrato.TablaRegistrosUsuarios.TIPO, tipo);
        valores.put(BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE, BDContrato.TablaRegistrosUsuarios.DATO_NO_SINCRONIZADO);

        return (int) mBaseDeDatos_escritura.insert(BDContrato.TablaRegistrosUsuarios.NOMBRE_TABLA, null, valores);
    }

    public RegistroUsuario RegistroDesincronizadoUsuario(String correo)
    {
        RegistroUsuario mRegistroUsuario = new RegistroUsuario();
        String whereClause;
        String[] whereArgs, projection = {
                BDContrato.TablaRegistrosUsuarios.ID,
                BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO,
                BDContrato.TablaRegistrosUsuarios.NOMBRE,
                BDContrato.TablaRegistrosUsuarios.CEDULA,
                BDContrato.TablaRegistrosUsuarios.HORA,
                BDContrato.TablaRegistrosUsuarios.FECHA,
                BDContrato.TablaRegistrosUsuarios.TEMPERATURA,
                BDContrato.TablaRegistrosUsuarios.OBSERVACION,
                BDContrato.TablaRegistrosUsuarios.PROYECTO,
                BDContrato.TablaRegistrosUsuarios.TURNO_TRABAJO,
                BDContrato.TablaRegistrosUsuarios.SITIO_TRABAJO,
                BDContrato.TablaRegistrosUsuarios.TIPO,
                BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE
        };

        whereClause = BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO + "=? AND " + BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE + "=?";
        whereArgs = new String[]{correo, String.valueOf(BDContrato.TablaRegistrosUsuarios.DATO_NO_SINCRONIZADO) };


        Cursor cursor = mBaseDeDatos_lectura.query(
                BDContrato.TablaRegistrosUsuarios.NOMBRE_TABLA,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();

        long mID = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.ID);
        long mCorreoUsuario = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO);
        long mNombre = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.NOMBRE);
        long mCedula = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.CEDULA);
        long mHora = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.HORA);
        long mFecha = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.FECHA);
        long mTemperatura = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.TEMPERATURA);
        long mObservacion = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.OBSERVACION);
        long mProyecto = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.PROYECTO);
        long mTurnoTrabajo = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.TURNO_TRABAJO);
        long mSitioTrabajo = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.SITIO_TRABAJO);
        long mTipo = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.TIPO);
        long mSincroniaFirebase = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE);

        mRegistroUsuario.setId(cursor.getInt((int) mID));
        mRegistroUsuario.setCorreoUsuario(cursor.getString((int) mCorreoUsuario));
        mRegistroUsuario.setNombre(cursor.getString((int) mNombre));
        mRegistroUsuario.setCedula(cursor.getString((int) mCedula));
        mRegistroUsuario.setHora(cursor.getString((int) mHora));
        mRegistroUsuario.setFecha(cursor.getString((int)mFecha));
        mRegistroUsuario.setTemperatura(cursor.getString((int) mTemperatura));
        mRegistroUsuario.setObservacion(cursor.getString((int) mObservacion));
        mRegistroUsuario.setProyecto(cursor.getString((int) mProyecto));
        mRegistroUsuario.setTurnoTrabajo(cursor.getString((int) mTurnoTrabajo));
        mRegistroUsuario.setSitioTrabajo(cursor.getString((int) mSitioTrabajo));
        mRegistroUsuario.setTipo(cursor.getString((int) mTipo));
        mRegistroUsuario.setSincronizacionFirebase(cursor.getInt((int) mSincroniaFirebase));

        cursor.close();
        return mRegistroUsuario;
    }

    //TODO Deberia buscar los registros asociados a un USUARIO
    public ArrayList<RegistroUsuario> RegistrosUsuario(String correo)
    {
        ArrayList<RegistroUsuario> mArrayRegistrosDesincronizados = new ArrayList<RegistroUsuario>();

        String[] projection = {
                BDContrato.TablaRegistrosUsuarios.ID,
                BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO,
                BDContrato.TablaRegistrosUsuarios.NOMBRE,
                BDContrato.TablaRegistrosUsuarios.CEDULA,
                BDContrato.TablaRegistrosUsuarios.HORA,
                BDContrato.TablaRegistrosUsuarios.FECHA,
                BDContrato.TablaRegistrosUsuarios.TEMPERATURA,
                BDContrato.TablaRegistrosUsuarios.OBSERVACION,
                BDContrato.TablaRegistrosUsuarios.PROYECTO,
                BDContrato.TablaRegistrosUsuarios.TURNO_TRABAJO,
                BDContrato.TablaRegistrosUsuarios.SITIO_TRABAJO,
                BDContrato.TablaRegistrosUsuarios.TIPO,
                BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE
        };

        Cursor cursor = mBaseDeDatos_lectura.query(
                BDContrato.TablaRegistrosUsuarios.NOMBRE_TABLA,
                projection,
                BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO + "=?",
                new String[]{correo},
                null,
                null,
                null,
                null);

        while(cursor.moveToNext())
        {
            RegistroUsuario mRegistroUsuario = new RegistroUsuario();

            long mID = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.ID);
            long mCorreoUsuario = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO);
            long mNombre = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.NOMBRE);
            long mCedula = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.CEDULA);
            long mHora = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.HORA);
            long mFecha = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.FECHA);
            long mTemperatura = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.TEMPERATURA);
            long mObservacion = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.OBSERVACION);
            long mProyecto = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.PROYECTO);
            long mTurnoTrabajo = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.TURNO_TRABAJO);
            long mSitioTrabajo = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.SITIO_TRABAJO);
            long mTipo = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.TIPO);
            long mSincroniaFirebase = cursor.getColumnIndex(BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE);

            mRegistroUsuario.setId(cursor.getInt((int) mID));
            mRegistroUsuario.setCorreoUsuario(cursor.getString((int) mCorreoUsuario));
            mRegistroUsuario.setNombre(cursor.getString((int) mNombre));
            mRegistroUsuario.setCedula(cursor.getString((int) mCedula));
            mRegistroUsuario.setHora(cursor.getString((int) mHora));
            mRegistroUsuario.setFecha(cursor.getString((int)mFecha));
            mRegistroUsuario.setTemperatura(cursor.getString((int) mTemperatura));
            mRegistroUsuario.setObservacion(cursor.getString((int) mObservacion));
            mRegistroUsuario.setProyecto(cursor.getString((int) mProyecto));
            mRegistroUsuario.setTurnoTrabajo(cursor.getString((int) mTurnoTrabajo));
            mRegistroUsuario.setSitioTrabajo(cursor.getString((int) mSitioTrabajo));
            mRegistroUsuario.setTipo(cursor.getString((int) mTipo));
            mRegistroUsuario.setSincronizacionFirebase(cursor.getInt((int) mSincroniaFirebase));

            mArrayRegistrosDesincronizados.add(mRegistroUsuario);
        }
        cursor.close();
        return  mArrayRegistrosDesincronizados;
    }

    public boolean ActualizarRegistroDesincronizado(String correo, int ID)
    {
        ContentValues valores = new ContentValues();
        String whereClause;
        String[] whereArgs;

        valores.put(BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE, BDContrato.TablaRegistrosUsuarios.DATO_SINCRONIZADO);

        whereClause = BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO + "=? AND " + BDContrato.TablaRegistrosUsuarios.ID + "=?";
        whereArgs = new String[]{correo, String.valueOf(ID) };

        long resultadoActualizar = mBaseDeDatos_escritura.update(
                BDContrato.TablaRegistrosUsuarios.NOMBRE_TABLA,
                valores,
                whereClause,
                whereArgs);

        if(resultadoActualizar > 0) return true;
        else return false;
    }

    public int CantidadRegistrosDesincronizados(String correo)
    {
        RegistroUsuario mRegistroUsuario = new RegistroUsuario();
        String whereClause;
        String[] whereArgs, projection = {
                BDContrato.TablaRegistrosUsuarios.ID,
                BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO,
                BDContrato.TablaRegistrosUsuarios.NOMBRE,
                BDContrato.TablaRegistrosUsuarios.CEDULA,
                BDContrato.TablaRegistrosUsuarios.HORA,
                BDContrato.TablaRegistrosUsuarios.FECHA,
                BDContrato.TablaRegistrosUsuarios.TEMPERATURA,
                BDContrato.TablaRegistrosUsuarios.OBSERVACION,
                BDContrato.TablaRegistrosUsuarios.PROYECTO,
                BDContrato.TablaRegistrosUsuarios.TURNO_TRABAJO,
                BDContrato.TablaRegistrosUsuarios.SITIO_TRABAJO,
                BDContrato.TablaRegistrosUsuarios.TIPO,
                BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE
        };

        whereClause = BDContrato.TablaRegistrosUsuarios.CORREO_USUARIO + "=? AND " + BDContrato.TablaRegistrosUsuarios.SINCRONIA_FIREBASE + "=?";
        whereArgs = new String[]{correo, String.valueOf(BDContrato.TablaRegistrosUsuarios.DATO_NO_SINCRONIZADO) };

        Cursor cursor = mBaseDeDatos_lectura.query(
                BDContrato.TablaRegistrosUsuarios.NOMBRE_TABLA,
                projection,
                whereClause,
                whereArgs,
                null,
                null,
                null,
                null);


        int cantidad = cursor.getCount();
        cursor.close();
        return cantidad;
    }
}
