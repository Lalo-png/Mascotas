package com.cesba.mascotas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cesba.mascotas.models.AreaConAforo;
import com.cesba.mascotas.models.Dueno;
import com.cesba.mascotas.models.Mascota;

import java.util.ArrayList;
import java.util.List;

public class ParqueDao {
    private final ParqueDbHelper dbHelper;

    public ParqueDao(Context context) {
        this.dbHelper = ParqueDbHelper.getInstance(context);
    }

    public long insertarDueno(Dueno dueno) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", dueno.getNombre());
        cv.put("telefono", dueno.getTelefono());
        cv.put("email", dueno.getEmail());
        cv.put("contacto_emergencia", dueno.getContactoEmergencia());
        return db.insert("duenos", null, cv);
    }

    public List<Dueno> obtenerDuenos() {
        List<Dueno> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_dueno, nombre, telefono, email, contacto_emergencia, fecha_registro FROM duenos ORDER BY nombre ASC", null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(new Dueno(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public long insertarMascota(Mascota mascota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id_dueno", mascota.getIdDueno());
        cv.put("nombre", mascota.getNombre());
        cv.put("especie", mascota.getEspecie());
        cv.put("raza", mascota.getRaza());
        cv.put("peso_kg", mascota.getPesoKg());
        cv.put("tamano", mascota.getTamano());
        cv.put("temperamento", mascota.getTemperamento());
        cv.put("vacunas_al_dia", mascota.getVacunasAlDia());
        cv.put("notas_medicas", mascota.getNotasMedicas());
        return db.insert("mascotas", null, cv);
    }

    public Mascota obtenerMascotaPorId(int idMascota) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_mascota, id_dueno, nombre, especie, raza, peso_kg, tamano, temperamento, vacunas_al_dia, notas_medicas FROM mascotas WHERE id_mascota = ?",
                new String[]{String.valueOf(idMascota)});
        Mascota mascota = null;
        if (cursor.moveToFirst()) {
            mascota = new Mascota(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getInt(8),
                    cursor.getString(9)
            );
        }
        cursor.close();
        return mascota;
    }

    public List<AreaConAforo> obtenerAreasConAforo() {
        List<AreaConAforo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT a.id_area, a.nombre, a.capacidad_maxima, a.reglas_restricciones, a.activa, " +
                "a.horario_apertura, a.horario_cierre, COUNT(v.id_visita) AS aforo_actual " +
                "FROM areas a " +
                "LEFT JOIN visitas v ON a.id_area = v.id_area AND v.fecha_salida IS NULL " +
                "WHERE a.activa = 1 " +
                "GROUP BY a.id_area";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(new AreaConAforo(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getInt(7)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public int obtenerVisitaActivaId(int idMascota) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_visita FROM visitas WHERE id_mascota = ? AND fecha_salida IS NULL LIMIT 1",
                new String[]{String.valueOf(idMascota)});
        int idVisita = -1;
        if (cursor.moveToFirst()) {
            idVisita = cursor.getInt(0);
        }
        cursor.close();
        return idVisita;
    }

    public long registrarEntrada(int idMascota, int idArea, String observaciones) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id_mascota", idMascota);
        cv.put("id_area", idArea);
        cv.put("observaciones_entrada", observaciones);
        return db.insert("visitas", null, cv);
    }

    public int registrarSalida(int idVisita, String observaciones) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE visitas SET fecha_salida = datetime('now', 'localtime'), observaciones_salida = ? WHERE id_visita = ? AND fecha_salida IS NULL",
                new Object[]{observaciones, idVisita});
        return 1;
    }
}