package com.example.securestoragejava.prefs;

/*
 * Auteur: Lemghili Mohammed Amine
 * Gestion sécurisée du token avec MasterKey et EncryptedSharedPreferences.
 */

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

/**
 * Classe gérant le stockage sécurisé d'informations sensibles (ex: Jetons d'API).
 * Utilise la bibliothèque Jetpack Security pour chiffrer les données au repos.
 */
public final class SecurePrefs {

    private static final String PREFS_NAME = "secure_prefs";
    private static final String KEY_API_TOKEN = "secure_api_token";
    private static final String KEY_API_TOKEN_CREATED_AT = "secure_api_token_created_at";
    
    // Durée de validité du token (7 jours en millisecondes)
    private static final long TOKEN_MAX_AGE_MS = 7L * 24L * 60L * 60L * 1000L;

    private SecurePrefs() {}

    /**
     * Initialise et retourne une instance de EncryptedSharedPreferences.
     * Le chiffrement est géré de manière transparente par Android Keystore.
     */
    private static SharedPreferences securePrefs(Context context) throws Exception {
        // Création d'une clé maître (MasterKey) utilisée pour chiffrer d'autres clés
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        // Création de l'objet SharedPreferences chiffré
        return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Chiffrement de la clé
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Chiffrement de la valeur
        );
    }

    /**
     * Sauvegarde un token de manière chiffrée.
     * @param context Contexte de l'application.
     * @param token Le jeton à sauvegarder.
     * @throws Exception Si une erreur survient lors du chiffrement.
     */
    public static void saveToken(Context context, String token) throws Exception {
        securePrefs(context).edit()
                .putString(KEY_API_TOKEN, token)
                .putLong(KEY_API_TOKEN_CREATED_AT, System.currentTimeMillis())
                .apply();
    }

    /**
     * Charge le token s'il n'est pas expiré.
     * @param context Contexte de l'application.
     * @return Le token déchiffré ou une chaîne vide si absent/expiré.
     * @throws Exception Si une erreur survient lors du déchiffrement.
     */
    public static String loadToken(Context context) throws Exception {
        SharedPreferences prefs = securePrefs(context);
        if (isTokenExpired(prefs)) {
            clearToken(prefs);
            return "";
        }
        return prefs.getString(KEY_API_TOKEN, "");
    }

    /**
     * Vérifie si un token valide et non expiré est présent.
     */
    public static boolean hasValidToken(Context context) throws Exception {
        SharedPreferences prefs = securePrefs(context);
        if (isTokenExpired(prefs)) {
            clearToken(prefs);
            return false;
        }
        String token = prefs.getString(KEY_API_TOKEN, "");
        return token != null && !token.isEmpty();
    }

    /**
     * Supprime toutes les données du stockage sécurisé.
     */
    public static void clear(Context context) throws Exception {
        securePrefs(context).edit().clear().apply();
    }

    /**
     * Vérifie si le token a dépassé sa durée de validité.
     */
    private static boolean isTokenExpired(SharedPreferences prefs) {
        long createdAt = prefs.getLong(KEY_API_TOKEN_CREATED_AT, 0L);
        return createdAt > 0L && System.currentTimeMillis() - createdAt > TOKEN_MAX_AGE_MS;
    }

    /**
     * Supprime spécifiquement les clés liées au token.
     */
    private static void clearToken(SharedPreferences prefs) {
        prefs.edit()
                .remove(KEY_API_TOKEN)
                .remove(KEY_API_TOKEN_CREATED_AT)
                .apply();
    }
}
