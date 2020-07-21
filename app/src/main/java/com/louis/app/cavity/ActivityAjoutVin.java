package com.louis.app.cavity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static com.louis.app.cavity.MainActivity.COULEUR_VIN_BLANC;
import static com.louis.app.cavity.MainActivity.COULEUR_VIN_LIQUOREUX;
import static com.louis.app.cavity.MainActivity.COULEUR_VIN_ROSE;
import static com.louis.app.cavity.MainActivity.COULEUR_VIN_ROUGE;
import static com.louis.app.cavity.MainActivity.mListeRegion;

public class ActivityAjoutVin extends AppCompatActivity {

    public static final String EXTRA_ID = "com.louis.app.cavity.EXTRA_ID";
    public static final String EXTRA_REGION = "com.louis.app.cavity.EXTRA_REGION";
    public static final String EXTRA_APPELLATION = "com.louis.app.cavity.EXTRA_APPELLATION";
    public static final String EXTRA_NOM = "com.louis.app.cavity.EXTRA_NOM";
    public static final String EXTRA_COULEUR = "com.louis.app.cavity.EXTRA_COULEUR";

    CoordinatorLayout mCoordinatorLayout;
    TextInputAutoCompleteTextView actvRegion;
    EditText etAppellation;
    EditText etNom;
    RadioGroup rgCouleur;
    Button bAjouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkThemeCavity);
        } else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_vin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        findViews();
        checkForEdit(actionBar);

        ArrayAdapter<String> adapteur = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListeRegion);
        actvRegion.setAdapter(adapteur);

        bAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVin();
            }
        });
    }

    private void saveVin(){
        String region = actvRegion.getText().toString();
        String appellation = etAppellation.getText().toString();
        String nom = etNom.getText().toString();
        String couleur = "#000000";

        if(rgCouleur.getCheckedRadioButtonId() != -1) {
            RadioButton rbBlanc = findViewById(R.id.AJOUTVIN_radioBlanc);
            RadioButton rbRouge = findViewById(R.id.AJOUTVIN_radioRouge);
            RadioButton rbRose = findViewById(R.id.AJOUTVIN_radioRose);
            RadioButton rbLiqu = findViewById(R.id.AJOUTVIN_radioLiquoreux);

            if(rbBlanc.isChecked()){
                couleur = COULEUR_VIN_BLANC;
            } else if(rbRouge.isChecked()){
                couleur = COULEUR_VIN_ROUGE;
            } else if(rbRose.isChecked()){
                couleur = COULEUR_VIN_ROSE;
            } else{
                couleur = COULEUR_VIN_LIQUOREUX;
            }
        }

        if(region.trim().isEmpty()){
            actvRegion.setError("Champ requis");
            return;
        }

        if(appellation.trim().isEmpty()){
            etAppellation.setError("Champ requis");
            return;
        }

        if(nom.trim().isEmpty()){
            etNom.setError("Champ requis");
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_REGION, region);
        data.putExtra(EXTRA_APPELLATION, appellation);
        data.putExtra(EXTRA_NOM, nom);
        data.putExtra(EXTRA_COULEUR, couleur);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);

        if(id != -1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    private void findViews(){
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.AJOUTVIN_coordinator);
        actvRegion = (TextInputAutoCompleteTextView) findViewById(R.id.AJOUTVIN_entrerRegion);
        etAppellation = (EditText) findViewById(R.id.AJOUTVIN_entrerAppellation);
        etNom = (EditText) findViewById(R.id.AJOUTVIN_entrerNom);
        rgCouleur = (RadioGroup) findViewById(R.id.AJOUTVIN_radioCouleur);
        bAjouter = (Button) findViewById(R.id.AJOUTVIN_boutonAjouter);
    }

    private void checkForEdit(ActionBar actionBar){
        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)){
            String couleur = intent.getStringExtra(EXTRA_COULEUR);
            RadioButton rBou;

            actionBar.setTitle(R.string.AJOUTVIN_appBar2);
            actvRegion.setText(intent.getStringExtra(EXTRA_REGION));
            etAppellation.setText(intent.getStringExtra(EXTRA_APPELLATION));
            etNom.setText(intent.getStringExtra(EXTRA_NOM));
            bAjouter.setText(R.string.AJOUTVIN_boutonAjouter2);

            switch(couleur){
                case COULEUR_VIN_ROSE:
                    rBou = findViewById(R.id.AJOUTVIN_radioRose);
                    break;

                case COULEUR_VIN_ROUGE:
                    rBou = findViewById(R.id.AJOUTVIN_radioRouge);
                    break;

                case COULEUR_VIN_LIQUOREUX:
                    rBou = findViewById(R.id.AJOUTVIN_radioLiquoreux);
                    break;

                default:
                    rBou = findViewById(R.id.AJOUTVIN_radioBlanc);
                    break;
            }
            rBou.setChecked(true);

        } else{
            actionBar.setTitle(R.string.AJOUTVIN_appBar1);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
