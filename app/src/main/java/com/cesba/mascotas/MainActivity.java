package com.cesba.mascotas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cesba.mascotas.data.ParqueDao;
import com.cesba.mascotas.models.AreaConAforo;
import com.cesba.mascotas.models.Mascota;
import com.cesba.mascotas.ui.AreasAdapter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnIrDuenos, btnIrMascotas, btnEscanearQR;
    private RecyclerView rvAreas;
    private AreasAdapter areasAdapter;
    private ParqueDao parqueDao;

    // Launcher moderno para el escáner QR de ZXing
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    procesarCodigoQR(result.getContents());
                } else {
                    Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parqueDao = new ParqueDao(this);

        btnIrDuenos = findViewById(R.id.btnIrDuenos);
        btnIrMascotas = findViewById(R.id.btnIrMascotas);
        btnEscanearQR = findViewById(R.id.btnEscanearQR);
        rvAreas = findViewById(R.id.rvAreas);

        rvAreas.setLayoutManager(new LinearLayoutManager(this));
        areasAdapter = new AreasAdapter(parqueDao.obtenerAreasConAforo());
        rvAreas.setAdapter(areasAdapter);

        // Navegación
        btnIrDuenos.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegistroDuenoActivity.class)));

        btnIrMascotas.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegistroMascotaActivity.class)));

        btnEscanearQR.setOnClickListener(v -> iniciarEscaneoQR());
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDashboard();
    }

    private void actualizarDashboard() {
        List<AreaConAforo> areasActualizadas = parqueDao.obtenerAreasConAforo();
        areasAdapter.actualizarLista(areasActualizadas);
    }

    private void iniciarEscaneoQR() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Enfoca el Pase QR de la mascota");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        qrLauncher.launch(options);
    }

    private void procesarCodigoQR(String contenido) {
        if (!contenido.startsWith("MASCOTA_ID:")) {
            Toast.makeText(this, "Código QR no válido para este parque", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            int idMascota = Integer.parseInt(contenido.replace("MASCOTA_ID:", "").trim());
            Mascota mascota = parqueDao.obtenerMascotaPorId(idMascota);

            if (mascota == null) {
                Toast.makeText(this, "Mascota no encontrada en la base de datos", Toast.LENGTH_LONG).show();
                return;
            }

            // Verificar si la mascota ya tiene una visita activa (sin fecha de salida)
            int idVisitaActiva = parqueDao.obtenerVisitaActivaId(idMascota);

            if (idVisitaActiva != -1) {
                mostrarDialogoSalida(mascota, idVisitaActiva);
            } else {
                mostrarDialogoEntrada(mascota);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Formato de ID corrupto en el QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEntrada(Mascota mascota) {
        List<AreaConAforo> areas = parqueDao.obtenerAreasConAforo();

        Spinner spAreas = new Spinner(this);
        ArrayAdapter<AreaConAforo> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAreas.setAdapter(adapter);
        spAreas.setPadding(48, 24, 48, 24);

        String alertaVacunas = (mascota.getVacunasAlDia() == 0)
                ? "\n⚠️ ATENCIÓN: Esta mascota NO tiene vacunas al día."
                : "\n✅ Vacunas al día.";

        new AlertDialog.Builder(this)
                .setTitle("Registrar ENTRADA: " + mascota.getNombre())
                .setMessage("Tamaño: " + mascota.getTamano() + " | Temperamento: " + mascota.getTemperamento()
                        + alertaVacunas + "\n\nSelecciona el área de ingreso:")
                .setView(spAreas)
                .setPositiveButton("Confirmar Entrada", (dialog, which) -> {
                    AreaConAforo areaSeleccionada = (AreaConAforo) spAreas.getSelectedItem();

                    // Validación de cupo máximo
                    if (areaSeleccionada.getAforoActual() >= areaSeleccionada.getCapacidadMaxima()) {
                        Toast.makeText(this, "Acceso denegado: El área " + areaSeleccionada.getNombre() + " está llena.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String ahora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    long res = parqueDao.registrarEntrada(mascota.getIdMascota(), areaSeleccionada.getIdArea(), ahora);

                    if (res > 0) {
                        Toast.makeText(this, "Entrada registrada en " + areaSeleccionada.getNombre(), Toast.LENGTH_SHORT).show();
                        actualizarDashboard();
                    } else {
                        Toast.makeText(this, "Error al registrar entrada", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoSalida(Mascota mascota, int idVisitaActiva) {
        new AlertDialog.Builder(this)
                .setTitle("Registrar SALIDA: " + mascota.getNombre())
                .setMessage("Esta mascota se encuentra actualmente dentro del parque.\n\n¿Deseas registrar su salida ahora?")
                .setPositiveButton("Confirmar Salida", (dialog, which) -> {
                    String ahora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    int filas = parqueDao.registrarSalida(idVisitaActiva, ahora);

                    if (filas > 0) {
                        Toast.makeText(this, "Salida registrada. ¡Vuelve pronto, " + mascota.getNombre() + "!", Toast.LENGTH_SHORT).show();
                        actualizarDashboard();
                    } else {
                        Toast.makeText(this, "Error al registrar salida", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
