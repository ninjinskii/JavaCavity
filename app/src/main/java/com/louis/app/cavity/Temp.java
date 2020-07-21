package com.louis.app.cavity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/* Tampon pour supression de vin. Garde les bouteilles en attendant */
@Entity(tableName = "temp_table")
public class Temp {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private int vin_id, apogee;
    private boolean fav = false;
    private String nom, nombre, millesime, commentaire, prixAchat, lieuxAchat,
            dateAchat, distinction, pdfPath, imgPath, commentaireNote;

    public Temp(String nom, String nombre, String millesime, String prixAchat, String lieuxAchat,
                    String dateAchat, String distinction, int vin_id, String commentaire,
                    String commentaireNote, String pdfPath, String imgPath, boolean fav) {
        this.nom = nom;
        this.nombre = nombre;
        this.millesime = millesime;
        this.commentaire = commentaire;
        this.prixAchat = prixAchat;
        this.lieuxAchat = lieuxAchat;
        this.dateAchat = dateAchat;
        this.distinction = distinction;
        this.commentaireNote = commentaireNote;
        this.pdfPath = pdfPath;
        this.imgPath = imgPath;
        this.vin_id = vin_id;
        this.fav = fav;
    }

    public String getMillesime() { return millesime; }

    public String getNom() { return nom; }

    public String getNombre() { return nombre; }

    public String getCommentaire() { return commentaire; }

    public String getPrixAchat() { return prixAchat; }

    public String getLieuxAchat() { return lieuxAchat; }

    public String getDateAchat() { return dateAchat; }

    public String getDistinction() { return distinction; }

    public String getPdfPath() { return pdfPath; }

    public String getImgPath() { return imgPath; }

    public int getApogee() { return apogee; }

    public String getCommentaireNote() { return commentaireNote; }

    public String getDistinctionPure(){ return distinction.substring(1); }

    public void setNom(String nom) { this.nom = nom; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public void setImgPath(String imgPath) { this.imgPath = imgPath; }

    public void setApogee(int apogee) { this.apogee = apogee; }

    public void setMillesime(String millesime) { this.millesime = millesime; }

    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public void setPrixAchat(String prixAchat) { this.prixAchat = prixAchat; }

    public void setLieuxAchat(String lieuxAchat) { this.lieuxAchat = lieuxAchat; }

    public void setDateAchat(String dateAchat) { this.dateAchat = dateAchat; }

    public void setDistinction(String distinction) { this.distinction = distinction; }

    public void setCommentaireNote(String commentaireNote) { this.commentaireNote = commentaireNote; }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public int getVin_id() { return vin_id; }

    public void setVin_id(int idVin) { this.vin_id = idVin; }

    public boolean isFav() { return fav; }

    public void setFav(boolean fav) { this.fav = fav; }
}