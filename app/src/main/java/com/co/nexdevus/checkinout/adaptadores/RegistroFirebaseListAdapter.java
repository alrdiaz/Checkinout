package com.co.nexdevus.checkinout.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.co.nexdevus.checkinout.R;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroFirebase;
import com.co.nexdevus.checkinout.basededatos.objetos.RegistroUsuario;
import com.co.nexdevus.checkinout.firebase.usuario.Registro;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

/***
 * Esta clase permite obtener las caracteristicas de un ArrayAdapter, estos array adapter son utiles
 * para personalizar los objetos que se despliegan en LISTVIEWS y en RECYCLERVIEWS
 */
public class RegistroFirebaseListAdapter extends ArrayAdapter<RegistroFirebase> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<RegistroFirebase> mRegistros;
    private int mRevisarIdRecurso;

    public RegistroFirebaseListAdapter(Context context, int tvIDRecurso, ArrayList<RegistroFirebase> mmRegistro)
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
        RegistroFirebase registro = mRegistros.get(posicion);

        //se asginan los objetos XML del layout personalizado creado
        MaterialTextView tvNombre, tvCedula, tvFecha, tvHora, tvTemperatura, tvObservacion, tvProyecto, tvTurnoTrabajo, tvSitioTrabajo, tvTipo;
        tvNombre = (MaterialTextView) convertView.findViewById(R.id.tvNombre);
        tvCedula = (MaterialTextView) convertView.findViewById(R.id.tvCedula);
        tvFecha = (MaterialTextView) convertView.findViewById(R.id.tvFecha);
        tvHora = (MaterialTextView) convertView.findViewById(R.id.tvHora);
        tvTemperatura = (MaterialTextView) convertView.findViewById(R.id.tvTemperatura);
        tvObservacion = (MaterialTextView) convertView.findViewById(R.id.tvObservacion);
        tvProyecto = (MaterialTextView) convertView.findViewById(R.id.tvProyecto);
        tvTurnoTrabajo = (MaterialTextView) convertView.findViewById(R.id.tvTurnoTrabajo);
        tvSitioTrabajo = (MaterialTextView) convertView.findViewById(R.id.tvSitioTrabajo);
        tvTipo = (MaterialTextView) convertView.findViewById(R.id.tvTipo);

        //se despliegan los valores en sus respectivos campos
        tvNombre.setText(registro.getNombre());
        tvCedula.setText(registro.getCedula());
        tvFecha.setText(registro.getFecha());
        tvHora.setText(registro.getHora());
        tvFecha.setText(registro.getFecha());
        tvObservacion.setText(registro.getObservacion());
        tvProyecto.setText(registro.getProyecto());
        tvTurnoTrabajo.setText(registro.getTurnoTrabajo());
        tvSitioTrabajo.setText(registro.getSitioTrabajo());
        tvTipo.setText(registro.getTipo());

        return convertView;
    }
}
