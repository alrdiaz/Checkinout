package com.co.nexdevus.checkinout.ui.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.co.nexdevus.checkinout.MainActivity;
import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.ui.usuario.informacion.InformacionActivity;
import com.co.nexdevus.checkinout.ui.usuario.registros.RegistrosActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UsuarioMainActivity extends AppCompatActivity {

    int TODOS_LOS_PERMISOS = 1; //variables para solicitud de permisos
    //variable que contiene todos los permisos necesitados por la aplicacion para funcionar
    String[] PERMISOS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    CameraSource fuenteCamara = null;
    String tokenLeido;

    MaterialButton btnInformacion, btnRegistros, btnGuardar;
    SurfaceView svCamara;
    TextInputEditText etNombreOperador, etLecturaID, etHora, etFecha, etTemperatura, etObservacion;
    MaterialTextView txtProyecto, txtTurnoTrabajo, txtSitioTrabajo, txtTipo, txtFirmaEscaner;
    LinearLayout llCerrandoSesion;
    ScrollView svMenu;

    private static int INTERVALO = 1000 * 3;
    Handler mManejador = new Handler();

    SharedPreferences sharedPref;

    //variable global que permite utilizarse en diferentes sitios del codigo
    private MediaPlayer mMediaPlayer = null;

    private AudioManager mAudioManager = null;

    private AudioManager.OnAudioFocusChangeListener mAFChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, because something else is also
                        // playing audio over you.
                        // i.e. for notifications or navigation directions
                        // Depending on your audio playback, you may prefer to
                        // pause playback here instead. You do you.
                        if (mMediaPlayer != null) {
//                            mMediaPlayer.pause();
                            mMediaPlayer.seekTo(0);
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Resume playback, because you hold the Audio Focus
                        // again!
                        // i.e. the phone call ended or the nav directions
                        // are finished
                        // If you implement ducking and lower the volume, be
                        // sure to return it to normal here, as well.
                        if (mMediaPlayer != null) mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        ReleaseOnFinish();
                    }
                }
            };

    final MediaPlayer.OnCompletionListener mEndPlay = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            ReleaseOnFinish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_main);

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        llCerrandoSesion = (LinearLayout) findViewById(R.id.llCerrandoSesion);
        svMenu = (ScrollView) findViewById(R.id.svMenu);

        btnInformacion = (MaterialButton) findViewById(R.id.btnInformacion);
        btnRegistros = (MaterialButton) findViewById(R.id.btnRegistros);
        btnGuardar = (MaterialButton) findViewById(R.id.btnGuardar);

        etNombreOperador = (TextInputEditText) findViewById(R.id.etNombreOperador);
        etLecturaID = (TextInputEditText) findViewById(R.id.etLecturaID);
        etHora = (TextInputEditText) findViewById(R.id.etHora);
        etFecha = (TextInputEditText) findViewById(R.id.etFecha);
        etTemperatura = (TextInputEditText) findViewById(R.id.etTemperatura);
        etObservacion = (TextInputEditText) findViewById(R.id.etObservacion);

        txtProyecto = (MaterialTextView) findViewById(R.id.txtProyecto);
        txtTurnoTrabajo = (MaterialTextView) findViewById(R.id.txtTurnoTrabajo);
        txtSitioTrabajo = (MaterialTextView) findViewById(R.id.txtSitioTrabajo);
        txtTipo = (MaterialTextView) findViewById(R.id.txtTipo);
        txtFirmaEscaner = (MaterialTextView) findViewById(R.id.txtFirmaEscaner);

        svCamara = (SurfaceView) findViewById(R.id.svCamara);

        sharedPref = this.getSharedPreferences(getString(R.string.preferencias_informacion_general), Context.MODE_PRIVATE); //se toman las "preferencias" almacenadas

        //solicitud de los permisos al usuario.
        if (!PermisosGarantizados(this, PERMISOS)) ActivityCompat.requestPermissions(this, PERMISOS, TODOS_LOS_PERMISOS);
        //else InicializarQR();

        btnInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InformacionActivity.class);
                startActivity(intent);
            }
        });

        btnRegistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RegistrosActivity.class);
                startActivity(intent);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarToast("aqui se almacenan los datos");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        InicializarQR();
    }

    @Override
    public void onResume() {
        super.onResume();

        String proyecto, sitioTrabajo, turnoTrabajo, tipo, firmaEscaner;

        proyecto = sharedPref.getString(getString(R.string.pref_proyecto), getString(R.string.valor_defecto));
        sitioTrabajo = sharedPref.getString(getString(R.string.pref_sitio_trabajo), getString(R.string.valor_defecto));
        turnoTrabajo = sharedPref.getString(getString(R.string.pref_turno_trabajo), getString(R.string.valor_defecto));
        tipo = sharedPref.getString(getString(R.string.pref_tipo), getString(R.string.valor_defecto));
        firmaEscaner = sharedPref.getString(getString(R.string.pref_firma_escaner), getString(R.string.valor_defecto));

        if(!proyecto.equals(getString(R.string.valor_defecto))) txtProyecto.setText(proyecto);
        if(!sitioTrabajo.equals(getString(R.string.valor_defecto))) txtSitioTrabajo.setText(sitioTrabajo);
        if(!turnoTrabajo.equals(getString(R.string.valor_defecto))) txtTurnoTrabajo.setText(turnoTrabajo);
        if(!tipo.equals(getString(R.string.valor_defecto))) txtTipo.setText(tipo);
        if(!firmaEscaner.equals(getString(R.string.valor_defecto))) txtFirmaEscaner.setText(firmaEscaner);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CerrarAplicacion();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            fuenteCamara.release();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.itemCerrarSesion:
                AlertaCerrarSesion();
                break;
//            case R.id.itemSincronizarFirebase:
//                if(mBDManejadorDatos.CantidadRegistrosDesincronizados() > 0)
//                {
//                    SincronizarFirebase();
////                    try {
////                        try { DetenerSincronizacionFirebase(); }catch (Exception e){ }
////                        IniciarSincronizacionFirebase();
////                    }catch (Exception e) { }
//                }
//                else MostrarToast("No hay registros por sincronizar");
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InhabilitarUI(boolean accion)
    {
        if(accion)
        {
            svMenu.setEnabled(!accion);
            etNombreOperador.setEnabled(!accion);
            etLecturaID.setEnabled(!accion);
            etHora.setEnabled(!accion);
            etFecha.setEnabled(!accion);
            etTemperatura.setEnabled(!accion);
            etObservacion.setEnabled(!accion);
            btnInformacion.setEnabled(!accion);
            btnRegistros.setEnabled(!accion);
            btnGuardar.setEnabled(!accion);
            llCerrandoSesion.setVisibility(View.VISIBLE);
        }
        else
        {
            svMenu.setEnabled(!accion);
            etNombreOperador.setEnabled(!accion);
            etLecturaID.setEnabled(!accion);
            etHora.setEnabled(!accion);
            etFecha.setEnabled(!accion);
            etTemperatura.setEnabled(!accion);
            etObservacion.setEnabled(!accion);
            btnInformacion.setEnabled(!accion);
            btnRegistros.setEnabled(!accion);
            btnGuardar.setEnabled(!accion);
            llCerrandoSesion.setVisibility(View.GONE);
        }
    }


    /***
     * Muestra un mensaje que solicita al usuario confirmar la accion solicitada.
     */
    private void AlertaCerrarSesion()
    {
        AlertDialog alert = new MaterialAlertDialogBuilder(UsuarioMainActivity.this)
                .setTitle("Cerrar Sesion")
                .setMessage("¿Esta seguro que desea cerrar la sesion?")
                .setPositiveButton(getString(R.string.boton_aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //se inhabilita la interfaz de usuario
                        InhabilitarUI(true);
                        //se obtiene el archivo de preferencias de usuario
                        SharedPreferences sharedPref = UsuarioMainActivity.this.getSharedPreferences(getString(R.string.preferencias_cio), Context.MODE_PRIVATE); //se toman las "preferencias" almacenadas
                        //se obtienen obtiene un objeto que permite editar las preferencias
                        SharedPreferences.Editor editorSharedPref = sharedPref.edit(); //objeto para editar las preferencias
                        //se modifican las preferencias respectivas.
                        editorSharedPref.putString(getString(R.string.pref_persistencia), getString(R.string.desactivado));
                        editorSharedPref.putString(getString(R.string.pref_correo_sesion), getString(R.string.valor_defecto));
                        editorSharedPref.putString(getString(R.string.pref_clave_sesion), getString(R.string.valor_defecto));
                        editorSharedPref.apply(); //se guardan las preferencias locales
                        Intent intent = new Intent(UsuarioMainActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //TODO Se cierra el menu actual y se envia al menu principal
                    }
                })
                .setNegativeButton(getString(R.string.boton_cancelar),null).show();
        alert.show();
    }

    private void InicializarQR() {
        MostrarToast("QR inicializado");
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(UsuarioMainActivity.this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        fuenteCamara = new CameraSource.Builder(UsuarioMainActivity.this, barcodeDetector).setRequestedPreviewSize(1600, 1024).setAutoFocusEnabled(true).build();

        svCamara.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try { fuenteCamara.start(svCamara.getHolder()); }
                catch (Exception e) { MostrarToast("Error al iniciar la camara: " + e.getMessage()); }
//                if (ActivityCompat.checkSelfPermission(UsuarioMainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//                {
//                    MostrarToast("no hay permisos para usar la camara");
//                    return;
//                }
//                else
//                {
//                    MostrarToast("Permisos garantizados");
//                    try { fuenteCamara.start(svCamara.getHolder()); }
//                    catch (Exception e) { MostrarToast("Error al iniciar la camara: " + e.getMessage()); }
//                }
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
                        etLecturaID.post(new Runnable() {
                            @Override
                            public void run() {etLecturaID.setText(cedula);}});

                        etNombreOperador.post(new Runnable() {
                            @Override
                            public void run() {etNombreOperador.setText(nombre);}});

                        etHora.post(new Runnable() {
                            @Override
                            public void run() {etHora.setText(hora);}});

                        etFecha.post(new Runnable() {
                            @Override
                            public void run() {etFecha.setText(fecha);}});
                    }
                }
                else MostrarToast("NO se detecta codigo");
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
            mMediaPlayer = MediaPlayer.create(this, R.raw.scanner_beep);
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
    private void ReleaseOnFinish() {
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
     * Permite esperar el resultado de la solicitud de permisos, si al menos uno falla, entonces la aplicacion se cierra
     * @param requestCode codigo solicitado
     * @param permissions permiso correspondiente
     * @param grantResults resultado
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // se revisa el caso de respuesta
        switch (requestCode) {
            case 1:
                //si el usuario no garantizo los permisos se actua CERRANDO LA APP
                if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) CerrarAplicacion();
                break;
        }
    }

    /***
     *  procedimiento que cierra la aplicacion segun la version de compilacion.
     */
    private void CerrarAplicacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) finishAffinity();
        else finish();
    }

    /***
     * Realiza una comprobacion rapida para conocer si los permisos han sido garantizados en algun momento,
     * de este modo la aplicacion puede continuar con el flujo
     * @param context contexto de la aplicacion
     * @param permisos lista de permisos
     * @return true si los permisos estan garantizados
     */
    public static boolean PermisosGarantizados(Context context, String... permisos) {
        if (context != null && permisos != null) {
            for (String permiso : permisos) {
                if (ActivityCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    /***
     * Permite mostrar un mensaje TOAST
     * @param mensaje mensaje a mostrar
     */
    private void MostrarToast(String mensaje)
    {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}