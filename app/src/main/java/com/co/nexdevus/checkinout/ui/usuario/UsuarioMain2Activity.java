package com.co.nexdevus.checkinout.ui.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.co.nexdevus.checkinout.MainActivity;
import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.basededatos.BDManejadorDatos;
import com.co.nexdevus.checkinout.ui.usuario.principal.PrincipalFragment;
import com.co.nexdevus.checkinout.ui.usuario.registros.RegistrosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UsuarioMain2Activity extends AppCompatActivity {

    ///region VARIABLES PARA OBJETOS XML
    BottomNavigationView bnvBarraInferior;
    ///endregion VARIABLES PARA OBJETOS XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_main2);

        //se asignan los objetos XML a las variables internas
        bnvBarraInferior = (BottomNavigationView) findViewById(R.id.bnvBarraInferior);

        //se inicializa el menu PrincipalFragment como pantalla principal a mostrar.
        getSupportFragmentManager().beginTransaction().replace(R.id.flContenedor, new PrincipalFragment()).commit();

        //se asigna funcionalidad a la barra de navegacion inferior
        bnvBarraInferior.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragmentoSeleccionado = null;//se crea un oobjeto que almacenara el fragmento correspondiente cada menu
                switch (menuItem.getItemId()) {
                    case R.id.itemPrincipal: //si el usuario presiona el item "PRINCIPAL"
                        fragmentoSeleccionado = new PrincipalFragment();
                        break;
                    case R.id.itemRegistros: //si el usuario presiona el item "REGISTROS"
                        fragmentoSeleccionado = new RegistrosFragment();
                        break;
                }
                //se envia la seleccion al fragmentManager (que permite mostrar uno u otro fragmento)
                getSupportFragmentManager().beginTransaction().replace(R.id.flContenedor, fragmentoSeleccionado).commit();
                return true;
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
        this.getMenuInflater().inflate(R.menu.menu_opciones,menu); //Se carga el menu previamente creado
        return true;
    }


    ///permite asignar una accion a cada item desplegado en el menu de opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.itemCerrarSesion: //el item permite ejecutar las acciones designadas en este switch/case
                AlertaCerrarSesion(); //se alerta al usuario sobre las acciones a tomar.
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Muestra un mensaje que solicita al usuario confirmar la accion solicitada.
     */
    private void AlertaCerrarSesion()
    {
        AlertDialog alert = new MaterialAlertDialogBuilder(UsuarioMain2Activity.this)
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
                        SharedPreferences sharedPref = UsuarioMain2Activity.this.getSharedPreferences(getString(R.string.preferencias_cio), Context.MODE_PRIVATE); //se toman las "preferencias" almacenadas
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
                        Intent intent = new Intent(UsuarioMain2Activity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent); //se crea la actividad
                    }
                })
                .setNegativeButton(getString(R.string.boton_cancelar),null).show();
        alert.show();
    }
}