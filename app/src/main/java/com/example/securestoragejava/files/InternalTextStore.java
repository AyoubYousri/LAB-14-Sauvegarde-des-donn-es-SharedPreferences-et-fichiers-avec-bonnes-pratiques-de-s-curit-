package com.example.securestoragejava.files;

/*
 * Auteur: Lemghili Mohammed Amine
 * Lecture et ecriture de fichiers texte UTF-8 dans le stockage interne prive.
 */

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Classe utilitaire pour la gestion de fichiers texte simples dans le stockage interne.
 * Le stockage interne est privé à l'application et les fichiers sont supprimés lors de la désinstallation.
 */
public final class InternalTextStore {

    private InternalTextStore() {}

    /**
     * Écrit du texte en encodage UTF-8 dans un fichier interne.
     * @param context Contexte de l'application.
     * @param fileName Nom du fichier (ex: "note.txt").
     * @param content Chaîne de caractères à sauvegarder.
     * @throws Exception En cas d'erreur lors de l'ouverture ou de l'écriture.
     */
    public static void writeUtf8(Context context, String fileName, String content) throws Exception {
        // openFileOutput crée un fichier dans /data/user/0/com.example.securestoragejava/files/
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Lit le contenu d'un fichier texte interne.
     * @param context Contexte de l'application.
     * @param fileName Nom du fichier à lire.
     * @return Le contenu du fichier sous forme de String.
     * @throws Exception Si le fichier n'existe pas ou en cas d'erreur de lecture.
     */
    public static String readUtf8(Context context, String fileName) throws Exception {
        try (FileInputStream fis = context.openFileInput(fileName)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            return bos.toString(StandardCharsets.UTF_8.name());
        }
    }

    /**
     * Supprime un fichier du stockage interne.
     * @param context Contexte de l'application.
     * @param fileName Nom du fichier à supprimer.
     * @return true si la suppression a réussi.
     */
    public static boolean delete(Context context, String fileName) {
        return context.deleteFile(fileName);
    }
}
