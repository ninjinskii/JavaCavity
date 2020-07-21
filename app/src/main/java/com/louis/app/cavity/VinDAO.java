package com.louis.app.cavity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VinDAO {

    @Insert
    void insert(Vin vin);

    @Update
    void update(Vin vin);

    @Delete
    void delete(Vin vin);

    // Liste des vins rangé par ordre alphabétique
    @Query("SELECT * FROM vin_table ORDER BY region, appellation")
    LiveData<List<Vin>> getAllVins();                        // LiveData nous permet d'observer cette liste de Vins.

    // Liste des vins rangé par ordre alphabétique
    @Query("SELECT * FROM vin_table ORDER BY couleur, appellation")
    LiveData<List<Vin>> getAllVinsByCouleur();

    @Query("SELECT region FROM vin_table")
    LiveData<List<String>> getAllRegions();

    // Actualise le nombre de bouteilles par vin
    @Query("UPDATE vin_table SET nombre=:nb WHERE id=:idVin")
    void setBouteilleCount(long idVin, int nb);

    // Retourne un vin selon son id
    @Query("SELECT * FROM vin_table WHERE id=:idVin")
    LiveData<Vin> getVinById(long idVin);

    // Retourne une liste de vin possédant un pattern précis
    @Query("SELECT * FROM vin_table WHERE nom LIKE :query OR appellation LIKE :query ORDER BY region, appellation")
    LiveData<List<Vin>> getVinBySearchQuery(String query);
}
