package com.example.securestoragejava.model;

/*
 * Auteur: Lemghili Mohammed Amine
 * Modele simple utilise pour la sauvegarde JSON.
 */

/**
 * Classe de modèle représentant un étudiant.
 * Utilisée pour démontrer la sérialisation et désérialisation JSON dans le stockage interne.
 */
public class Student {
    /** Identifiant unique de l'étudiant */
    public final int id;
    
    /** Nom de l'étudiant */
    public final String name;
    
    /** Âge de l'étudiant */
    public final int age;

    /**
     * Constructeur pour créer un nouvel objet étudiant.
     * 
     * @param id Identifiant.
     * @param name Nom complet.
     * @param age Âge en années.
     */
    public Student(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
