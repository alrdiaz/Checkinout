package com.co.nexdevus.checkinout.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroUsuario;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

/***
 * Esta clase permite obtener las caracteristicas de un ArrayAdapter, estos array adapter son utiles
 * para personalizar los objetos que se despliegan en LISTVIEWS y en RECYCLERVIEWS
 */
public class RegistroListAdapter extends ArrayAdapter<RegistroUsuario> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<RegistroUsuario> mRegistros;
    private int mRevisarIdRecurso;

    public RegistroListAdapter(Context context, int tvIDRecurso, ArrayList<RegistroUsuario> mmRegistro)
    {
        super(context, tvIDRecurso, mmRegistro);
        this.mRegistros = mmRegistro;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mRevisarIdRecurso = tvIDRecurso;
    }

    public View getView(int posicion, View convertView, ViewGroup parent)
    {
        ///se obtiene la variable del recurso a desplegar
        convertView = mLayoutInflater.inflate(mRevisarIdRecurso, null);

        //se usan los datos del objeto
        RegistroUsuario registro = mRegistros.get(posicion);

        //se asginan los objetos XML del layout personalizado creado
        MaterialTextView txtFecha, txtHora, txtNombre, txtCedula, txtTemperatura;
        txtFecha = (MaterialTextView) convertView.findViewById(R.id.txtFecha);
        txtHora = (MaterialTextView) convertView.findViewById(R.id.txtHora);
        txtNombre = (MaterialTextView) convertView.findViewById(R.id.txtNombre);
        txtCedula = (MaterialTextView) convertView.findViewById(R.id.txtCedula);
        txtTemperatura = (MaterialTextView) convertView.findViewById(R.id.txtTemperatura);

        //se despliegan los valores en sus respectivos campos
        txtFecha.setText(registro.getFecha());
        txtHora.setText(registro.getHora());
        txtNombre.setText(registro.getNombre());
        txtCedula.setText(registro.getCedula());
        txtTemperatura.setText(registro.getTemperatura());

        return convertView;
    }
}
