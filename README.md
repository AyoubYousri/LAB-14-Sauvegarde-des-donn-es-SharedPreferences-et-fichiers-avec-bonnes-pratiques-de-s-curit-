# LAB-14-Sauvegarde-des-données-SharedPreferences-et-fichiers-avec-bonnes-pratiques-de-sécurité 🚀

Ce projet est une application Android de démonstration axée sur les différentes méthodes de stockage de données sécurisées et persistantes. Il a été modernisé pour offrir une interface utilisateur élégante et des performances optimales en suivant les meilleures pratiques Android.

## ✨ Caractéristiques principales

- **Interface Utilisateur Moderne** : Design basé sur Material Design avec des couleurs vives, des bordures arrondies et une ergonomie améliorée.
- **Gestion du Thème** : Support dynamique des thèmes clair et sombre, persisté via les préférences.
- **Sécurité des Données** :
    - **EncryptedSharedPreferences** : Stockage chiffré des jetons (tokens) API utilisant Android Keystore (Jetpack Security).
    - **SharedPreferences Standards** : Gestion des préférences non sensibles (nom, langue, thème).
- **Stockage de Fichiers** :
    - **Interne (JSON)** : Sérialisation et sauvegarde d'une liste d'étudiants dans un fichier JSON privé.
    - **Interne (Texte)** : Utilisation de fichiers UTF-8 pour les notes rapides.
    - **Externe App-Specific** : Exportation de fichiers vers le stockage externe sans nécessiter de permissions complexes.
- **Gestion du Cache** : Stockage temporaire et fonction de purge complète.

## 🛠️ Points Techniques

- **Multi-threading** : Utilisation d'un `ExecutorService` pour effectuer les opérations de lecture/écriture en arrière-plan, garantissant une UI fluide (pas de freeze).
- **Documentation Détaillée** : Chaque classe et méthode est documentée avec des commentaires Javadoc pour faciliter la compréhension.
- **Bonnes Pratiques** : Séparation des responsabilités, gestion des erreurs et interface utilisateur réactive.

---
<img width="686" height="1437" alt="Capture d&#39;écran 2026-05-09 204108" src="https://github.com/user-attachments/assets/c2541c4c-0481-4e67-b94d-5b895dbec68d" />

