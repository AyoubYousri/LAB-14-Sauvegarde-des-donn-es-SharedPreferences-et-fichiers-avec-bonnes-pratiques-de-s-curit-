package com.example.securestoragejava.files;

/*
 * Auteur: Lemghili Mohammed Amine
 * Sauvegarde et chargement d'une liste d'etudiants au format JSON.
 */

import android.content.Context;

import com.example.securestoragejava.model.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gère la sérialisation et la désérialisation d'une liste d'objets Student en format JSON,
 * et s'occupe de leur stockage dans les fichiers internes de l'application.
 */
public final class StudentsJsonStore {

    public static final String FILE_NAME = "students.json";

    private StudentsJsonStore() {}

    /**
     * Convertit la liste d'étudiants en JSON et la sauvegarde dans un fichier.
     * @param context Contexte de l'application.
     * @param students Liste d'étudiants à sauvegarder.
     * @throws Exception En cas d'erreur de conversion JSON ou d'écriture.
     */
    public static void save(Context context, List<Student> students) throws Exception {
        String json = toJson(students);
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Charge et désérialise la liste d'étudiants depuis le fichier JSON.
     * @param context Contexte de l'application.
     * @return Liste d'étudiants, ou une liste vide si le fichier n'existe pas ou est corrompu.
     */
    public static List<Student> load(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            String json = bos.toString(StandardCharsets.UTF_8.name());
            return fromJson(json);
        } catch (Exception e) {
            // En cas d'erreur (fichier absent par ex.), on retourne une liste vide sécurisée
            return Collections.emptyList();
        }
    }

    /**
     * Supprime le fichier JSON des étudiants.
     * @param context Contexte de l'application.
     * @return true si la suppression a réussi.
     */
    public static boolean delete(Context context) {
        return context.deleteFile(FILE_NAME);
    }

    /**
     * Convertit une liste d'objets Student en une chaîne de caractères JSON.
     */
    private static String toJson(List<Student> students) throws Exception {
        JSONArray arr = new JSONArray();
        for (Student s : students) {
            JSONObject obj = new JSONObject();
            obj.put("id", s.id);
            obj.put("name", s.name);
            obj.put("age", s.age);
            arr.put(obj);
        }
        return arr.toString();
    }

    /**
     * Parse une chaîne JSON pour reconstruire la liste d'objets Student.
     */
    private static List<Student> fromJson(String json) throws Exception {
        JSONArray arr = new JSONArray(json);
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            list.add(new Student(
                obj.getInt("id"), 
                obj.getString("name"), 
                obj.getInt("age")
            ));
        }
        return list;
    }
}
