package com.louis.app.cavity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BouteilleDAO {

    @Insert
    void insert(Bouteille bou);

    @Update
    void update(Bouteille bou);

    @Delete
    void delete(Bouteille bou);

    // Retourne le nombre total de bouteilles
    @Query("SELECT SUM(nombre) AS prix_total FROM bouteille_table")
    LiveData<Integer> getTotalBouteille();

    // Retourne les bouteilles associées à un vin
    @Query("SELECT * FROM bouteille_table WHERE vin_id=:idVin ORDER BY millesime")
    LiveData<List<Bouteille>> getBouteilleByVin(long idVin);

    // Retourne les bouteilles dont l'apogée est atteinte
    @Query("SELECT * FROM bouteille_table WHERE apogee <= :year")
    LiveData<List<Bouteille>> getBouteillesToDrink(int year);

    // Retourne les bouteilles favorites
    @Query("SELECT * FROM bouteille_table WHERE fav = 1")
    LiveData<List<Bouteille>> getBouteillesFavorites();

    // Retourne les lieux d'achat des bouteilles
    @Query("SELECT lieuxAchat FROM bouteille_table")
    LiveData<List<String>> getPurchaseLocation();

    // Retourne la bouteille d'un certain id
    @Query("SELECT * FROM bouteille_table WHERE id=:idBou")
    LiveData<Bouteille> getBouteilleById(long idBou);

    // Ajoute les bouteilles d'un certain vin_id dans la table temp
    @Query("INSERT INTO temp_table SELECT * FROM bouteille_table WHERE vin_id=:idVin")
    void insertBouInTemp(long idVin);

    // Retourne le nombre de bouteilles par vins
    @Query("SELECT SUM(nombre) AS nb_vin FROM bouteille_table WHERE vin_id=:idVin")
    LiveData<Integer> getBouteilleCountByIdVin(long idVin);

    // Retourne les bouteilles les mieux notées
}
