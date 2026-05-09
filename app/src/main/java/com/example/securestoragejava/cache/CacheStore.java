package com.example.securestoragejava.cache;

/*
 * Auteur: Lemghili Mohammed Amine
 * Stockage temporaire dans cacheDir et purge manuelle du cache.
 */

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Gère le stockage des fichiers temporaires dans le répertoire de cache de l'application.
 * Les fichiers dans ce répertoire peuvent être supprimés par le système si l'espace disque est faible.
 */
public final class CacheStore {

    private CacheStore() {}

    /**
     * Écrit une chaîne de caractères dans un fichier situé dans le répertoire de cache.
     * @param context Contexte de l'application.
     * @param fileName Nom du fichier à créer ou écraser.
     * @param content Contenu textuel à sauvegarder.
     * @throws Exception En cas d'erreur d'E/S.
     */
    public static void write(Context context, String fileName, String content) throws Exception {
        // Accès au répertoire de cache interne via context.getCacheDir()
        File file = new File(context.getCacheDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Lit le contenu d'un fichier de cache.
     * @param context Contexte de l'application.
     * @param fileName Nom du fichier à lire.
     * @return Le contenu du fichier ou null s'il n'existe pas.
     * @throws Exception En cas d'erreur de lecture.
     */
    public static String read(Context context, String fileName) throws Exception {
        File file = new File(context.getCacheDir(), fileName);
        if (!file.exists()) return null;
        return readFile(file);
    }

    /**
     * Supprime tous les fichiers présents dans le répertoire de cache de l'application.
     * @param context Contexte de l'application.
     * @return Le nombre de fichiers effectivement supprimés.
     */
    public static int purge(Context context) {
        File[] files = context.getCacheDir().listFiles();
        if (files == null) return 0;
        int deleted = 0;
        for (File f : files) {
            // Suppression récursive simplifiée (ne gère pas les sous-dossiers ici)
            if (f.delete()) deleted++;
        }
        return deleted;
    }

    /**
     * Méthode utilitaire privée pour lire le contenu d'un objet File.
     */
    private static String readFile(File file) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
        // Retourne le contenu en utilisant l'encodage UTF-8
        return bos.toString(StandardCharsets.UTF_8.name());
    }
}
