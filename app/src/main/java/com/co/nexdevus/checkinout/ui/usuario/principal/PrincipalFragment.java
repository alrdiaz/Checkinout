package com.co.nexdevus.checkinout.ui.usuario.principal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.basededatos.BDManejadorDatos;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroUsuario;
import com.co.nexdevus.checkinout.datos.Constantes;
import com.co.nexdevus.checkinout.firebase.usuario.Registro;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrincipalFragment extends Fragment {

    ///region VARIABLES PARA OBJETOS XML
    FloatingActionButton btnGuardar;
    SurfaceView svCamara;
    TextInputEditText etNombre, etCedula, etHora, etFecha, etTemperatura, etObservacion;
    Spinner spProyecto, spTurnoTrabajo, spSitioTrabajo, spTipo;
    LinearLayout llCerrandoSesion;
    ScrollView svMenu;
    TextInputLayout tfHora, tfFecha, tfNombre, tfCedula, tfTemperatura;
    ///endregion VARIABLES PARA OBJETOS XML

    ///region VARIABLES GLOBALES
    String tokenLeido, correoUsuario, proyecto, turnoTrabajo, sitioTrabajo, tipoTrabajo;
    private static int INTERVALO = 1000 * 3; //1000 = 1 segundo * 3 = 3 segundos
    //variables para el llenado de datos de los SPINNERS
    static final String[] DATOS_PROYECTO = {Constantes.PROYECTO,"Pch San Bartolomé","Pch Oibita" };
    static final String[] DATOS_TURNO_TRABAJO = { Constantes.TURNO_TRABAJO,"Diurno","Nocturno","Sabado" };
    static final String[] DATOS_SITIO_TRABAJO = { Constantes.SITIO_TRABAJO, "Túnel","Casa de máquinas","Tuberia GRP", "Captación","Lineas de Transmisión", "Montajes","Pruebas", "Instrumentación","Laboratorio", "Equipos", "Almacén", "Gerencia"};
    static final String[] DATOS_TIPO = {Constantes.TIPO, "Operativo","Visitante","Proveedor", "Conductor" };
    ///endregion VARIABLES GLOBALES

    ///region VARIABLES OBJETOS ESPECIFICOS
    SharedPreferences sharedPref; //variable para almacenar las preferencias
    CameraSource fuenteCamara = null; //variable para la camara
    BDManejadorDatos mBDManejadorDatos; //variable para la base de datos local desarrollada en SQLite3
    Handler mManejador = new Handler(); //variable para tarea en segundo plano
    ///region VARIABLES OBJETOS ESPECIFICOS

    ///region VARIABLES FIREBASE
    private DatabaseReference mDatabase; //variable con la que se interactua con la base de datos RealtimeDatabase
    ///endregion VARIABLES FIREBASE

    ///region VARIABLES Y OBJETOS PARA LA REPRODUCCION DE UN AUDIO
    private MediaPlayer mMediaPlayer = null;

    private AudioManager mAudioManager = null;

    private AudioManager.OnAudioFocusChangeListener mAFChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // se baja el volumen de algun otro elemento que se este reproduciendo
                        // para reproducir nuestro audio
                        // por ejemplo. para notificaciones emergentes
                        // dependiendo del tipo de audio se puede escoger si pausar, detener u otra accion
                        if (mMediaPlayer != null) mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Se continua, ya que el foco de audio lo tenemos nuevamente
                        // por ejemplo: alguna llamada o alguna direccion de gps finalizo
                        // si se implementa alguna tecnica de bajar el volumen
                        // es bueno asegurarnos de devolverlo al estado de reproduccion
                        // inicial normal
                        if (mMediaPlayer != null) mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        SoltarAlFinalizar(); //libera los recursos implementados
                    }
                }
            };

    //evento que indica cuando el audio termino de reproducir
    final MediaPlayer.OnCompletionListener mEndPlay = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            SoltarAlFinalizar();
        }
    };
    ///endregion VARIABLES Y OBJETOS PARA LA REPRODUCCION DE UN AUDIO

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* al usar FRAGMENTS es necesario obtener una referencia "root" o "raiz" del menu donde estamos
         * por eso es necesario crear una variable con la cual asignar los elementos de la actividad
         * asi como tambien los elementos que forman parte del XML
         */
        //Se obtiene la raiz del menu
        View raiz = inflater.inflate(R.layout.fragment_principal, container, false);

        //se asigna el manejador de audio en cuestion para esta actividad (fragment)
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        //se asignan los objetos XML a las variables declaras internas
        llCerrandoSesion = (LinearLayout) raiz.findViewById(R.id.llCerrandoSesionFrag);
        svMenu = (ScrollView) raiz.findViewById(R.id.svMenuFrag);
        btnGuardar = (FloatingActionButton) raiz.findViewById(R.id.btnGuardarFrag);
        etNombre = (TextInputEditText) raiz.findViewById(R.id.etNombreFrag);
        etCedula = (TextInputEditText) raiz.findViewById(R.id.etCedulaFrag);
        etHora = (TextInputEditText) raiz.findViewById(R.id.etHoraFrag);
        etFecha = (TextInputEditText) raiz.findViewById(R.id.etFechaFrag);
        etTemperatura = (TextInputEditText) raiz.findViewById(R.id.etTemperaturaFrag);
        etObservacion = (TextInputEditText) raiz.findViewById(R.id.etObservacionFrag);
        spProyecto = (Spinner) raiz.findViewById(R.id.spProyectoFrag);
        spTurnoTrabajo = (Spinner) raiz.findViewById(R.id.spTurnoTrabajoFrag);
        spSitioTrabajo = (Spinner) raiz.findViewById(R.id.spSitioTrabajoFrag);
        spTipo = (Spinner) raiz.findViewById(R.id.spTipoFrag);
        tfNombre = (TextInputLayout) raiz.findViewById(R.id.tfNombreFrag);
        tfCedula = (TextInputLayout) raiz.findViewById(R.id.tfCedulaFrag);
        tfHora = (TextInputLayout) raiz.findViewById(R.id.tfHoraFrag);
        tfFecha = (TextInputLayout) raiz.findViewById(R.id.tfFechaFrag);
        tfTemperatura = (TextInputLayout) raiz.findViewById(R.id.tfTemperaturaFrag);
        svCamara = (SurfaceView) raiz.findViewById(R.id.svCamaraFrag);

        //se agregan los datos a los respectivos Spinners
        spProyecto.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, DATOS_PROYECTO));
        spTurnoTrabajo.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, DATOS_TURNO_TRABAJO));
        spSitioTrabajo.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, DATOS_SITIO_TRABAJO));
        spTipo.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, DATOS_TIPO));

        //se buscan las preferencias almacenadas
        sharedPref = getContext().getSharedPreferences(getString(R.string.preferencias_cio), Context.MODE_PRIVATE);
        //se revisa el valor del objeto y se asigna a una variable global para usarla en toda la clase
        correoUsuario = sharedPref.getString(getString(R.string.pref_correo_sesion), getString(R.string.valor_defecto));

        /* Se inicializan las variables globales designadas a las funcionalidades de la base de datos
         * para conocer mas informacion sobre la documentacion del objeto "mDatabase" correspondiente a
         * firebase puedes revisar la documentacion aqui: https://firebase.google.com/docs/database/android/start?authuser=0
         *
         * en el caso del objeto mDBManejadorDatos es un objeto creado que se encuentra en la carpeta
         * java/com.co.nexdevus.checkinout.basededatos
         *
         * en este documento se enlazan los elementos necesarios en orden de manejar la base de datos en SQLite3
         */
        mBDManejadorDatos = new BDManejadorDatos(getContext()); //objeto que maneja la base de datos local SQLite3
        mDatabase = FirebaseDatabase.getInstance().getReference(); //objeto que maneja la referencia de datos de Firebase

        tfTemperatura.setError("Agregue Temperatura");

        ///region COMPORTAMIENTO INTERFAZ USUARIO
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                tfNombre.setError(null); //despues de que el texto cambia se elimina el error mostrado
            }
        });

        etCedula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tfCedula.setError(null);//despues de que el texto cambia se elimina el error mostrado
            }
        });

        etFecha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                tfFecha.setError(null);//despues de que el texto cambia se elimina el error mostrado
            }
        });

        etHora.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                tfHora.setError(null);//despues de que el texto cambia se elimina el error mostrado
            }
        });

        etTemperatura.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                tfTemperatura.setError(null);//despues de que el texto cambia se elimina el error mostrado
            }
        });


        tfHora.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActualizarTiempoActual();
            }
        });

        tfFecha.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActualizarTiempoActual();
            }
        });
        ///endregion COMPORTAMIENTO INTERFAZ USUARIO

        //se asigna el evento al boton de guardado mostrado
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //variables locales que permiten almacenar los datos de los objetos XML
                String nombre, cedula, hora, fecha, temperatura, observacion, mProyecto, mTurnoTrabajo, mSitioTrabajo, mTipo;

                //inicializacion de las variables locales con los datos provenientes de la interfaz
                nombre = etNombre.getText().toString();
                cedula = etCedula.getText().toString();
                hora = etHora.getText().toString();
                fecha = etFecha.getText().toString();
                temperatura = etTemperatura.getText().toString();
                observacion = etObservacion.getText().toString();
                mProyecto = spProyecto.getSelectedItem().toString();
                mTurnoTrabajo = spTurnoTrabajo.getSelectedItem().toString();
                mSitioTrabajo = spSitioTrabajo.getSelectedItem().toString();
                mTipo = spTipo.getSelectedItem().toString();

                ///region VALIDACION DE DATOS EN LA INTERFAZ
                if(nombre.equals(Constantes.VACIO))
                {
                    tfNombre.setError("Debe escribir un nombre");
                    return;
                }
                if(cedula.equals(Constantes.VACIO))
                {
                    tfCedula.setError("Debe escribir una cedula");
                    return;
                }
                if(hora.equals(Constantes.VACIO) || etFecha.getText().toString().equals(Constantes.VACIO))
                {
                    tfHora.setError("Actualice la hora y la fecha");
                    return;
                }
                if(proyecto.equals(Constantes.PROYECTO))
                {
                    MostrarToast("Seleccione proyecto");
                    return;
                }
                if(turnoTrabajo.equals(Constantes.TURNO_TRABAJO))
                {
                    MostrarToast("Seleccione turno trabajo");
                    return;
                }
                if(sitioTrabajo.equals(Constantes.SITIO_TRABAJO))
                {
                    MostrarToast("Seleccione sitio de trabajo");
                    return;
                }
                if(mTipo.equals(Constantes.TIPO))
                {
                    MostrarToast("Seleccione Tipo");
                    return;
                }
                ///endregion VALIDACION DE DATOS EN LA INTEFAZ

                //se almacenan los datos en la base de datos local
                int resultado = mBDManejadorDatos.NuevoRegistroUsuario(correoUsuario, nombre, cedula, hora, fecha, temperatura, observacion, mProyecto, mTurnoTrabajo, mSitioTrabajo, mTipo);
                /* Al realizar la accion de almacenamiento en la base de datos local, creando un nuevo registro
                 * la respuesta del procedimiento es la cantidad de lineas afectadas, en este caso
                 * debe ser afectada al menos 1 fila ya que se esta agregando un nuevo elemento
                 * de lo contrario significa que fallo el insertar datos en la base de datos
                 */
                if(resultado > 0) //si se afecto al menos una fila se tiene certeza de que se almaceno
                {
                    //se inicializan los valores correspondientes a los datos que deben ser llenados manualmente
                    etNombre.setText(Constantes.VACIO);
                    etCedula.setText(Constantes.VACIO);
                    etHora.setText(Constantes.VACIO);
                    etFecha.setText(Constantes.VACIO);
                    etTemperatura.setText(Constantes.VACIO);
                    etObservacion.setText(Constantes.VACIO);
                    tfTemperatura.setError("Agregue Temperatura");
                    //se envia el foco al textinicial "NOMBRE"
                    etNombre.requestFocus();
                    //se inicia la accion de almacenamiento de datos en la nube.
                    SincronizarFirebase(correoUsuario);

                    //se obtienen las preferencias almacenadas localmente
                    SharedPreferences.Editor editorSharedPref = sharedPref.edit(); //objeto para editar las preferencias
                    editorSharedPref.putString(getString(R.string.pref_proyecto), mProyecto);
                    editorSharedPref.putString(getString(R.string.pref_turno_trabajo), mTurnoTrabajo);
                    editorSharedPref.putString(getString(R.string.pref_sitio_trabajo), mSitioTrabajo);
                    editorSharedPref.putString(getString(R.string.pref_tipo), mTipo);
                    editorSharedPref.apply();

                    Log.d(Constantes.TAG_DEBUG, "guardo preferencia proyecto: " + mProyecto);

                    //se muestra al usuario un TOAST de retroalimentacion informativo
                    MostrarToast("Registro Almacenado Satisfactoriamente");
                }
                else MostrarToast("No se pudo guardar"); //se muestra un mensaje de retroalimentacion
            }
        });
        return raiz;
    }

    @Override
    public void onResume() {
        super.onResume();
        //se solicitan las variables almacenadas para los spinners
        proyecto = sharedPref.getString(getString(R.string.pref_proyecto), getString(R.string.valor_defecto));
        turnoTrabajo = sharedPref.getString(getString(R.string.pref_turno_trabajo), getString(R.string.valor_defecto));
        sitioTrabajo = sharedPref.getString(getString(R.string.pref_sitio_trabajo), getString(R.string.valor_defecto));
        tipoTrabajo = sharedPref.getString(getString(R.string.pref_tipo), getString(R.string.valor_defecto));

        if(!proyecto.equals(getString(R.string.valor_defecto))) for(int i = 0; i < DATOS_PROYECTO.length; i++) if(proyecto.equals(DATOS_PROYECTO[i])) spProyecto.setSelection(i);
        if(!turnoTrabajo.equals(getString(R.string.valor_defecto))) for(int i = 0; i < DATOS_TURNO_TRABAJO.length ; i++) if(turnoTrabajo.equals(DATOS_TURNO_TRABAJO[i])) spTurnoTrabajo.setSelection(i);
        if(!sitioTrabajo.equals(getString(R.string.valor_defecto))) for(int i = 0; i < DATOS_SITIO_TRABAJO.length; i++) if(sitioTrabajo.equals(DATOS_SITIO_TRABAJO[i])) spSitioTrabajo.setSelection(i);
        if(!tipoTrabajo.equals(getString(R.string.valor_defecto))) for(int i = 0; i < DATOS_TIPO.length; i++) if(tipoTrabajo.equals(DATOS_TIPO[i])) spTipo.setSelection(i);

        //cada vez que se resuma la aplicacion, se inicializa el QR
        InicializarQR();
        /* Se revisa si existe algun elemento desincronizado en la base de datos local
         * Si existe ese elemento desincronizado se inicia la tarea de envio de datos a la nube
         * de lo contrario continua el flujo habitual de la aplicacion
         */
        if(mBDManejadorDatos.CantidadRegistrosDesincronizados(correoUsuario) > 0) SincronizarFirebase(correoUsuario);


//        if(turnoTrabajo.equals(getString(R.string.valor_defecto)))
//        {
//            for(int i = 0; i < DATOS_TURNO_TRABAJO.length; i++)
//            {
//                if(proyecto.equals(DATOS_TURNO_TRABAJO[i]))
//                {
//                    spTurnoTrabajo.setSelection(i);
//                    break;
//                }
//            }
//        }
//
//        if(sitioTrabajo.equals(getString(R.string.valor_defecto)))
//        {
//            for(int i = 0; i < DATOS_SITIO_TRABAJO.length; i++)
//            {
//                if(sitioTrabajo.equals(DATOS_SITIO_TRABAJO[i]))
//                {
//                    spSitioTrabajo.setSelection(i);
//                    break;
//                }
//            }
//        }
//
//        if(turnoTrabajo.equals(getString(R.string.valor_defecto)))
//        {
////            for(int i = 0; i < DATOS_TURNO_TRABAJO.length; i++) if(turnoTrabajo.equals(DATOS_TURNO_TRABAJO[i])) spTurnoTrabajo.setSelection(i);
//
//            for(int i = 0; i < DATOS_TURNO_TRABAJO.length; i++)
//            {
//                if(turnoTrabajo.equals(DATOS_TURNO_TRABAJO[i]))
//                {
//                    spTurnoTrabajo.setSelection(i);
//                    break;
//                }
//            }
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /* cuando se va a pausa, se deben soltar los recursos de uso de la camara
         * ya que cada vez que se resume se reactiva, esto evita tener varias instancias
         * de llamada a la camara
         */
        try { fuenteCamara.stop(); } catch (Exception e) { }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* cuando se va a pausa, se deben soltar los recursos de uso de la camara
         * ya que cada vez que se resume se reactiva, esto evita tener varias instancias
         * de llamada a la camara
         */
        try { fuenteCamara.stop(); } catch (Exception e) { }
    }

    /***
     * inicia el proceso de sincronizacion de Firebase, buscando los registros de un correo
     */
    private void SincronizarFirebase(String correo)
    {
        //se obtienen 1 registro no sincronizado localmente correspondiente al correo que acaba de iniciar sesion
        RegistroUsuario registro = mBDManejadorDatos.RegistroDesincronizadoUsuario(correo);
        //se envia dicho registro al procedimiento de escritura en firebase
        escrituraFirebase(registro);
    }

    /***
     * Procedimiento de escritura de datos en firebase
     * @param registroUsuario el registro de usuario a almacenar
     */
    public void escrituraFirebase(RegistroUsuario registroUsuario)
    {
        ///variables locales.
        String correoReal, usuario, horaRegistro, fechaRegistro, momentoRegistro;
        int idRegistro;

        //inicializacion de variables locales
        idRegistro = registroUsuario.getId(); //almacena el ID del registro
        /* se obtiene el correo del registro
         * se reemplaza el "." del correo por un espacio debido a que Firebase no permite
         * dicho caracter
         */
        correoReal = registroUsuario.getCorreoUsuario();
        usuario = correoReal.replace(".", " ");
        //se obtiene el registro de la hora y fecha
        horaRegistro = registroUsuario.getHora();
        fechaRegistro = registroUsuario.getFecha();
        /* Se unen los registros FECHA y HORA para crear una ID unica e irrepetible por registro
         * en orden de lograr esto, la hora almacenada tiene el formato HH:MM:SS
         * se unen con un espacio para respetar el formato de no aceptacion de caracteres "." "/"
         */
        momentoRegistro = fechaRegistro.concat(" ").concat(horaRegistro);
        /* Se almacenan los demas datos en un objeto de tipo "REGISTRO" este objeto se tienen datos
         * ordenados de modo tal que puedan ser reconocidos por el procedimiento de la API de firebase
         * para ver informacion sobre esta clase la puedes encontrar en:
         *
         * java/com.co.nexdevus.checkinout/firebase/usuario/Registro
         */
        Registro registro = new Registro(
                String.valueOf(registroUsuario.getId()),
                registroUsuario.getNombre(),
                registroUsuario.getCedula(),
                registroUsuario.getTemperatura(),
                registroUsuario.getObservacion(),
                registroUsuario.getProyecto(),
                registroUsuario.getTurnoTrabajo(),
                registroUsuario.getSitioTrabajo(),
                registroUsuario.getTipo());

        /* La estructura de almacenamiento en firebase tiene la siguiente forma:
         * correo/marcaTiempo/Registro
         *
         * El siguiente procedimiento que almacena un nuevo "hijo" en la base de datos de FIREBASE
         * este procedimiento es ASINCRONICO por lo que se le da tiempo al la API de firebase de que responda
         * si la solicitud de almacenamiento ha sido completada o no
         *
         * al ser completada se busca un nuevo registro desincronizado y se inicia el proceso de envio de datos a
         * RealtimeDatabase.
         */
        mDatabase.child(usuario).child(momentoRegistro).setValue(registro, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                //si ocurrio un erro se muestra un mensaje.
                if(error != null) MostrarToast("No se puede sincronizar con Firebase en este momento");
                else
                {
                    /* si no ocurre un error, significa que el dato ha sido satisfactoriamente almacenado en la nube
                     * de este modo entonces se debe actualizar el campo SINCRONIA_FIREBASE que corresponde al registro
                     * recien sincronizado, de este modo es posible evitar duplicar la escritura de datos en firebase
                     */
                    mBDManejadorDatos.ActualizarRegistroDesincronizado(correoReal, idRegistro);
                    //se buscan si hay mas registros desincronizados para iniciar el proceso nuevamente.
                    if(mBDManejadorDatos.CantidadRegistrosDesincronizados(correoReal) > 0) SincronizarFirebase(correoReal);
                }
            }
        });
    }

    /***
     * Procedimiento que permite obtener la hora actual del sistema en los campos de la interfaz respectivos
     */
    private void ActualizarTiempoActual() {
        //se obtiene la hora y fecha con el formato del patron
        String fechaHoraSistema = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()); //obtencion de la fecha del sistema
        //se separa la fecha en hora y fecha segun el caracter designado (en este caso espacio)
        String[] fechaHora = fechaHoraSistema.split(" "); //separacion de fecha y Hora
        //se actualizan los datos en la interfaz
        etFecha.setText(fechaHora[0]);  //se muestra la fecha
        etHora.setText(fechaHora[1]); //se muestra la hora
    }

    /***
     * Procedimiento que permite habilitar la interaccion con la interfaz del usuario
     * @param accion true = inhabilita interfaz
     */
    private void InhabilitarUI(boolean accion)
    {
        if(accion)
        {
            svMenu.setEnabled(!accion);
            etNombre.setEnabled(!accion);
            etCedula.setEnabled(!accion);
            etHora.setEnabled(!accion);
            etFecha.setEnabled(!accion);
            etTemperatura.setEnabled(!accion);
            etObservacion.setEnabled(!accion);
            btnGuardar.setEnabled(!accion);
            llCerrandoSesion.setVisibility(View.VISIBLE);
        }
        else
        {
            svMenu.setEnabled(!accion);
            etNombre.setEnabled(!accion);
            etCedula.setEnabled(!accion);
            etHora.setEnabled(!accion);
            etFecha.setEnabled(!accion);
            etTemperatura.setEnabled(!accion);
            etObservacion.setEnabled(!accion);
            btnGuardar.setEnabled(!accion);
            llCerrandoSesion.setVisibility(View.GONE);
        }
    }

    /***
     * Permite inicializar las acciones correspondientes a la lectura de datos QR
     */
    private void InicializarQR() {
        //se tiene la misma funcionalidad que se tenia en el codigo KOTLIN ORIGINAL
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        fuenteCamara = new CameraSource.Builder(getContext(), barcodeDetector).setRequestedPreviewSize(1600, 1024).setAutoFocusEnabled(true).build();

        svCamara.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try { fuenteCamara.start(svCamara.getHolder()); }
                catch (Exception e) { MostrarToast("Error al iniciar la camara: " + e.getMessage()); }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) { fuenteCamara.stop(); }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                //se crea una variable para almacenar las lecuturas detectadas
                final SparseArray<Barcode> detectados = detections.getDetectedItems();
                //se pregunta si hay al menos 1 elemento leido
                if(detectados.size() > 0)
                {
                    //se almacena el token en la posicion inicial del vector
                    String token = detectados.valueAt(0).displayValue.toString();
                    //se consulta si el token leido no se habia leido anteriormente
                    if(!token.equals(tokenLeido))
                    {
                        tokenLeido = token; // si no se habia leido entonces se actualiza el token leido
                        //establecimiento de variables
                        String nombre, cedula, hora, fecha;
                        String[] lectura = tokenLeido.split(":"); //separacion de datos de QR
                        String fechaHoraSistema = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()); //obtencion de la fecha del sistema
                        String[] fechaHora = fechaHoraSistema.split(" "); //separacion de fecha y Hora

                        nombre = lectura[0];
                        cedula = lectura[1];
                        fecha = fechaHora[0];
                        hora = fechaHora[1];

                        ReproducirSonido(); //se reproduce un sonido "beep" para indicar que se ha leido el codigo QR satisfactoriamente
                        /*
                         * se comienza una rutina secundaria con un INTERVALO configurable en milisegundos
                         * esto permite reiniciar la variable que permite "volver a analizar un codigo QR"
                         * este metodo es mucho menos agresivo que un Thread con un Sleep y permite conservar
                         * la fluidez del manejo de la aplicacion
                         */
                        mManejador.postDelayed(mTarea, INTERVALO);

                        /*
                         * se ejecuta una serie de tareas que permiten actualizar en tiempo real la lectura de datos del QR
                         * y desplegarlos en la interfaz correspondiente lo que permite que sea fluida la visualizacion
                         * de los datos
                         */
                        etCedula.post(new Runnable() {
                            @Override
                            public void run() {
                                etCedula.setText(cedula);}});

                        etNombre.post(new Runnable() {
                            @Override
                            public void run() {
                                etNombre.setText(nombre);}});

                        etHora.post(new Runnable() {
                            @Override
                            public void run() {etHora.setText(hora);}});

                        etFecha.post(new Runnable() {
                            @Override
                            public void run() {etFecha.setText(fecha);}});
                    }
                }
            }
        });
    }

    /***
     * reproduce el sonido almacenado en la carpeta RAW "beep"
     */
    private void ReproducirSonido()
    {
        //se asegura que si existe un recurso siendo usado, se suelte este y se haga uso eficiente de la memoria
        if (mMediaPlayer != null) mMediaPlayer.release();
        /**
         * se solicita el AUDIO FOCUS mediante tres parametros API 15
         *
         * 1ro. AudioManager.OnAudioFocusChangeListener que es el objeto que debe ser creado para solicitar el foco
         * 2do. que tipo de audio vamos a reproducir en este caso STREAM_MUSIC ya que aunque son cortos los audios son .mp3
         * 3ro. por cuanto tiempo usaremos el foco AUDIOFOCUS_GAIN_TRANSIENT lo que significa que usaremos el foco totalmente por corto periodo de tiempo
         *
         */
        int result = mAudioManager.requestAudioFocus(mAFChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) //si entramos tenemos focus
        {
            //crea un recurso multimedia con el audio asociado
            mMediaPlayer = MediaPlayer.create(getContext(), R.raw.scanner_beep);
            //se hace la reproduccion del sonido
            mMediaPlayer.start();
            //permite que el "callback" este asignado a la variable creada "mCompletionListener" que contiene el "trigger" de la finalizacion
            // de la reproduccion del archivo multimedia
            mMediaPlayer.setOnCompletionListener(mEndPlay);

        }
    }

    /**
     * permite asegurar que el recurso se libera para maximizar el uso de la memoria en el dispositivo
     */
    private void SoltarAlFinalizar() {
        if (mMediaPlayer != null) {
            //suelta el recurso y reutiliza memoria
            mMediaPlayer.release();
            //coloca la variable con un valor null para asegurarnos que el objeto este vacio
            mMediaPlayer = null;
            //abandona el foco de manera segura una vez se limpia MediaPlayer
            mAudioManager.abandonAudioFocus(mAFChangeListener);
        }
    }

    /***
     * Tarea que se ejecuta luego del INTERVALO consumido en el POSTDELAY del HANDLER
     */
    Runnable mTarea = new Runnable() {
        @Override
        public void run() { tokenLeido = ""; }
    };

    /***
     * Permite mostrar un mensaje TOAST
     * @param mensaje mensaje a mostrar
     */
    private void MostrarToast(String mensaje)
    {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}