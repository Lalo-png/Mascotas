package com.cesba.mascotas.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParqueDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "parque_mascotas.db";
    private static final int DATABASE_VERSION = 1;
    private static ParqueDbHelper instance;

    public static synchronized ParqueDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ParqueDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private ParqueDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS duenos (" +
                "id_dueno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "telefono TEXT NOT NULL, " +
                "email TEXT, " +
                "contacto_emergencia TEXT, " +
                "fecha_registro TEXT DEFAULT (datetime('now', 'localtime'))" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS areas (" +
                "id_area INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE, " +
                "capacidad_maxima INTEGER DEFAULT 20, " +
                "reglas_restricciones TEXT, " +
                "activa INTEGER DEFAULT 1, " +
                "horario_apertura TEXT DEFAULT '07:00', " +
                "horario_cierre TEXT DEFAULT '19:00'" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS mascotas (" +
                "id_mascota INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_dueno INTEGER NOT NULL, " +
                "nombre TEXT NOT NULL, " +
                "especie TEXT NOT NULL DEFAULT 'Perro', " +
                "raza TEXT, " +
                "peso_kg REAL, " +
                "tamano TEXT CHECK (tamano IN ('Chico', 'Mediano', 'Grande', 'Gigante')), " +
                "temperamento TEXT, " +
                "vacunas_al_dia INTEGER DEFAULT 1, " +
                "notas_medicas TEXT, " +
                "FOREIGN KEY (id_dueno) REFERENCES duenos (id_dueno) ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS visitas (" +
                "id_visita INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_mascota INTEGER NOT NULL, " +
                "id_area INTEGER NOT NULL, " +
                "fecha_entrada TEXT NOT NULL DEFAULT (datetime('now', 'localtime')), " +
                "fecha_salida TEXT, " +
                "observaciones_entrada TEXT, " +
                "observaciones_salida TEXT, " +
                "FOREIGN KEY (id_mascota) REFERENCES mascotas (id_mascota) ON DELETE RESTRICT, " +
                "FOREIGN KEY (id_area) REFERENCES areas (id_area) ON DELETE RESTRICT" +
                ");");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_visitas_mascota ON visitas(id_mascota);");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_visitas_area ON visitas(id_area);");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_visitas_fechas ON visitas(fecha_entrada, fecha_salida);");

        db.execSQL("INSERT INTO areas (nombre, capacidad_maxima, reglas_restricciones, activa, horario_apertura, horario_cierre) " +
                "VALUES ('Agilidad', 10, 'Solo sociables con arnés', 1, '07:00', '19:00');");
        db.execSQL("INSERT INTO areas (nombre, capacidad_maxima, reglas_restricciones, activa, horario_apertura, horario_cierre) " +
                "VALUES ('Perros Pequeños', 15, 'Razas menores a 10kg', 1, '08:00', '20:00');");
        db.execSQL("INSERT INTO areas (nombre, capacidad_maxima, reglas_restricciones, activa, horario_apertura, horario_cierre) " +
                "VALUES ('Alberca Canina', 6, 'Chaleco salvavidas obligatorio si aplica', 1, '09:00', '18:00');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS visitas;");
        db.execSQL("DROP TABLE IF EXISTS mascotas;");
        db.execSQL("DROP TABLE IF EXISTS areas;");
        db.execSQL("DROP TABLE IF EXISTS duenos;");
        onCreate(db);
    }
}
