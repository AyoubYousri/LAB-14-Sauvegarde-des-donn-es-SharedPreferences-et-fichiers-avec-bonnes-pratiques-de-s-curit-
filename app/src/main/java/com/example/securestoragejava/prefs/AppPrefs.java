package com.example.securestoragejava.prefs;

/*
 * Auteur: Lemghili Mohammed Amine
 * Gestion des SharedPreferences non sensibles: nom, langue et theme.
 */

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe utilitaire pour la gestion des préférences utilisateur non sensibles.
 * Utilise les SharedPreferences standards d'Android.
 */
public final class AppPrefs {

    // Nom du fichier de préférences
    private static final String PREFS_NAME = "app_prefs";
    
    // Clés pour les différentes valeurs stockées
    private static final String KEY_NAME = "pref_name";
    private static final String KEY_LANG = "pref_lang";
    private static final String KEY_THEME = "pref_theme";

    private AppPrefs() {}

    /**
     * Sauvegarde les préférences de l'utilisateur.
     * 
     * @param context Contexte de l'application.
     * @param name Nom de l'utilisateur.
     * @param lang Langue préférée (fr, en, ar).
     * @param theme Thème choisi (light, dark, system).
     * @param sync Si true, utilise commit() (synchrone), sinon apply() (asynchrone).
     * @return true si la sauvegarde est réussie (toujours true pour apply()).
     */
    public static boolean save(Context context, String name, String lang, String theme, boolean sync) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_LANG, lang)
                .putString(KEY_THEME, theme);

        if (sync) {
            // commit() écrit les données de manière synchrone sur le disque
            return editor.commit();
        } else {
            // apply() écrit les données en arrière-plan (préférable pour l'UI thread)
            editor.apply();
            return true;
        }
    }

    /**
     * Charge les préférences stockées.
     * 
     * @param context Contexte de l'application.
     * @return Un objet Triple contenant le nom, la langue et le thème.
     */
    public static Triple load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Récupération avec des valeurs par défaut si les clés n'existent pas
        String name = prefs.getString(KEY_NAME, "");
        String lang = prefs.getString(KEY_LANG, "fr");
        String theme = prefs.getString(KEY_THEME, "system");
        return new Triple(name, lang, theme);
    }

    /**
     * Supprime toutes les préférences stockées dans ce fichier.
     * @param context Contexte de l'application.
     */
    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    /**
     * Classe de données simple pour transporter les trois valeurs de préférences.
     */
    public static final class Triple {
        public final String name;
        public final String lang;
        public final String theme;

        public Triple(String name, String lang, String theme) {
            this.name = name;
            this.lang = lang;
            this.theme = theme;
        }
    }
}
