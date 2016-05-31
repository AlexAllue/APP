package com.example.allu.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Allu on 20/03/2016.
 */
public class BaseSQLiteHelper extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla de Clientes
    String sqlCreate = "CREATE TABLE Users " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " user TEXT, " +
            " password TEXT, " +
            " email TEXT )";

    //Sentencia SQL para crear la tabla de Citas
    String sqlCreate3 = "CREATE TABLE Weather " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " town TEXT, " +
            " degrees TEXT, " +
            " humidity TEXT, " +
            " longitude TEXT, " +
            " latitude TEXT, " +
            " optional TEXT )";


    public BaseSQLiteHelper(Context contexto, String nombre,
                               SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecutan las sentencias de creación de las tablas
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate3);
    }


    //Metodo en desuso para resetear la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior,
                          int versionNueva) {


/*       db.execSQL("DROP TABLE IF EXISTS Usuarios");

        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);*/
    }
}
