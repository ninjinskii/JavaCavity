package com.louis.app.cavity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vin_table")
public class Vin {

    @PrimaryKey(autoGenerate = true)
    private long id;

    // Champs présents dans la BD
    private String region, couleur, appellation, nom;
    private int nombre;

    public Vin(String region, String couleur, String appellation, String nom){
        this.nombre = 0;
        this.region = region;
        this.couleur = couleur;
        this.appellation = appellation;
        this.nom = nom;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getRegion() { return region; }

    public String getCouleur() { return couleur; }

    public String getAppellation() { return appellation; }

    public String getNom() { return nom; }

    public void setRegion(String region) { this.region = region; }

    public int getNombre() { return nombre; }

    public void setNombre(int nombre) { this.nombre = nombre; }
}
