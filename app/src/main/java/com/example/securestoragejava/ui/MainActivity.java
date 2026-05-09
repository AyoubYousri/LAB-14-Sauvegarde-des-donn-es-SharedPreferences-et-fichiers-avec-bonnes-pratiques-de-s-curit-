package com.example.securestoragejava.ui;

/*
 * Auteur: Lemghili Mohammed Amine
 * Ecran principal du lab: preferences, token chiffre, fichiers, cache et export.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.securestoragejava.R;
import com.example.securestoragejava.cache.CacheStore;
import com.example.securestoragejava.external.ExternalAppFilesStore;
import com.example.securestoragejava.files.InternalTextStore;
import com.example.securestoragejava.files.StudentsJsonStore;
import com.example.securestoragejava.model.Student;
import com.example.securestoragejava.prefs.AppPrefs;
import com.example.securestoragejava.prefs.SecurePrefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activité principale gérant l'interface utilisateur et la coordination des différentes méthodes de stockage.
 * Optimisée avec des exécutions en arrière-plan pour garantir une interface fluide.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SecureStorageJava";
    private final List<String> langs = Arrays.asList("fr", "en", "ar");

    private EditText etName;
    private EditText etToken;
    private Spinner spLang;
    private SwitchCompat swDark;
    private TextView tvResult;
    
    private boolean syncingUi;
    
    // Executor pour effectuer les opérations disque/chiffrement hors du thread principal
    private final ExecutorService diskExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme(AppPrefs.load(this).theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etToken = findViewById(R.id.etToken);
        spLang = findViewById(R.id.spLang);
        swDark = findViewById(R.id.swDark);
        tvResult = findViewById(R.id.tvResult);

        setupLangSpinner();

        findViewById(R.id.btnSavePrefs).setOnClickListener(v -> savePrefs());
        findViewById(R.id.btnLoadPrefs).setOnClickListener(v -> loadPrefsToUi());
        findViewById(R.id.btnSaveJson).setOnClickListener(v -> saveJsonFile());
        findViewById(R.id.btnLoadJson).setOnClickListener(v -> loadJsonFile());
        findViewById(R.id.btnExportExternal).setOnClickListener(v -> exportExternalFile());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());
        
        swDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (syncingUi) return;
            saveThemeFromSwitch(isChecked);
        });

        loadPrefsToUi();
    }

    private void setupLangSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, langs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLang.setAdapter(adapter);
    }

    private void saveThemeFromSwitch(boolean isDark) {
        String name = etName.getText().toString().trim();
        String lang = langs.get(Math.max(0, spLang.getSelectedItemPosition()));
        String theme = isDark ? "dark" : "light";

        diskExecutor.execute(() -> {
            AppPrefs.save(this, name, lang, theme, false);
            mainHandler.post(() -> {
                applyTheme(theme);
                Toast.makeText(this, "Thème mis à jour", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void applyTheme(String theme) {
        int mode = "dark".equals(theme) ? AppCompatDelegate.MODE_NIGHT_YES : 
                   "light".equals(theme) ? AppCompatDelegate.MODE_NIGHT_NO : 
                   AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Sauvegarde les préférences et le token en arrière-plan.
     */
    private void savePrefs() {
        String name = etName.getText().toString().trim();
        String lang = langs.get(Math.max(0, spLang.getSelectedItemPosition()));
        String theme = swDark.isChecked() ? "dark" : "light";
        String token = etToken.getText().toString();

        tvResult.setText("Sauvegarde en cours...");

        diskExecutor.execute(() -> {
            try {
                // Sauvegarde Prefs Normales
                AppPrefs.save(this, name, lang, theme, false);
                
                // Sauvegarde Token (Chiffré)
                if (!token.trim().isEmpty()) {
                    SecurePrefs.saveToken(this, token);
                }
                
                // Cache
                CacheStore.write(this, "last_ui.txt", "name=" + name);

                mainHandler.post(() -> {
                    tvResult.setText("Sauvegarde terminée avec succès.");
                    applyTheme(theme);
                    Toast.makeText(this, "Données enregistrées", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "Erreur sauvegarde", e);
                mainHandler.post(() -> tvResult.setText("Erreur lors de la sauvegarde."));
            }
        });
    }

    /**
     * Charge les données en arrière-plan pour éviter de bloquer l'UI lors du déchiffrement.
     */
    private void loadPrefsToUi() {
        tvResult.setText("Chargement...");
        
        diskExecutor.execute(() -> {
            AppPrefs.Triple triple = AppPrefs.load(this);
            int tokenLen = 0;
            boolean hasToken = false;
            try {
                String token = SecurePrefs.loadToken(this);
                tokenLen = token != null ? token.length() : 0;
                hasToken = SecurePrefs.hasValidToken(this);
            } catch (Exception ignored) {}

            final int finalTokenLen = tokenLen;
            final boolean finalHasToken = hasToken;

            mainHandler.post(() -> {
                syncingUi = true;
                etName.setText(triple.name);
                swDark.setChecked("dark".equals(triple.theme));
                int idx = langs.indexOf(triple.lang);
                spLang.setSelection(idx >= 0 ? idx : 0);
                syncingUi = false;

                tvResult.setText(String.format(
                    "Chargement terminé.\nNom: %s\nLangue: %s\nToken: %d chars (%s)",
                    triple.name, triple.lang, finalTokenLen, finalHasToken ? "Actif" : "Expiré/Absent"
                ));
            });
        });
    }

    private void saveJsonFile() {
        String name = etName.getText().toString().trim();
        diskExecutor.execute(() -> {
            try {
                List<Student> students = new ArrayList<>(StudentsJsonStore.load(this));
                if (students.isEmpty()) students.addAll(defaultStudents());
                
                if (!name.isEmpty() && !containsStudentName(students, name)) {
                    students.add(new Student(nextStudentId(students), name, 20));
                }
                
                StudentsJsonStore.save(this, students);
                InternalTextStore.writeUtf8(this, "note.txt", "MàJ JSON : " + System.currentTimeMillis());

                mainHandler.post(() -> tvResult.setText("Fichier JSON mis à jour (" + students.size() + " étudiants)."));
            } catch (Exception e) {
                mainHandler.post(() -> tvResult.setText("Erreur JSON."));
            }
        });
    }

    private List<Student> defaultStudents() {
        return Arrays.asList(new Student(1, "Amina", 20), new Student(2, "Omar", 21));
    }

    private boolean containsStudentName(List<Student> students, String name) {
        for (Student s : students) if (s.name.equalsIgnoreCase(name)) return true;
        return false;
    }

    private int nextStudentId(List<Student> students) {
        int max = 0;
        for (Student s : students) if (s.id > max) max = s.id;
        return max + 1;
    }

    private void loadJsonFile() {
        diskExecutor.execute(() -> {
            List<Student> students = StudentsJsonStore.load(this);
            StringBuilder sb = new StringBuilder("Liste des étudiants :\n");
            for (Student s : students) sb.append("- ").append(s.name).append(" (ID:").append(s.id).append(")\n");
            mainHandler.post(() -> tvResult.setText(sb.toString()));
        });
    }

    private void exportExternalFile() {
        diskExecutor.execute(() -> {
            try {
                String path = ExternalAppFilesStore.write(this, "export_lab.txt", "Données exportées le " + System.currentTimeMillis());
                mainHandler.post(() -> tvResult.setText("Exporté vers :\n" + path));
            } catch (Exception e) {
                mainHandler.post(() -> tvResult.setText("Erreur export."));
            }
        });
    }

    private void clearAll() {
        diskExecutor.execute(() -> {
            AppPrefs.clear(this);
            try { SecurePrefs.clear(this); } catch (Exception ignored) {}
            StudentsJsonStore.delete(this);
            InternalTextStore.delete(this, "note.txt");
            CacheStore.purge(this);

            mainHandler.post(() -> {
                etName.setText("");
                etToken.setText("");
                swDark.setChecked(false);
                applyTheme("light");
                tvResult.setText("Toutes les données locales ont été effacées.");
                Toast.makeText(this, "Nettoyage effectué", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
