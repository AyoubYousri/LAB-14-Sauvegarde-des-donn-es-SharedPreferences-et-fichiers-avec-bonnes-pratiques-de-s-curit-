package com.example.securestoragejava.external;

/*
 * Auteur: Lemghili Mohammed Amine
 * Export vers le stockage externe app-specific, sans stockage public.
 */

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Gère le stockage de fichiers dans le répertoire externe spécifique à l'application.
 * Ce stockage est situé sur la carte SD ou une partition émulée, mais il reste propre à l'app.
 * Avantage : Aucune permission RUNTIME (READ/WRITE_EXTERNAL_STORAGE) n'est requise pour Android 4.4+.
 */
public final class ExternalAppFilesStore {

    private ExternalAppFilesStore() {}

    /**
     * Écrit du contenu dans un fichier sur le stockage externe privé de l'application.
     * @param context Contexte de l'application.
     * @param fileName Nom du fichier.
     * @param content Texte à sauvegarder.
     * @return Le chemin absolu du fichier créé, ou null si le stockage n'est pas disponible.
     * @throws Exception En cas d'erreur d'écriture.
     */
    public static String write(Context context, String fileName, String content) throws Exception {
        // getExternalFilesDir(null) retourne le dossier /Android/data/[package_name]/files/
        File dir = context.getExternalFilesDir(null);
        if (dir == null) return null;

        File file = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return file.getAbsolutePath();
    }

    /**
     * Lit un fichier depuis le stockage externe privé.
     * @param context Contexte de l'application.
     * @param fileName Nom du fichier.
     * @return Le contenu du fichier ou null s'il n'existe pas ou si le stockage est indisponible.
     * @throws Exception En cas d'erreur de lecture.
     */
    public static String read(Context context, String fileName) throws Exception {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) return null;

        File file = new File(dir, fileName);
        if (!file.exists()) return null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
        return bos.toString(StandardCharsets.UTF_8.name());
    }

    /**
     * Supprime un fichier du stockage externe privé.
     */
    public static boolean delete(Context context, String fileName) {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) return false;

        File file = new File(dir, fileName);
        return file.delete();
    }
}
