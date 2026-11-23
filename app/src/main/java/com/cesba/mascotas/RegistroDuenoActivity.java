package com.cesba.mascotas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.cesba.mascotas.data.ParqueDao;
import com.cesba.mascotas.models.Dueno;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroDuenoActivity extends AppCompatActivity {

    private EditText etNombre, etTelefono, etEmail, etContactoEmergencia;
    private Button btnGuardar;
    private ParqueDao parqueDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_dueno);

        parqueDao = new ParqueDao(this);

        etNombre = findViewById(R.id.etNombreDueno);
        etTelefono = findViewById(R.id.etTelefonoDueno);
        etEmail = findViewById(R.id.etEmailDueno);
        etContactoEmergencia = findViewById(R.id.etContactoEmergencia);
        btnGuardar = findViewById(R.id.btnGuardarDueno);

        btnGuardar.setOnClickListener(v -> guardarDueno());
    }

    private void guardarDueno() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String emergencia = etContactoEmergencia.getText().toString().trim();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Nombre y teléfono son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Dueno nuevoDueno = new Dueno(0, nombre, telefono, email, emergencia, fechaActual);

        long id = parqueDao.insertarDueno(nuevoDueno);
        if (id > 0) {
            Toast.makeText(this, "Dueño registrado con éxito (ID: " + id + ")", Toast.LENGTH_SHORT).show();
            finish(); // Regresa al dashboard principal
        } else {
            Toast.makeText(this, "Error al guardar dueño", Toast.LENGTH_SHORT).show();
        }
    }
}