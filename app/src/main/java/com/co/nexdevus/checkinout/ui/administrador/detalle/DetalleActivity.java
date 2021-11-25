package com.co.nexdevus.checkinout.ui.administrador.detalle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.adaptadores.RegistroFirebaseListAdapter;
import com.co.nexdevus.checkinout.adaptadores.RegistroListAdapter;
import com.co.nexdevus.checkinout.basededatos.BDContrato;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroFirebase;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroUsuario;
import com.co.nexdevus.checkinout.datos.Constantes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class DetalleActivity extends AppCompatActivity {

    ///region VARIABLES PARA OBJETOS XML
    FloatingActionButton btnExportar;
    ListView lvRegistrosUsuario;
    ///endregion VARIABLES PARA OBJETOS XML

    ///region VARIABLES DE MANEJO DE DATOS EN LISTVIEW
    RegistroFirebaseListAdapter registroFirebaseListAdapter;
    ArrayList<RegistroFirebase> registrosFirebase;
    ArrayList<RegistroFirebase> registrosFirebaseUsuario;
    ///endregion VARIABLES DE MANEJO DE DATOS EN LISTVIEW

    String correo; //variable global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        //se asignan los objetos XML a las variables declaradas internas
        btnExportar = (FloatingActionButton) findViewById(R.id.btnExportar);
        lvRegistrosUsuario = (ListView) findViewById(R.id.lvRegistrosUsuario);

        try{
            //se obtienen las variables importadas como Bundle
            Intent mIntent = getIntent();
            Bundle mBundle = mIntent.getExtras();
            /* en orden de realizar una adquisicion de los datos mediante bundle, es necesario que el objeto
             * RegistroFirebase extienda la clase Serializable de este modo es posible pasar un Object[]
             * personalizable en forma de clase instanciable
             */
            registrosFirebase = (ArrayList<RegistroFirebase>) mBundle.get(Constantes.REGISTROS);
            correo = (String) mBundle.get(Constantes.USUARIO);
        }catch (Exception e){
            Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        /*
         * se confirma si el correo no es igual a un valor por defecto en este caso "null"
         * esto permite asegurar de que no se realizan acciones innecesarias
         */
        if(registrosFirebase.size() > 0)
        {
            //se crea un objeto para almacenar los registros dentro del Arraylist que coincidan con el correo a revisar
            registrosFirebaseUsuario = new ArrayList<RegistroFirebase>();
            for(int i = 0; i < registrosFirebase.size(); i++)
            {
                //se recorre el vector en orden de revisar si el correo el el registro coincide
                if(registrosFirebase.get(i).getCorreoUsuario().equals(correo))
                {
                    //si coincide, entonces se agrega en el vector designado para luego mostrarlo
                    registrosFirebaseUsuario.add(registrosFirebase.get(i));
                    registroFirebaseListAdapter = new RegistroFirebaseListAdapter(this, R.layout.registro_firebase_list_adapter, registrosFirebaseUsuario);
                    lvRegistrosUsuario.setAdapter(registroFirebaseListAdapter);
                }
            }
        }

        //se crea el evento que permite detectar el evento de pulsar el boton
        btnExportar.setOnClickListener(new View.OnClickListener() {
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
                    for(int i = 0; i < registrosFirebaseUsuario.size(); i++)
                    {
                        RegistroFirebase registro = registrosFirebaseUsuario.get(i);
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
                    MostrarToast("ocurrio un error al exportar el archivo excel");
                }
            }
        });
    }

    /***
     * Permite mostrar un mensaje TOAST
     * @param mensaje mensaje a mostrar
     */
    private void MostrarToast(String mensaje) { Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show(); }
}