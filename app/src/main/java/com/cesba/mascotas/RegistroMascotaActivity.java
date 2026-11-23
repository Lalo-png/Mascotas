package com.cesba.mascotas;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.cesba.mascotas.data.ParqueDao;
import com.cesba.mascotas.models.Dueno;
import com.cesba.mascotas.models.Mascota;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.List;

public class RegistroMascotaActivity extends AppCompatActivity {

    private Spinner spDuenos, spTamano;
    private EditText etNombre, etEspecie, etRaza, etPeso, etTemperamento, etNotas;
    private CheckBox cbVacunas;
    private Button btnGuardar;
    private ParqueDao parqueDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_mascota);

        parqueDao = new ParqueDao(this);

        spDuenos = findViewById(R.id.spDuenos);
        spTamano = findViewById(R.id.spTamano);
        etNombre = findViewById(R.id.etNombreMascota);
        etEspecie = findViewById(R.id.etEspecie);
        etRaza = findViewById(R.id.etRaza);
        etPeso = findViewById(R.id.etPeso);
        etTemperamento = findViewById(R.id.etTemperamento);
        etNotas = findViewById(R.id.etNotas);
        cbVacunas = findViewById(R.id.cbVacunas);
        btnGuardar = findViewById(R.id.btnGuardarMascota);

        cargarSpinners();

        btnGuardar.setOnClickListener(v -> guardarMascotaYGenerarQR());
    }

    private void cargarSpinners() {
        // 1. Cargar dueños desde SQLite
        List<Dueno> listaDuenos = parqueDao.obtenerDuenos();
        if (listaDuenos.isEmpty()) {
            Toast.makeText(this, "Primero debes registrar al menos un dueño", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ArrayAdapter<Dueno> duenoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaDuenos);
        duenoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuenos.setAdapter(duenoAdapter);

        // 2. Cargar tamaños permitidos por la restricción CHECK de la BD
        String[] tamanos = {"Chico", "Mediano", "Grande", "Gigante"};
        ArrayAdapter<String> tamanoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tamanos);
        tamanoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTamano.setAdapter(tamanoAdapter);
    }

    private void guardarMascotaYGenerarQR() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la mascota es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Dueno duenoSeleccionado = (Dueno) spDuenos.getSelectedItem();
        String especie = etEspecie.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        double peso = pesoStr.isEmpty() ? 0.0 : Double.parseDouble(pesoStr);
        String tamano = spTamano.getSelectedItem().toString();
        String temperamento = etTemperamento.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();
        int vacunas = cbVacunas.isChecked() ? 1 : 0;

        Mascota mascota = new Mascota(0, duenoSeleccionado.getIdDueno(), nombre, especie, raza, peso, tamano, temperamento, vacunas, notas);

        long idMascota = parqueDao.insertarMascota(mascota);
        if (idMascota > 0) {
            mostrarDialogoQR(idMascota, nombre);
        } else {
            Toast.makeText(this, "Error al registrar la mascota", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoQR(long idMascota, String nombreMascota) {
        try {
            String contenidoQR = "MASCOTA_ID:" + idMascota;
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(contenidoQR, BarcodeFormat.QR_CODE, 500, 500);

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            imageView.setPadding(32, 32, 32, 32);

            new AlertDialog.Builder(this)
                    .setTitle("Pase QR: " + nombreMascota)
                    .setMessage("Código generado (" + contenidoQR + "). Úsalo para registrar entrada/salida en el parque.")
                    .setView(imageView)
                    .setCancelable(false)
                    .setPositiveButton("Finalizar", (dialog, which) -> finish())
                    .show();

        } catch (Exception e) {
            Toast.makeText(this, "Mascota guardada, pero falló la generación del QR", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}