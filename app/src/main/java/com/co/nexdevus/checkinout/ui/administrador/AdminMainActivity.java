package com.co.nexdevus.checkinout.ui.administrador;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.co.nexdevus.checkinout.MainActivity;
import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.basededatos.BDContrato;
import com.co.nexdevus.checkinout.basededatos.BDManejadorDatos;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroFirebase;
import com.co.nexdevus.checkinout.datos.Constantes;
import com.co.nexdevus.checkinout.ui.administrador.detalle.DetalleActivity;
import com.co.nexdevus.checkinout.ui.usuario.UsuarioMain2Activity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class AdminMainActivity extends AppCompatActivity {

    ///region VARIABLES PARA OBJETOS XML
    ListView lvUsuarios;
    ProgressBar pbProcesando;
    FloatingActionButton btnExportarTodos;
    ///endregion VARIABLES PARA OBJETOS XML

    ///region VARIABLES OBJETOS ESPECIFICOS
    BDManejadorDatos mBDManejadorDatos;
    ///region VARIABLES OBJETOS ESPECIFICOS

    ///region VARIABLES FIREBASE
    private DatabaseReference mDatabase;
    ArrayList<RegistroFirebase> registrosFirebase;
    ///endregion VARIABLES FIREBASE

    ///region VARIABLES DE MANEJO DE DATOS EN LISTVIEW
    ArrayList<String> usuariosArray;
    ArrayAdapter<String> usuariosAdapter;
    ///endregion VARIABLES DE MANEJO DE DATOS EN LISTVIEW


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        //se asignan los objetos XML a las variables declaradas internas
        lvUsuarios = (ListView) findViewById(R.id.lvUsuarios);
        pbProcesando = (ProgressBar) findViewById(R.id.pbProcesando);
        btnExportarTodos = (FloatingActionButton) findViewById(R.id.btnExportarTodos);

        //variables para el almacenamiento registros remotos
        registrosFirebase = new ArrayList<RegistroFirebase>();
        usuariosArray = new ArrayList<String>();
        usuariosAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, usuariosArray);

        //se inicializa el adaptador del listview para mostrar nada al principio
        lvUsuarios.setAdapter(usuariosAdapter);

        /* Se inicializan las variables globales designadas a las funcionalidades de la base de datos
         * para conocer mas informacion sobre la documentacion del objeto "mDatabase" correspondiente a
         * firebase puedes revisar la documentacion aqui: https://firebase.google.com/docs/database/android/start?authuser=0
         *
         * en el caso del objeto mDBManejadorDatos es un objeto creado que se encuentra en la carpeta
         * java/com.co.nexdevus.checkinout.basededatos
         *
         * en este documento se enlazan los elementos necesarios en orden de manejar la base de datos en SQLite3
         */
        mBDManejadorDatos = new BDManejadorDatos(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //se crea un evento que permite activar acciones cuando se ejecutan cambios en la base de datos de Firebase
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //si los datos en Firebase cambian entonces se ejecutan ciertas acciones
                registrosFirebase.clear(); //se limpia el arreglo de registros de firebase
                usuariosArray.clear(); //se limpia el arreglo de registro de datos mostrados en el ListView
                usuariosAdapter.notifyDataSetChanged(); //se notifica que los datos agregados en el adaptador cambiaron
                pbProcesando.setVisibility(View.VISIBLE); //se muestra por un momento el progresbar indicando carga de datos al usuario

                /* Se buscan los datos en el objeto de Firebase recordando que es un objeto de tipo JSON
                 * el snapshot hace referencia a los datos que se encuentran de momento en la base de datos
                 * remota
                 */
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    //en este caso se obtienen los usuarios que son los hijos principales de la base de datos
                    String usuario = String.valueOf(snapshot.getKey()); //se obtiene usuario
                    String correo = usuario.replace(" ", "."); //se convierte el usuario a crreo

                    usuariosArray.add(correo); //se agrega el correo al adaptador con el cual se llena el ListView
                    usuariosAdapter.notifyDataSetChanged();//se notifica que cambiaron los datos en el adaptador

                    //al tener el hijo principal se recorren los hijos dentro de "usuarios"
                    for(DataSnapshot snap : snapshot.getChildren()) {
                        //se crea un nuevo Objeto que almacena los datos de los registros en una colecion de datos locales
                        RegistroFirebase registroFirebase = new RegistroFirebase();
                        //se declaran variables locales
                        String tiempo, fecha, hora;
                        String[] marcaTiempo;

                        /* se inicializan las variables locales tomando los datos del JSON de Firebase
                         * recordando que la estructura de datos de Firebase es distinta al a estructura de datos locales
                         * se tiene:
                         *
                         *
                         *  - (CORREO)
                         *  --------- (FECHA Y HORA)
                         * ------------------------ (ID)
                         * ------------------------ (NOMBRE)
                         * ------------------------ (CEDULA)
                         * ------------------------ (TEMPERATURA)
                         * ------------------------ (OBSERVACION)
                         * ------------------------ (PROYECTO)
                         * ------------------------ (TURNO_TRABAJO)
                         * ------------------------ (SITIO_TRABAJO)
                         * ------------------------ (TIPO)
                         *
                         */
                        tiempo = String.valueOf(snap.getKey());
                        marcaTiempo = tiempo.split(" ");
                        fecha = marcaTiempo[0];
                        hora = marcaTiempo[1];

                        /* Se almacenan los datos de firebase en un arreglo que contiene todos los datos de firebase en forma de
                         * registros lineales, en caso de necesitar algo mas especifico es posible migrar este codigo a cada muestra
                         * de datos individual, lo que haria la carga y muestra de registros de cada usuario en tiempo real
                         *
                         * tenemos entonces los datos de la siguiente manera
                         * (CORREO) (ID) (NOMBRE) (CEDULA) (FECHA) (HORA) (TEMPERATURA) (OBSERVACION) (PROYECTO) (TURNO_TRABAJO) (SITIO_TRABAJO) (TIPO)
                         */
                        registroFirebase.setCorreoUsuario(correo);
                        registroFirebase.setId(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.ID).getValue()));
                        registroFirebase.setNombre(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.NOMBRE).getValue()));
                        registroFirebase.setCedula(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.CEDULA).getValue()));
                        registroFirebase.setFecha(fecha);
                        registroFirebase.setHora(hora);
                        registroFirebase.setTemperatura(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.TEMPERATURA).getValue()));
                        registroFirebase.setObservacion(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.OBSERVACION).getValue()));
                        registroFirebase.setProyecto(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.PROYECTO).getValue()));
                        registroFirebase.setTurnoTrabajo(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.TURNO_TRABAJO).getValue()));
                        registroFirebase.setSitioTrabajo(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.SITIO_TRABAJO).getValue()));
                        registroFirebase.setTipo(String.valueOf(snap.child(BDContrato.TablaRegistrosFirebase.TIPO).getValue()));

                        registrosFirebase.add(registroFirebase); //Se almacena en un arreglo de registros
                    }
                    pbProcesando.setVisibility(View.GONE);//se establece el indicadr como invisible
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //NADA QUE HACER DE MOMENTO
            }
        };

        mDatabase.addValueEventListener(userListener);//se agrega el evento a la referencia de la base de datos

        //se agrega un evento de CLICK al presionar un elemento del LISTVIEW
        lvUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    /* al presionar un elemento, se almacena en un BUNDLE las preferencias
                     * este modo es mucho mas eficiente que el uso de shared preferences en el caso
                     * de enviar datos de una actividad a otra pero no almacena los datos de manera persistente
                     */
                    Intent intent = new Intent(view.getContext(), DetalleActivity.class);
                    intent.putExtra(Constantes.REGISTROS, registrosFirebase); //se asigna el objeto "REGISTROS FIREBASE"
                    intent.putExtra(Constantes.USUARIO, adapterView.getItemAtPosition(i).toString()); //se asigna el "CORREO" presionado
                    startActivity(intent);//Se inicia la actividad
                }catch (Exception e){
                    Log.d(Constantes.TAG_DEBUG, "error: " + e.getMessage());
                }
            }
        });

        btnExportarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Se obtiene la hora del sistema de modo tal que sea utilizada para el nombre del archivo
                 * de este modo es posible generar al menos un registro cada 1 segundo sin que se sobreescriba
                 *
                 */
                String fechaHoraSistema = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime()); //obtencion de la fecha del sistema
                //se crea un objeto que permite establecer las politicas de grabacion de datos en el dispositivo
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                //se construye el objeto que permite comunicar la aplicacion con el almacenamiento interno
                StrictMode.setVmPolicy(builder.build());
                /* Permite crear un objeto de tipo FILE que tiene como parametros
                 *  Enviorment.getExternalStorageDirectory() toma el directorio para almacenar
                 *  fechaHoraSistema.concat(".xls") permite crear un archivo con el formato:
                 *
                 * dd_MM_yyyy_HH_mm_ss.xls
                 */
                File csv = new File(Environment.getExternalStorageDirectory(), fechaHoraSistema.concat(".xls"));
                try {
                    //se crea un objeto para configurar el tipo de codificacion de lenguaje del documento
                    WorkbookSettings wbSettings = new WorkbookSettings();
                    //se establece english como lenguaje primario (por convencion es el mas reconocido por lectores)
                    wbSettings.setLocale(new Locale("en", "EN"));
                    //se crea una variable que tendra como objeto "contener" el archivo .xls (en orden de llenarlo)
                    WritableWorkbook workbook;
                    /* El workbook es el objeto que con las configuraciones de lenguaje, directorio y nombre
                     * es posible entonces editar y llenar con los siguientes procedimientos
                     */
                    workbook = Workbook.createWorkbook(csv, wbSettings);
                    //se crea una HOJA del libro excel, NOMBRE = REGISTROS y la pagina es la 0
                    WritableSheet sheet = workbook.createSheet("REGISTROS", 0);
                    /* Se agregan las celdas de la hoja, en este caso estas primeras celdas son "TITULOS" de cada columna
                     * tenemos entonces una hoja de excel con el siguiente orden
                     * ID, CORREO_USUARIO, NOMBRE, CEDULA, FECHA, HORA, TEMPERATURA, OBSERVACION, PROYECTO, TURNO_TRABAJO, SITIO_TRABAJO, TIPO
                     */
                    sheet.addCell(new Label(0, 0, BDContrato.TablaRegistrosFirebase.ID));
                    sheet.addCell(new Label(1, 0, BDContrato.TablaRegistrosFirebase.CORREO_USUARIO));
                    sheet.addCell(new Label(2, 0, BDContrato.TablaRegistrosFirebase.NOMBRE));
                    sheet.addCell(new Label(3, 0, BDContrato.TablaRegistrosFirebase.CEDULA));
                    sheet.addCell(new Label(4, 0, BDContrato.TablaRegistrosFirebase.FECHA));
                    sheet.addCell(new Label(5, 0, BDContrato.TablaRegistrosFirebase.HORA));
                    sheet.addCell(new Label(6, 0, BDContrato.TablaRegistrosFirebase.TEMPERATURA));
                    sheet.addCell(new Label(7, 0, BDContrato.TablaRegistrosFirebase.OBSERVACION));
                    sheet.addCell(new Label(8, 0, BDContrato.TablaRegistrosFirebase.PROYECTO));
                    sheet.addCell(new Label(9, 0, BDContrato.TablaRegistrosFirebase.TURNO_TRABAJO));
                    sheet.addCell(new Label(10, 0, BDContrato.TablaRegistrosFirebase.SITIO_TRABAJO));
                    sheet.addCell(new Label(11, 0, BDContrato.TablaRegistrosFirebase.TIPO));

                    /* En orden de llenar los datos correspondientes de la hoja de calculo de excel (CSV)
                     * se recorre ordenadamente los registros del objeto y se vacian en las columnas respectivas.
                     */
                    for(int i = 0; i < registrosFirebase.size(); i++)
                    {
                        RegistroFirebase registro = registrosFirebase.get(i);
                        sheet.addCell(new Label(0, i, registro.getId()));
                        sheet.addCell(new Label(1, i, registro.getCorreoUsuario()));
                        sheet.addCell(new Label(2, i, registro.getNombre()));
                        sheet.addCell(new Label(3, i, registro.getCedula()));
                        sheet.addCell(new Label(4, i, registro.getFecha()));
                        sheet.addCell(new Label(5, i, registro.getHora()));
                        sheet.addCell(new Label(6, i, registro.getTemperatura()));
                        sheet.addCell(new Label(7, i, registro.getObservacion()));
                        sheet.addCell(new Label(8, i, registro.getProyecto()));
                        sheet.addCell(new Label(9, i, registro.getTurnoTrabajo()));
                        sheet.addCell(new Label(10, i, registro.getSitioTrabajo()));
                        sheet.addCell(new Label(11, i, registro.getTipo()));

                    }
                    //se escriben los datos en el libro
                    workbook.write();
                    //se cierra la escritura del libro para permitir la escritura de otro libro nuevo
                    workbook.close();
                    //se muestra al usuario que la operacion se ejecuto correctamente
                    MostrarToast("Archivo exportado satisfactoriamente");
                } catch(Exception e){
                    Log.d(Constantes.TAG_DEBUG, "error: " + e.getMessage());
//                    MostrarToast("ocurrio un error al exportar el archivo excel");
                }
            }
        });
    }

    //permite crear un menu en la parte superior izquierda en formato de 3 puntos (opciones)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
         * El menu que se despliega en esta opcion debe estar creado anteriormente, para visualizar el actual
         * solo debemos ir a la carpeta res/menu/menu_opciones
         */
        this.getMenuInflater().inflate(R.menu.menu_opciones,menu);
        return true;
    }


    ///permite asignar una accion a cada item desplegado en el menu de opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.itemCerrarSesion://el item permite ejecutar las acciones designadas en este switch/case
                AlertaCerrarSesion();//se alerta al usuario sobre las acciones a tomar.
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Muestra un mensaje que solicita al usuario confirmar la accion solicitada.
     */
    private void AlertaCerrarSesion()
    {
        AlertDialog alert = new MaterialAlertDialogBuilder(AdminMainActivity.this)
                .setTitle("Cerrar Sesion")
                .setMessage("¿Esta seguro que desea cerrar la sesion?")
                .setPositiveButton(getString(R.string.boton_aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*
                         * En orden de realizar el cierre de sesion, es necesario eliminar las opciones guardadas por defecto
                         * en SharedPreferences, de este modo, es posible que la interfaz del menu principal (MainActivity)
                         *  no inicie sesion automaticamente.
                         *
                         * para esto:
                         * 1ro. se obtienen las preferencias almacenadas localmente
                         * 2do. se crea un objeto de edicion de preferencias
                         * 3ro. se asignan los valores iniciales correspondientes a las variables
                         * - persistencia = desactivado (permite que el MainActivity ignore una seccion de codigo)
                         * - correoSesion = null
                         * - claveSesion = null
                         * 4to. se aplican los cambios
                         */
                        //se obtiene el archivo de preferencias de usuario
                        SharedPreferences sharedPref = AdminMainActivity.this.getSharedPreferences(getString(R.string.preferencias_cio), Context.MODE_PRIVATE); //se toman las "preferencias" almacenadas
                        //se obtienen obtiene un objeto que permite editar las preferencias
                        SharedPreferences.Editor editorSharedPref = sharedPref.edit(); //objeto para editar las preferencias
                        //se modifican las preferencias respectivas.
                        editorSharedPref.putString(getString(R.string.pref_persistencia), getString(R.string.desactivado));
                        editorSharedPref.putString(getString(R.string.pref_correo_sesion), getString(R.string.valor_defecto));
                        editorSharedPref.putString(getString(R.string.pref_clave_sesion), getString(R.string.valor_defecto));
                        editorSharedPref.apply(); //se guardan las preferencias locales
                        /*
                         * luego de asegurarnos que las variables almacenadas en las preferencias han sido modificadas
                         * a sus valores iniciales, se realiza un accion de creacion de nueva actividad, pero se agrega la bandera:
                         *  - Intent.FLAG_ACTIVITY_CLEAR_TOP
                         * esto permite crear una nueva actividad y que el boton de "BACK" no este asociado a un menu anterior
                         * esto permite que la nueva actividad creada sea la unica actividad en la pila de llamadas del ciclo de vida
                         * de Android.
                         */
                        //luego de guardar las preferencias se devuelve al menu principal
                        Intent intent = new Intent(AdminMainActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.boton_cancelar),null).show();
        alert.show();
    }

    /***
     * Permite mostrar un mensaje TOAST
     * @param mensaje mensaje a mostrar
     */
    private void MostrarToast(String mensaje) { Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show(); }


}