package com.louis.app.cavity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.louis.app.cavity.MainActivity.DB_NAME;

public class ActivityParametres extends AppCompatActivity {

    public static final String PREFS_DEVISE = "com.louis.app.cavity.PREFS_DEVISE";
    public static final String PREFS_NIGHT = "com.louis.app.cavity.PREFS_NIGHT";
    public static final String PREFS_TRI = "com.louis.app.cavity.PREFS_TRI";
    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    private CoordinatorLayout mCoordinatorLayout;
    private SharedPreferences prefs;
    private RelativeLayout rlNight;
    private RelativeLayout rlBDI;
    private RelativeLayout rlTri;
    private RelativeLayout rlBD;
    private RadioButton rb;
    private String devise;
    private Switch sNight;
    private boolean night;
    private Switch sTri;
    private String tri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkThemeCavity);
        } else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.PAR_title));

        findViews();

        // Récupération des préférences
        prefs = this.getSharedPreferences("com.louis.app.cavity", Context.MODE_PRIVATE);
        devise = prefs.getString(PREFS_DEVISE, " €");
        tri = prefs.getString(PREFS_TRI, "region");
        night = prefs.getBoolean(PREFS_NIGHT, false);

        // Actualisation des vues selon préférences
        switch(devise) {
            case " .-":
                rb = findViewById(R.id.PAR_radioF);
                rb.setChecked(true);
                break;

            case " £":
                rb = findViewById(R.id.PAR_radioL);
                rb.setChecked(true);
                break;

            case " $":
                rb = findViewById(R.id.PAR_radioD);
                rb.setChecked(true);
                break;

            default:
                rb = findViewById(R.id.PAR_radioE);
                rb.setChecked(true);
                break;
        }

        if(tri.equals("couleur")) {
            sTri.setChecked(true);
        } else{
            sTri.setChecked(false);
        }

        if(night){
            sNight.setChecked(true);
        } else{
            sNight.setChecked(false);
        }

        // Listeners
        rlBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveDatabase();
            }
        });

        rlBDI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mCoordinatorLayout, R.string.PAR_warningBD, Snackbar.LENGTH_INDEFINITE).setAction(R.string.PAR_warningOKBD, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(mCoordinatorLayout, R.string.PAR_warningBD2, Snackbar.LENGTH_INDEFINITE).setAction(R.string.PAR_warningOKBD, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPopulatedDatabase();
                            }
                        }).setDuration(8000).show();
                    }
                }).setDuration(8000).show();
            }
        });

        rlTri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sTri.setChecked(!sTri.isChecked());
            }
        });

        rlNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sNight.setChecked(!sNight.isChecked());
            }
        });
    }

    private void findViews(){
        mCoordinatorLayout = findViewById(R.id.PAR_coordinator);
        rlNight = findViewById(R.id.PAR_relativeLNight);
        sNight = findViewById(R.id.PAR_switchNightMode);
        rlTri = findViewById(R.id.PAR_relativeLTri);
        rlBDI = findViewById(R.id.PAR_relativeLBDI);
        rlBD = findViewById(R.id.PAR_relativeLBD);
        sTri = findViewById(R.id.PAR_switchTri);
    }

    /* Copie une BD dans le dossier assets dans le stockage interne de l'appli
    Utilisant Room, il faut aussi copier les ficher -shm, -wal pour ouvrir la BD

    2éme version: permet de copier la BD contenue dans le fichier de stockage externe adns le stockage interne
    */
    private void setPopulatedDatabase(){

        if(checkPermissionExternalStrorage()){
            return;
        }

        // vin_database, vin_database-shm, vin_database-wal
        File f1 = new File(getExternalFilesDir(null), DB_NAME);
        File f2 = new File(getExternalFilesDir(null), DB_NAME + "-shm");
        File f3 = new File(getExternalFilesDir(null), DB_NAME + "-wal");

        writeDatabaseFile(f1);
        writeDatabaseFile(f2);
        writeDatabaseFile(f3);
    }

    // Prend en paramètre un des trois fichiers formant une base de données Room
    private void writeDatabaseFile(File f){
        OutputStream os;
        InputStream is;

        try {

            //is = this.getAssets().open(f.getName()); //1ere version
            is = new FileInputStream(f);
            os = new FileOutputStream(this.getDatabasePath(f.getName()));

            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            os.close();
            is.close();

            Snackbar.make(mCoordinatorLayout, R.string.MAIN_BDimportee, Snackbar.LENGTH_INDEFINITE).setDuration(8000).show();

        } catch (IOException e){
            Snackbar.make(mCoordinatorLayout, R.string.MAIN_erreurCopieBD, Snackbar.LENGTH_LONG).show();
        }
    }

    // Exporte une copie de la BD dans le stockage externe, accessible à l'utilisateur
    private void retrieveDatabase(){

        if(checkPermissionExternalStrorage()){
            return;
        }

        File f1 = new File(getExternalFilesDir(null), DB_NAME);
        File f2 = new File(getExternalFilesDir(null), DB_NAME + "-shm");
        File f3 = new File(getExternalFilesDir(null), DB_NAME + "-wal");

        try{

            // vin_database
            InputStream is = new FileInputStream(getDatabasePath(DB_NAME));
            OutputStream os = new FileOutputStream(f1);

            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // vin_database-shm
            is = new FileInputStream(getDatabasePath(DB_NAME + "-shm"));
            os = new FileOutputStream(f2);

            data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // vin_database-wal
            is = new FileInputStream(getDatabasePath(DB_NAME + "-wal"));
            os = new FileOutputStream(f3);

            data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            Snackbar.make(mCoordinatorLayout, R.string.PAR_BDExportee, Snackbar.LENGTH_LONG).show();

        } catch(IOException e){
            Snackbar.make(mCoordinatorLayout, R.string.PAR_BDExporteeErr, Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean checkPermissionExternalStrorage(){
        int permission = ContextCompat.checkSelfPermission(ActivityParametres.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityParametres.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);

            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        RadioButton rbE = findViewById(R.id.PAR_radioE);
        RadioButton rbF = findViewById(R.id.PAR_radioF);
        RadioButton rbL = findViewById(R.id.PAR_radioL);
        RadioButton rbD = findViewById(R.id.PAR_radioD);
        SharedPreferences.Editor editor = prefs.edit();
        Switch s = findViewById(R.id.PAR_switchTri);


        if(rbE.isChecked()){
            editor.putString(PREFS_DEVISE, " €");
        }

        if(rbF.isChecked()){
            editor.putString(PREFS_DEVISE, " .-");
        }

        if(rbL.isChecked()){
            editor.putString(PREFS_DEVISE, " £");
        }

        if(rbD.isChecked()){
            editor.putString(PREFS_DEVISE, " $");
        }

        if(s.isChecked()){
            editor.putString(PREFS_TRI, "couleur");
        } else{
            editor.putString(PREFS_TRI, "region");
        }

        if(sNight.isChecked()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean(PREFS_NIGHT, true);
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean(PREFS_NIGHT, false);
        }

        editor.apply();

        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
