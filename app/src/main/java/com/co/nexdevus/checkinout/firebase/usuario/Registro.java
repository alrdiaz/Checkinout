package com.co.nexdevus.checkinout.firebase.usuario;

/***
 * Esta clase permite almacenar los datos del registro, estos datos de registro carecen de los datos
 * FECHA, HORA y el CORREO ya que este objeto esta creado para funcionar como estructura de datos ordenada
 * a almacenar como Objeto mediante Firebase.
 *
 * Recordando que la estructura firebase tiene como premisa un par KEY = VALUE
 * adicionalmente es importante recordar que, la estructura es JSON
 */
public class Registro {

    ///region LLAVES (KEY)
    public String ID;
    public String NOMBRE;
    public String CEDULA;
    public String TEMPERATURA;
    public String OBSERVACION;
    public String PROYECTO;
    public String TURNO_TRABAJO;
    public String SITIO_TRABAJO;
    public String TIPO;
    ///endregion LLAVES (KEY)

    /***
     * Constructor publico
     */
    public Registro(){}

    /***
     * Procedimiento que inicializa los valores correspondiente de las variables internas
     * @param id corresponde al registro en la base de datos local
     * @param nombre nombre de la persona escaneada
     * @param cedula documento de identidad de la persona escaneada
     * @param temperatura temperatura actual ingresada por el operador
     * @param observacion observacion tipeada por el operador
     * @param proyecto proyecto de la persona escaneada
     * @param turnoTrabajo turno de la persona escaneada
     * @param sitioTrabajo sitio de trabajo de la persona escaneada
     * @param tipo tipo de persona escaneada
     */
    public Registro(String id, String nombre, String cedula, String temperatura,
             String observacion, String proyecto, String turnoTrabajo,
             String sitioTrabajo, String tipo)
    {
        this.ID = id;
        this.NOMBRE = nombre;
        this.CEDULA = cedula;
        this.TEMPERATURA = temperatura;
        this.OBSERVACION = observacion;
        this.PROYECTO = proyecto;
        this.TURNO_TRABAJO = turnoTrabajo;
        this.SITIO_TRABAJO = sitioTrabajo;
        this.TIPO = tipo;
    }
}
