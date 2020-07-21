package com.louis.app.cavity;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Couche supplémentaire entre les données et le ViewModel (Architecture MVVM)
// pour que ce dernier n'ai pas à se préoccuper de la source des données (BD, Internet...)
public class VinRepository {
    private VinDAO vinDao;
    private BouteilleDAO bouDao;
    private TempDAO tempDao;

    public VinRepository(Application application){
        VinDatabase db = VinDatabase.getInstance(application);
        vinDao = db.vinDao();
        bouDao = db.bouDao();
        tempDao = db.tempDao();
    }


    // Vin___________________________________________________________
    public void insert(Vin vin){
        new InsertVinAsyncTask(vinDao).execute(vin);
    }
    public void update(Vin vin){
        new UpdateVinAsyncTask(vinDao).execute(vin);
    }
    public void delete(Vin vin){
        new DeleteVinAsyncTask(vinDao).execute(vin);
    }

    public LiveData<List<Vin>> getAllVins(){ return vinDao.getAllVins(); }
    public LiveData<List<Vin>> getAllVinsByCouleur(){ return vinDao.getAllVinsByCouleur(); }
    public LiveData<List<String>> getAllRegions(){ return vinDao.getAllRegions(); }
    public void setBouteilleCount(long idVin, long nb){ new SetCountVinAsyncTask(vinDao).execute(idVin, nb); }
    public LiveData<Vin> getVinById(long idVin){ return vinDao.getVinById(idVin); }
    public LiveData<List<Vin>> getVinBySearchQuery(String query){ return vinDao.getVinBySearchQuery(query);}




    // Bouteilles____________________________________________________
    public void insert(Bouteille bou){ new InsertBouAsyncTask(bouDao).execute(bou); }
    public void update(Bouteille bou){ new UpdateBouAsyncTask(bouDao).execute(bou); }
    public void delete(Bouteille bou){ new DeleteBouAsyncTask(bouDao).execute(bou); }

    public LiveData<Integer> getTotalBouteilles(){ return bouDao.getTotalBouteille(); }
    public LiveData<List<Bouteille>> getBouteilleByVin(long vinId){ return bouDao.getBouteilleByVin(vinId); }
    public LiveData<List<Bouteille>> getBouteillesToDrink(int year){ return bouDao.getBouteillesToDrink(year); }
    public LiveData<List<Bouteille>> getBouteillesFavorites(){ return bouDao.getBouteillesFavorites(); }
    public LiveData<List<String>> getPurchaseLocation(){ return bouDao.getPurchaseLocation(); }
    public void insertBouInTemp(long vinId){new InsertBouInTempAsyncTask(bouDao).execute(vinId); }
    public LiveData<Bouteille> getBouteilleById(long idBou){ return bouDao.getBouteilleById(idBou); }
    public LiveData<Integer> getBouteilleCountByIdVin(long idVin){ return bouDao.getBouteilleCountByIdVin(idVin); }




        // Temp__________________________________________________________
    // Copie la table générée de bouteilles liées au vin supprimé dans la table des bouteilles
    public void copyTemp(){ new CopyTempAsyncTask(tempDao).execute(); }
    public void insert(Temp temp){ new InsertTempAsyncTask(tempDao).execute(temp); }
    public void dropTableTemp(){ new DropTableAsyncTask(tempDao).execute(); }


    // Création de tâches asynchrone pour modifier la BD_____________
    // Vins
    private static class InsertVinAsyncTask extends AsyncTask<Vin, Void, Void>{
        private VinDAO vinDao;

        public InsertVinAsyncTask(VinDAO vinDao) {
            this.vinDao = vinDao;
        }

        @Override
        protected Void doInBackground(Vin... vins) {
            vinDao.insert(vins[0]);
            return null;
        }
    }

    private static class UpdateVinAsyncTask extends AsyncTask<Vin, Void, Void>{
        private VinDAO vinDao;

        public UpdateVinAsyncTask(VinDAO vinDao) {
            this.vinDao = vinDao;
        }

        @Override
        protected Void doInBackground(Vin... vins) {
            vinDao.update(vins[0]);
            return null;
        }
    }

    private static class DeleteVinAsyncTask extends AsyncTask<Vin, Void, Void>{
        private VinDAO vinDao;

        public DeleteVinAsyncTask(VinDAO vinDao) {
            this.vinDao = vinDao;
        }

        @Override
        protected Void doInBackground(Vin... vins) {
            vinDao.delete(vins[0]);
            return null;
        }
    }

    private static class SetCountVinAsyncTask extends AsyncTask<Long, Void, Void>{
        private VinDAO vinDao;

        public SetCountVinAsyncTask(VinDAO vinDao) {
            this.vinDao = vinDao;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            int nb = (int) (long) longs[1];
            vinDao.setBouteilleCount(longs[0], nb);
            return null;
        }
    }


    // Bouteilles
    private static class InsertBouAsyncTask extends AsyncTask<Bouteille, Void, Void>{
        private BouteilleDAO bouDao;

        public InsertBouAsyncTask(BouteilleDAO bouDao) {
            this.bouDao = bouDao;
        }

        @Override
        protected Void doInBackground(Bouteille... bouteilles) {
            bouDao.insert(bouteilles[0]);
            return null;
        }
    }

    private static class UpdateBouAsyncTask extends AsyncTask<Bouteille, Void, Void>{
        private BouteilleDAO bouDao;

        public UpdateBouAsyncTask(BouteilleDAO bouDao) {
            this.bouDao = bouDao;
        }

        @Override
        protected Void doInBackground(Bouteille... bouteilles) {
            bouDao.update(bouteilles[0]);
            return null;
        }
    }

    private static class DeleteBouAsyncTask extends AsyncTask<Bouteille, Void, Void>{
        private BouteilleDAO bouDao;

        public DeleteBouAsyncTask(BouteilleDAO bouDao) {
            this.bouDao = bouDao;
        }

        @Override
        protected Void doInBackground(Bouteille... bouteilles) {
            bouDao.delete(bouteilles[0]);
            return null;
        }
    }

    private static class InsertBouInTempAsyncTask extends AsyncTask<Long, Void, Void>{
        private BouteilleDAO bouDao;

        public InsertBouInTempAsyncTask(BouteilleDAO bouDao) {
            this.bouDao = bouDao;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            bouDao.insertBouInTemp(longs[0]);
            return null;
        }
    }


    // Temp
    private static class InsertTempAsyncTask extends AsyncTask<Temp, Void, Void>{
        private TempDAO tempDao;

        public InsertTempAsyncTask(TempDAO tempDao) {
            this.tempDao = tempDao;
        }

        @Override
        protected Void doInBackground(Temp... temps) {
            tempDao.insert(temps[0]);
            return null;
        }
    }

    private static class CopyTempAsyncTask extends AsyncTask<Void, Void, Void>{
        private TempDAO tempDao;

        public CopyTempAsyncTask(TempDAO tempDao) {
            this.tempDao = tempDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tempDao.copyTable();
            return null;
        }
    }

    private static class DropTableAsyncTask extends AsyncTask<Void, Void, Void>{
        private TempDAO tempDao;

        public DropTableAsyncTask(TempDAO tempDao) {
            this.tempDao = tempDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            tempDao.dropTableTemp();
            return null;
        }
    }

}
