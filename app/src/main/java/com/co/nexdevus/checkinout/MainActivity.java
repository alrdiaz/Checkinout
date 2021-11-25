package com.co.nexdevus.checkinout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.co.nexdevus.checkinout.datos.Constantes;
import com.co.nexdevus.checkinout.ui.administrador.AdminMainActivity;
import com.co.nexdevus.checkinout.ui.usuario.UsuarioMain2Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ///region VARIABLES PERMISOS
    int TODOS_LOS_PERMISOS = 1;//variable que determina si todos los permisos fueron aceptados

    /* variable que contiene todos los permisos necesitados por la aplicacion para funcionar
     * 1. CAMERA = es necesario solicitar los permisos de la camara para el uso del lector QR
     * 2. WRITE_EXTERNAL_STORAGE = es necesaria para almacenar el documento XLS
     * 3. READ_EXTERNAL_STORAGE = es necesario para determinar la carpeta de almacenamiento del XLS
     */
    String[] PERMISOS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    ///endregion FIN VARIABLES PERIMSOS

    ///region FIREBASE
    private FirebaseAuth mAuth; //variable que almacena el procedimiento de Authentication
    ///endregion FIN FIREBASE

    ///region VARIABLES OBJETOS XML
    MaterialButton btnIniciarSesion;
    TextInputEditText etUsuario, etClave;
    LinearLayout llIniciandoSesion;
    MaterialCheckBox cbMantenerSesion;
    TextInputLayout tfUsuario, tfClave;
    ///endregion FIN VARIABLES OBJETOS XML

    ///region VARIABLES
    SharedPreferences sharedPref;
    String persistencia;
    ///endregion FIN VARIABLES

    //Se ejecuta una sola vez al crear la actividad.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); //se obtiene la referencia a Authentication de FIREABASE

        //se asignan los objetos en XML del menu correspondiente a las variables internas
        llIniciandoSesion = (LinearLayout) findViewById(R.id.llIniciandoSesion);
        etUsuario = (TextInputEditText) findViewById(R.id.etUsuario);
        etClave = (TextInputEditText) findViewById(R.id.etClave);
        btnIniciarSesion = (MaterialButton) findViewById(R.id.btnIniciarSesion);
        cbMantenerSesion = (MaterialCheckBox) findViewById(R.id.cbMantenerSesion);
        tfUsuario = (TextInputLayout) findViewById(R.id.tfUsuario);
        tfClave = (TextInputLayout) findViewById(R.id.tfClave);

        //se solicital nos permisos definidos en la variable PERMISOS uno tras de otro.
        if (!PermisosGarantizados(this, PERMISOS)) ActivityCompat.requestPermissions(this, PERMISOS, TODOS_LOS_PERMISOS);

        //se obtienen las preferencias almacenadas de manera local en la app
        sharedPref = this.getSharedPreferences(getString(R.string.preferencias_cio), Context.MODE_PRIVATE); //se toman las "preferencias" almacenadas

        ///region COMPORTAMIENTO ENTRADA DATOS USUARIO
        etUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                tfUsuario.setError(null); //desactiva la muestra del error en el TextInputEditField
            }
        });

        etClave.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                tfClave.setError(null); //desactiva la muestra del error en el TextInputEditField
            }
        });
        ///endregion COMPORTAMIENTO ENTRADA DATOS USUARIO

        //se asigna al boton la funcionalidad deseada
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //declaracion de variables en scope local para almacenar los datos de los objetos XML
                String usuario, clave;
                //se inicializan las variables con los valores actuales de los objetos XML
                usuario = etUsuario.getText().toString(); //usuario = correo
                clave = etClave.getText().toString(); //clave asignada al correo

                //validaciones de condiciones iniciales del de las entradas de texto
                if(usuario.equals(Constantes.VACIO))
                {
                    tfUsuario.setError("Escriba un correo electronico valido");
                    return;
                }
                if(clave.equals(Constantes.VACIO))
                {
                    tfClave.setError("Escriba una contraseña");
                    return;
                }

                //si los datos estan rellenados, entonces se procede con el proceso de inicio de sesion
                InhabilitarUI(true); // 1ro. se inhabilita la interaccion con la interfaz
                InicioSesion(usuario, clave); //2do. se envia "usuario" (correo) y "clave" a firebase (esperando su respuesta)
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Si la persistencia esta activa, entonces se debe:
        //1. bloquear la interaccion con la interfaz
        //2. intentar iniciar sesion
        //3. continuar con el menu correspondiente
        persistencia = sharedPref.getString(getString(R.string.pref_persistencia), getString(R.string.desactivado));
        if(!persistencia.equals(getString(R.string.desactivado)))
        {
            cbMantenerSesion.setChecked(true);
            InhabilitarUI(true);

            String usuario, clave;
            usuario = sharedPref.getString(getString(R.string.pref_correo_sesion), getString(R.string.valor_defecto));
            clave = sharedPref.getString(getString(R.string.pref_clave_sesion), getString(R.string.valor_defecto));

            etUsuario.setText(usuario);
            etClave.setText(clave);

            /*
             * Si se decide mantener la sesion abierta con el CHECKBOX "MANTENER SESION ABIERTA"
             * entonces no se realiza un nuevo inicio de sesion en firebase, solo se inicia sesion automaticamente
             */
            if(usuario.equals(Constantes.CORREO_ADMINISTRADOR))
            {
                MostrarToast("iniciando sesion como Administrador");
                Intent intent = new Intent(MainActivity.this, AdminMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
            {
                MostrarToast("iniciando sesion como Usuario");
                Intent intent = new Intent(MainActivity.this, UsuarioMain2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            //para iniciar sesion con FIREBASE basta con descomentar la linea 184 y comentar el segmento superior
//            InicioSesion(usuario, clave);
        }
    }

    /***
     * Permite iniciar sesion siempre y cuando exista la cuenta verificada en la nube
     * @param correo correo electronico o usuario
     * @param clave clave correspondiente
     */
    private void InicioSesion(String correo, String clave)
    {
        mAuth.signInWithEmailAndPassword(correo, clave).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //si la tarea es exitosa, es porque si existe la cuenta y esta verificada
                //se procede a iniciar sesion correspondiente (USUARIO o ADMINISTRADOR)
                if (task.isSuccessful())
                {
                    //se obtienen las preferencias almacenadas localmente
                    SharedPreferences.Editor editorSharedPref = sharedPref.edit(); //objeto para editar las preferencias
                    FirebaseUser currentUser = mAuth.getCurrentUser(); //objeto para obtener la cuenta actual confirmada

                    //se revisa si el checkbox esta "checkeado" o no
                    if(cbMantenerSesion.isChecked()) //si esta checkeado se almacenan los valores en la memoria local
                    {
                        editorSharedPref.putString(getString(R.string.pref_persistencia), getString(R.string.activado));
                        editorSharedPref.putString(getString(R.string.pref_correo_sesion), correo);
                        editorSharedPref.putString(getString(R.string.pref_clave_sesion), clave);
                    }
                    else //si no se checkea se asegura de inicializar los valores de las preferencias
                    {
                        editorSharedPref.putString(getString(R.string.pref_persistencia), getString(R.string.desactivado));
                        editorSharedPref.putString(getString(R.string.pref_correo_sesion), correo);
                        editorSharedPref.putString(getString(R.string.pref_clave_sesion), getString(R.string.valor_defecto));
                    }
                    editorSharedPref.apply(); //se guardan las preferencias locales

                    //si el correo que inicia sesion es igual al "CORREO ADMINISTRADOR"
                    //entonces se inicia sesion para la interfaz correspondiente
                    if(currentUser.getEmail().equals(Constantes.CORREO_ADMINISTRADOR))
                    {
                        MostrarToast("iniciando sesion como Administrador");
                        Intent intent = new Intent(MainActivity.this, AdminMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else //de lo contrario se inicia sesion como usuario normal.
                    {
                        MostrarToast("iniciando sesion como Usuario");
                        Intent intent = new Intent(MainActivity.this, UsuarioMain2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
                else
                {
                    MostrarToast("fallo el inicio de sesion... revise los datos utilizados");
                    InhabilitarUI(false);
                }

            }
        });
    }

    /***
     * Inhabilita o habilita la interaccion con la interfaz, segun sea el caso.
     * @param accion true = habilita
     */
    private void InhabilitarUI(boolean accion)
    {
        if(accion)
        {
            etClave.setEnabled(!accion);
            etUsuario.setEnabled(!accion);
            btnIniciarSesion.setEnabled(!accion);
            cbMantenerSesion.setEnabled(!accion);
            llIniciandoSesion.setVisibility(View.VISIBLE);
        }
        else
        {
            etClave.setEnabled(!accion);
            etUsuario.setEnabled(!accion);
            btnIniciarSesion.setEnabled(!accion);
            cbMantenerSesion.setEnabled(!accion);
            llIniciandoSesion.setVisibility(View.GONE);
        }
    }

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
        //TODO CERRAR APP DESDE FRAGMENTO
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
     * Permite desplegar un mensaje TOAST
     * @param mensaje cadena de caracteres a mostrar en el TOAST
     */
    private void MostrarToast(String mensaje)
    {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}