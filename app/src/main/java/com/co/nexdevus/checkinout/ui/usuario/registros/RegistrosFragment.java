package com.co.nexdevus.checkinout.ui.usuario.registros;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.adaptadores.RegistroListAdapter;
import com.co.nexdevus.checkinout.basededatos.BDManejadorDatos;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroUsuario;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RegistrosFragment extends Fragment {

    ListView lvRegistros; //variable de objeto XML

    BDManejadorDatos mBDManejadorDatos; //variable de objeto de la base de datos

    ///region VARIABLES DE MANEJO DE DATOS EN LISTVIEW
    RegistroListAdapter registroListAdapter;
    ArrayList<RegistroUsuario> mRegistros;
    ///endregion VARIABLES DE MANEJO DE DATOS EN LISTVIEW

    SharedPreferences sharedPref; //variable para manejo de preferencias de usuario

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* al usar FRAGMENTS es necesario obtener una referencia "root" o "raiz" del menu donde estamos
         * por eso es necesario crear una variable con la cual asignar los elementos de la actividad
         * asi como tambien los elementos que forman parte del XML
         */
        //Se obtiene la raiz del menu
        View raiz = inflater.inflate(R.layout.fragment_registros, container, false);

        //se asignan los objetos XML a las variables declaradas internas
        lvRegistros = (ListView) raiz.findViewById(R.id.lvRegistros);

        /* Se inicializa la variable global designada a las funcionalidades de la base de datos
         * para conocer mas informacion del objeto mDBManejadorDatos es un objeto creado que se encuentra en la carpeta
         *
         * java/com.co.nexdevus.checkinout.basededatos
         *
         * en este documento se enlazan los elementos necesarios en orden de manejar la base de datos en SQLite3
         */
        mBDManejadorDatos = new BDManejadorDatos(getContext());

        //se inicializa la variable que almacenara los registros
        mRegistros = new ArrayList<RegistroUsuario>();

        //se inicializa el adaptador del listview para mostrar nada al principio
        lvRegistros.setAdapter(null);

        //se buscan las preferencias almacenadas
        sharedPref = getContext().getSharedPreferences(getString(R.string.preferencias_cio), Context.MODE_PRIVATE);
        //se inicializa una variable de Scope local
        String correo = sharedPref.getString(getString(R.string.pref_correo_sesion), getString(R.string.valor_defecto));

        /*
         * se confirma si el correo no es igual a un valor por defecto en este caso "null"
         * esto permite asegurar de que no se realizan acciones innecesarias
         */
        if (!correo.equals(getString(R.string.valor_defecto))) {
            //se toman los datos del registro segun el correo almacenado
            mRegistros = mBDManejadorDatos.RegistrosUsuario(correo);
            //se revisa si el tamaño de la coleccion de datos es superior a 0
            if (mRegistros.size() > 0)
            {
                //se crea una lista de tipo RegistroUsuario con Scope Local
                ArrayList<RegistroUsuario> registroUsuarioAux = new ArrayList<RegistroUsuario>();
                /* se recorre el arreglo de registros para tomar cada dato y mostrarlo en su respectivo objeto
                 *
                 * existe otro metodo de muestra de datos que se realiza con notificar cambios en el objeto interno
                 * pero en este caso se decidio esta tecnica.
                 */
                for (int i = 0; i < mRegistros.size(); i++) {
                    registroUsuarioAux.add(mRegistros.get(i));
                    registroListAdapter = new RegistroListAdapter(raiz.getContext(), R.layout.registro_list_adapter, registroUsuarioAux);
                    lvRegistros.setAdapter(registroListAdapter);
                }
            }
        }
        return raiz;
    }
}