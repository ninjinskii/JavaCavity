package com.louis.app.cavity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static com.louis.app.cavity.MainActivity.COULEUR_VIN_BLANC;
import static com.louis.app.cavity.MainActivity.COULEUR_VIN_ROUGE;

@Database(entities = {Vin.class, Bouteille.class, Temp.class}, version = 3, exportSchema = false)
public abstract class VinDatabase extends RoomDatabase {

    private static VinDatabase instance;

    // Room s'occupe de ces fonctions automatiquement
    public abstract VinDAO vinDao();
    public abstract BouteilleDAO bouDao();
    public abstract TempDAO tempDao();

    // Singleton
    static synchronized VinDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    VinDatabase.class, "vin_database")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    //.addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    // Migrations
    private static final Migration MIGRATION_1_2 = new Migration(1, 2){
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3){
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX index_bouteille_table_vin_id ON bouteille_table(vin_id)");
        }
    };

    // Mock data
    /*private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new GenererMockAsyncTask(instance).execute();
        }
    };*/

    /*private static class GenererMockAsyncTask extends AsyncTask<Void, Void, Void>{
        private VinDAO vinDao;
        private BouteilleDAO bouDao;

        private GenererMockAsyncTask(VinDatabase db){
            vinDao = db.vinDao();
            bouDao = db.bouDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            vinDao.insert(new Vin("Alsace", COULEUR_VIN_BLANC, "Riesling", "Un riesling incroyable"));
            vinDao.insert(new Vin("Alsace", COULEUR_VIN_BLANC, "Riesling", "Un autre riesling"));
            vinDao.insert(new Vin("Alsace", COULEUR_VIN_BLANC, "Pinot Gris", "Un Pinot Gris"));
            vinDao.insert(new Vin("Bordeaux", COULEUR_VIN_ROUGE,   "Saint-Emilion", "Un Saint-Emilion"));
            vinDao.insert(new Vin("Bordeaux", COULEUR_VIN_ROUGE, "Saint-Emilion", "Un autre Saint Emilion"));
            vinDao.insert(new Vin("Alsace", COULEUR_VIN_BLANC, "Riesling", "Un riesling incroyable"));
            vinDao.insert(new Vin("Alsace", COULEUR_VIN_BLANC, "Riesling", "Un autre riesling"));
            vinDao.insert(new Vin("Alsace", COULEUR_VIN_BLANC, "Pinot Gris", "Un Pinot Gris"));
            vinDao.insert(new Vin("Bordeaux", COULEUR_VIN_ROUGE,   "Saint-Emilion", "Un Saint-Emilion"));
            vinDao.insert(new Vin("Bordeaux", COULEUR_VIN_ROUGE, "Saint-Emilion", "Un autre Saint Emilion"));

            bouDao.insert(new Bouteille("Nom1", "1", "1995", "10", "Lure1", "01/01/2000", "D1", 1));
            bouDao.insert(new Bouteille("Nom2", "2", "1996", "11", "Lure2", "01/01/2000", "A" + "#C0C0C0", 2));
            bouDao.insert(new Bouteille("Nom3", "3", "1997", "12", "Lure3", "01/01/2000", "D3", 1));
            bouDao.insert(new Bouteille("Nom4", "4", "1998", "13", "Lure4", "01/01/2000", "B10", 2));
            bouDao.insert(new Bouteille("Nom5", "5", "1999", "10", "Lure5", "01/01/2000", "D1", 1));

            return null;
        }
    }*/
}
