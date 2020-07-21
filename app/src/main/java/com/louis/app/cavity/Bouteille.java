package com.louis.app.cavity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/* Une bouteille est un vin avec des caractéristiques propres */
@Entity(indices = {@Index("vin_id")},
        tableName = "bouteille_table",
        foreignKeys = @ForeignKey(entity = Vin.class, parentColumns = "id",
                childColumns = "vin_id", onDelete = CASCADE))
public class Bouteille {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long vin_id;
    private int apogee;
    private boolean fav = false;
    private String nom, nombre, millesime, commentaire, prixAchat, lieuxAchat,
            dateAchat, distinction, pdfPath, imgPath, commentaireNote;

    public Bouteille(String nom, String nombre, String millesime, String prixAchat, String lieuxAchat,
                     String dateAchat, String distinction, long vin_id) {
        this.nom = nom;
        this.nombre = nombre;
        this.millesime = millesime;
        this.commentaire = " ";
        this.prixAchat = prixAchat;
        this.lieuxAchat = lieuxAchat;
        this.dateAchat = dateAchat;
        this.distinction = distinction;
        this.commentaireNote = " ";
        this.vin_id = vin_id;
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

    public long getVin_id() { return vin_id; }

    public void setVin_id(int idVin) { this.vin_id = idVin; }

    public boolean isFav() { return fav; }

    public void setFav(boolean fav) { this.fav = fav; }
}