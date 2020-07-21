package com.louis.app.cavity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class VinViewModel extends AndroidViewModel {
    private VinRepository repository;

    public VinViewModel(@NonNull Application application) {
        super(application);

        repository = new VinRepository(application);
    }




    // Vin_____________________________________________________________
    public void insert(Vin vin){
        repository.insert(vin);
    }
    public void update(Vin vin){
        repository.update(vin);
    }
    public void delete(Vin vin){
        repository.delete(vin);
    }

    public LiveData<List<Vin>> getAllVin() { return repository.getAllVins(); }
    public LiveData<List<Vin>> getAllVinByCouleur() { return repository.getAllVinsByCouleur(); }
    public LiveData<List<String>> getAllRegions() { return repository.getAllRegions(); }
    public void setBouteilleCount(long idVin, long nb){ repository.setBouteilleCount(idVin, nb); }
    public LiveData<Vin> getVinById(long idVin){ return repository.getVinById(idVin); }
    public LiveData<List<Vin>> getVinBySearchQuery(String query){ return repository.getVinBySearchQuery(query); }




    // Bouteille________________________________________________________
    public void insert(Bouteille bou){ repository.insert(bou); }
    public void update(Bouteille bou){
        repository.update(bou);
    }
    public void delete(Bouteille bou){
        repository.delete(bou);
    }

    public LiveData<Integer> getTotalBouteilles(){ return repository.getTotalBouteilles(); }
    public LiveData<List<Bouteille>> getBouteillesByVin(long vinId){ return repository.getBouteilleByVin(vinId); }
    public LiveData<List<Bouteille>> getBouteillesToDrink(int year) { return repository.getBouteillesToDrink(year); }
    LiveData<List<Bouteille>> getBouteillesFavorites(){ return repository.getBouteillesFavorites(); }
    public LiveData<List<String>> getPurchaseLocation() { return repository.getPurchaseLocation(); }
    public void insertBouInTemp(long vinId){ repository.insertBouInTemp(vinId);}
    public LiveData<Bouteille> getBouteilleById(long idBou){ return repository.getBouteilleById(idBou); }
    public LiveData<Integer> getBouteilleCountByIdVin(long idVin){ return repository.getBouteilleCountByIdVin(idVin); }




    // Temp____________________________________________________________
    public void insert(Temp temp){ repository.insert(temp); }
    public void copyTemp(){ repository.copyTemp(); }
    public void dropTablTemp(){ repository.dropTableTemp(); }

}
