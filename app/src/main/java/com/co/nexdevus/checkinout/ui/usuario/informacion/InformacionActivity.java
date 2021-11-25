package com.co.nexdevus.checkinout.ui.usuario.informacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.co.nexdevus.checkinout.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collection;

public class InformacionActivity extends AppCompatActivity {

    Spinner spProyecto, spTurnoTrabajo, spSitioTrabajo, spTipo;
    MaterialButton btnGuardarInformacion;
    TextInputEditText etFirmaEscaner;
//    AutoCompleteTextView acProyecto, acTurnoTrabajo, acSitioTrabajo, acTipo;

    //definicion de textos fijos
    static final String SIN_SELECCION = "Seleccione";
    static final String VACIO = "";
    static final String[] DATOS_PROYECTO = { SIN_SELECCION,"Pch San Bartolomé","Pch Oibita" };
    static final String[] DATOS_TURNO_TRABAJO = { SIN_SELECCION,"Diurno","Nocturno","Sabado" };
    static final String[] DATOS_SITIO_TRABAJO = { SIN_SELECCION, "Túnel","Casa de máquinas","Tuberia GRP", "Captación","Lineas de Transmisión", "Montajes","Pruebas", "Instrumentación","Laboratorio", "Equipos", "Almacén", "Gerencia"};
    static final String[] DATOS_TIPO = {SIN_SELECCION, "Operativo","Visitante","Proveedor", "Conductor" };

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        //se asignan los elementos del XML a las variables JAVA
        spProyecto = (Spinner) findViewById(R.id.spProyecto);
        spTurnoTrabajo = (Spinner) findViewById(R.id.spTurnoTrabajo);
        spSitioTrabajo = (Spinner) findViewById(R.id.spSitioTrabajo);
        spTipo = (Spinner) findViewById(R.id.spTipo);
//        acProyecto = (AutoCompleteTextView) findViewById(R.id.acProyecto);
//        acTurnoTrabajo = (AutoCompleteTextView) findViewById(R.id.acTurnoTrabajo);
//        acSitioTrabajo = (AutoCompleteTextView) findViewById(R.id.acSitioTrabajo);
//        acTipo = (AutoCompleteTextView) findViewById(R.id.acTipo);
        etFirmaEscaner = (TextInputEditText) findViewById(R.id.etFirmaEscaner);
        btnGuardarInformacion = (MaterialButton) findViewById(R.id.btnGuardarInformacion);

        //se agregan los datos a los respectivos Spinners
        spProyecto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_PROYECTO));
        spTurnoTrabajo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_TURNO_TRABAJO));
        spSitioTrabajo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_SITIO_TRABAJO));
        spTipo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_TIPO));
//        acProyecto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_PROYECTO));
//        acTurnoTrabajo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_TURNO_TRABAJO));
//        acSitioTrabajo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_SITIO_TRABAJO));
//        acTipo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DATOS_TIPO));

        sharedPref = this.getSharedPreferences(getString(R.string.preferencias_informacion_general), Context.MODE_PRIVATE); //se toman las "preferencias" almacenadas

        btnGuardarInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se verifica que todos los campos han sido rellenados satisfactoriamente con alguna opcion valida
                if(spProyecto.getSelectedItem().toString().equals(SIN_SELECCION))
                {
                    MostrarToast("Debes escoger un Proyecto");
                    return;
                }
                if(spSitioTrabajo.getSelectedItem().toString().equals(SIN_SELECCION))
                {
                    MostrarToast("Debes escoger un sitio de Trabajo");
                    return;
                }
                if(spTurnoTrabajo.getSelectedItem().toString().equals(SIN_SELECCION))
                {
                    MostrarToast("Debes escoger un Turno de trabajo");
                    return;
                }
                if(spTipo.getSelectedItem().toString().equals(SIN_SELECCION))
                {
                    MostrarToast("Debes escger un Tipo");
                    return;
                }
                if(etFirmaEscaner.getText().toString().equals(VACIO))
                {
                    MostrarToast("Debes escribir la firma");
                    return;
                }

                //se obtienen las preferencias almacenadas localmente DEDICADAS A LA INFORMACION
                SharedPreferences.Editor editorSharedPref = sharedPref.edit(); //objeto para editar las preferencias
                editorSharedPref.putString(getString(R.string.pref_proyecto), spProyecto.getSelectedItem().toString());
                editorSharedPref.putString(getString(R.string.pref_turno_trabajo), spTurnoTrabajo.getSelectedItem().toString());
                editorSharedPref.putString(getString(R.string.pref_sitio_trabajo), spSitioTrabajo.getSelectedItem().toString());
                editorSharedPref.putString(getString(R.string.pref_tipo), spTipo.getSelectedItem().toString());
                editorSharedPref.putString(getString(R.string.pref_firma_escaner), etFirmaEscaner.getText().toString());
                editorSharedPref.apply();
                MostrarToast("Informacion General Guardada");
                finish();
            }
        });
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