package com.louis.app.cavity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.louis.app.cavity.ActivityBouteille.COULEUR_MEDAILLE_ARGENT;
import static com.louis.app.cavity.ActivityBouteille.COULEUR_MEDAILLE_BRONZE;
import static com.louis.app.cavity.ActivityBouteille.COULEUR_MEDAILLE_OR;
import static com.louis.app.cavity.ActivityBouteille.mListeLieux;
import static com.louis.app.cavity.MainActivity.ANNEE;

public class ActivityAjoutBouteille extends AppCompatActivity {
    public static final String EXTRA_ID_BOU = "com.louis.app.cavity.EXTRA_ID_BOU";
    public static final String EXTRA_MILLESIME = "com.louis.app.cavity.EXTRA_MILLESIME";
    public static final String EXTRA_APOGEE = "com.louis.app.cavity.EXTRA_APOGEE";
    public static final String EXTRA_DATE = "com.louis.app.cavity.EXTRA_DATE";
    public static final String EXTRA_PRIX = "com.louis.app.cavity.EXTRA_PRIX";
    public static final String EXTRA_NOMBRE = "com.louis.app.cavity.EXTRA_NOMBRE";
    public static final String EXTRA_LIEUX = "com.louis.app.cavity.EXTRA_LIEUX";
    public static final String EXTRA_DISTINCTION = "com.louis.app.cavity.EXTRA_DISTINCTION";
    public static final String EXTRA_COMMENTAIREDIS = "com.louis.app.cavity.EXTRA_COMMENTAIREDIS";
    public static final String EXTRA_PDF = "com.louis.app.cavity.EXTRA_PDF";
    public static final String EXTRA_IMAGE = "com.louis.app.cavity.EXTRA_IMAGE";
    public static final String EXTRA_COMMENTAIRE = "com.louis.app.cavity.EXTRA_COMMENTAIRE";
    public static final String EXTRA_FAVORI = "com.louis.app.cavity.EXTRA_FAVORI";

    public final int PICKFILE_RESULT_CODE = 0;

    final Calendar myCalendar = Calendar.getInstance();
    private boolean isImgOk = false;
    private boolean isPdfOk = false;
    private boolean fav = false;
    private Uri filePathPdf;
    private Uri filePathImg;

    private CoordinatorLayout mCoordinatorLayout;

    private TextInputAutoCompleteTextView etLieuxAchat;
    private TextInputEditText etCommentaireDis;
    private TextInputEditText etCommentaire;
    private TextInputEditText etNombre;
    private TextInputEditText etPrix;
    private RadioGroup rgDistinction;
    private RadioGroup rgEtoile;
    private RadioGroup rgMed;
    private NumberPicker npApogee;
    private NumberPicker npMil;
    private ImageButton ibImg;
    private ImageButton ibPdf;
    private Button bValider;
    private TextView etNote;
    private EditText etDate;
    private TextView tvImg;
    private TextView tvPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkThemeCavity);
        } else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_bouteille);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        findViews();

        npApogee.setMaxValue(ANNEE + 100);
        npApogee.setMinValue(ANNEE - 20);
        npApogee.setValue(ANNEE);

        npMil.setMaxValue(ANNEE + 20);
        npMil.setMinValue(ANNEE - 100);
        npMil.setValue(ANNEE);

        checkForEdit(actionBar);
        setListeners();

        ArrayAdapter<String> adapteur = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListeLieux);
        etLieuxAchat.setAdapter(adapteur);
    }

    private void findViews(){
        mCoordinatorLayout = findViewById(R.id.AJOUTBOU_coordinator);
        etLieuxAchat = (TextInputAutoCompleteTextView) findViewById(R.id.AJOUTBOU_entrerLieux);
        etCommentaireDis = (TextInputEditText) findViewById(R.id.AJOUTBOU_commentaireDis);
        etCommentaire = (TextInputEditText) findViewById(R.id.AJOUTBOU_commentaire);
        etNombre = (TextInputEditText) findViewById(R.id.AJOUTBOU_entrerNombre);
        rgDistinction = (RadioGroup) findViewById(R.id.AJOUTBOU_radioGroupDistinc);
        rgMed = (RadioGroup) findViewById(R.id.AJOUTBOU_radioGroupMed);
        rgEtoile = (RadioGroup) findViewById(R.id.AJOUTBOU_radioGroupEtoiles);
        etPrix = (TextInputEditText) findViewById(R.id.AJOUTBOU_entrerPrix);
        npApogee = (NumberPicker) findViewById(R.id.AJOUTBOU_entrerApogee);
        npMil = (NumberPicker) findViewById(R.id.AJOUTBOU_entrerMil);
        ibImg = (ImageButton) findViewById(R.id.AJOUTBOU_boutonImg);
        ibPdf = (ImageButton) findViewById(R.id.AJOUTBOU_boutonPdf);
        bValider = (Button) findViewById(R.id.AJOUTBOU_validerBouteille);
        etNote = (EditText) findViewById(R.id.AJOUTBOU_note);
        etDate = (EditText) findViewById(R.id.AJOUTBOU_entrerDate);
        tvImg = (TextView) findViewById(R.id.AJOUTBOU_Image);
        tvPdf = (TextView) findViewById(R.id.AJOUTBOU_Pdf);

    }

    private void setListeners(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDate);
            }

        };

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityAjoutBouteille.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        rgDistinction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.AJOUTBOU_radioMed){
                    rgEtoile.setVisibility(View.GONE);
                    etNote.setVisibility(View.GONE);
                    rgMed.setVisibility(View.VISIBLE);
                }

                else if(checkedId == R.id.AJOUTBOU_radioNote20 || checkedId == R.id.AJOUTBOU_radioNote100) {
                    rgEtoile.setVisibility(View.GONE);
                    rgMed.setVisibility(View.GONE);
                    etNote.setVisibility(View.VISIBLE);
                }

                else if(checkedId == R.id.AJOUTBOU_radioEtoiles) {
                    etNote.setVisibility(View.GONE);
                    rgMed.setVisibility(View.GONE);
                    rgEtoile.setVisibility(View.VISIBLE);
                }

                else if(checkedId == R.id.AJOUTBOU_radioAutre) {
                    etNote.setVisibility(View.GONE);
                    rgMed.setVisibility(View.GONE);
                    rgEtoile.setVisibility(View.GONE);
                }
            }
        });

        ibPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPdfOk) {
                    filePathPdf = null;
                    isPdfOk = false;

                    ibPdf.setImageResource(R.drawable.ic_add_green);
                    tvPdf.setText(getText(R.string.AJOUTBOU_boutonPdf));

                    Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_fichierRetire), Snackbar.LENGTH_LONG).show();
                } else {
                    Intent fileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    fileintent.addCategory(Intent.CATEGORY_OPENABLE);
                    fileintent.setType("application/pdf");
                    try {
                        startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
                    } catch (ActivityNotFoundException e) {
                        Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_noFileExp), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        ibImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImgOk) {
                    filePathImg = null;
                    isImgOk = false;

                    ibImg.setImageResource(R.drawable.ic_add_green);
                    tvImg.setText(getText(R.string.AJOUTBOU_boutonImage));

                    Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_fichierRetire), Snackbar.LENGTH_LONG).show();
                } else {

                    Intent fileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    fileintent.addCategory(Intent.CATEGORY_OPENABLE);
                    fileintent.setType("image/*");
                    try {
                        startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
                    } catch (ActivityNotFoundException e) {
                        Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_noFileExp), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        bValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBouteille();
            }
        });
    }

    private void checkForEdit(ActionBar actionBar) {
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID_BOU)) {
            String distinction = intent.getStringExtra(EXTRA_DISTINCTION);
            String distinctionPure = distinction.substring(1);

            RadioButton rBou;
            RadioButton rBou2 = null;

            String pdf = intent.getStringExtra(EXTRA_PDF);
            String img = intent.getStringExtra(EXTRA_IMAGE);
            fav = intent.getBooleanExtra(EXTRA_FAVORI, false);

            if(pdf != null){
                isPdfOk = true;
                filePathPdf = Uri.parse(pdf);
                ibPdf.setImageResource(R.drawable.ic_clear);
                tvPdf.setText(getText(R.string.AJOUTBOU_boutonPdfAnnuler));
            }

            if(img != null){
                isImgOk = true;
                filePathImg = Uri.parse(img);
                ibImg.setImageResource(R.drawable.ic_clear);
                tvImg.setText(getText(R.string.AJOUTBOU_boutonImageAnnuler));
            }

            actionBar.setTitle(R.string.AJOUTBOU_appBar2);
            npMil.setValue(Integer.valueOf(intent.getStringExtra(EXTRA_MILLESIME)));
            npApogee.setValue(intent.getIntExtra(EXTRA_APOGEE, 0));
            etDate.setText(intent.getStringExtra(EXTRA_DATE));
            etPrix.setText(intent.getStringExtra(EXTRA_PRIX));
            etNombre.setText(intent.getStringExtra(EXTRA_NOMBRE));
            etLieuxAchat.setText(intent.getStringExtra(EXTRA_LIEUX));
            etCommentaire.setText(intent.getStringExtra((EXTRA_COMMENTAIRE)));
            etCommentaireDis.setText(intent.getStringExtra(EXTRA_COMMENTAIREDIS));

            switch(String.valueOf(distinction.charAt(0))) {
                case "A":
                    rBou = findViewById(R.id.AJOUTBOU_radioMed);
                    RadioGroup rgMed = findViewById(R.id.AJOUTBOU_radioGroupMed);

                    if(distinctionPure.equals(COULEUR_MEDAILLE_BRONZE)){
                        rgMed.setVisibility(View.VISIBLE);
                        rBou2 = findViewById(R.id.AJOUTBOU_bronze);
                    }
                    else if(distinctionPure.equals(COULEUR_MEDAILLE_ARGENT)){
                        rgMed.setVisibility(View.VISIBLE);
                        rBou2 = findViewById(R.id.AJOUTBOU_argent);
                    } else if(distinctionPure.equals(COULEUR_MEDAILLE_OR)){
                        rgMed.setVisibility(View.VISIBLE);
                        rBou2 = findViewById(R.id.AJOUTBOU_or);
                    } else{
                        rBou = findViewById(R.id.AJOUTBOU_radioAutre);
                    }
                    break;

                case "B":
                    rBou = findViewById(R.id.AJOUTBOU_radioNote20);
                    findViewById(R.id.AJOUTBOU_note).setVisibility(View.VISIBLE);
                    etNote.setText(distinctionPure);
                    break;

                case "C":
                    rBou = findViewById(R.id.AJOUTBOU_radioNote100);
                    findViewById(R.id.AJOUTBOU_note).setVisibility(View.VISIBLE);
                    etNote.setText(distinctionPure);
                    break;

                case "D":
                    rBou = findViewById(R.id.AJOUTBOU_radioEtoiles);
                    findViewById(R.id.AJOUTBOU_radioGroupEtoiles).setVisibility(View.VISIBLE);

                    if(distinctionPure.equals("1")){
                        rBou2 = findViewById(R.id.AJOUTBOU_etoile1);
                    } else if(distinctionPure.equals("2")){
                        rBou2 = findViewById(R.id.AJOUTBOU_etoile2);
                    } else{
                        rBou2 = findViewById(R.id.AJOUTBOU_etoile3);
                    }
                    break;

                default:
                    rBou = findViewById(R.id.AJOUTBOU_radioAutre);
                    findViewById(R.id.AJOUTBOU_radioGroupMed).setVisibility(View.GONE);
                    break;
            }

            rBou.setChecked(true);

            if(rBou2 != null) {
                rBou2.setChecked(true);
            }

            bValider.setText(R.string.AJOUTBOU_boutonAjouter2);


        } else {
            actionBar.setTitle(R.string.AJOUTBOU_appBar1);
        }
    }

    private void saveBouteille(){
        String mil = String.valueOf(npMil.getValue());
        int apogee = npApogee.getValue();
        String date = etDate.getText().toString();
        String prix = etPrix.getText().toString();
        String nombre = etNombre.getText().toString();
        String lieux = etLieuxAchat.getText().toString();
        String com = etCommentaire.getText().toString();
        String comDis = etCommentaireDis.getText().toString();
        String distinction = "A#000000";

        boolean isValid = true;

        if(rgDistinction.getCheckedRadioButtonId() != -1) {
            RadioButton rbMed = findViewById(R.id.AJOUTBOU_radioMed);
            RadioButton rbN20 = findViewById(R.id.AJOUTBOU_radioNote20);
            RadioButton rbN100 = findViewById(R.id.AJOUTBOU_radioNote100);
            RadioButton rbEtoile = findViewById(R.id.AJOUTBOU_radioEtoiles);
            RadioButton rbAutre = findViewById(R.id.AJOUTBOU_radioAutre);
            RadioButton rbBrz = findViewById(R.id.AJOUTBOU_bronze);
            RadioButton rbArg = findViewById(R.id.AJOUTBOU_argent);
            RadioButton rbE1 = findViewById(R.id.AJOUTBOU_etoile1);
            RadioButton rbE2 = findViewById(R.id.AJOUTBOU_etoile2);
            RadioButton rbE3 = findViewById(R.id.AJOUTBOU_etoile3);

            if(rbMed.isChecked()) {
                if(rgMed.getCheckedRadioButtonId() != -1) {
                    if(rbBrz.isChecked()) {
                        distinction = "A" + COULEUR_MEDAILLE_BRONZE;
                    } else if(rbArg.isChecked()) {
                        distinction = "A" + COULEUR_MEDAILLE_ARGENT;
                    } else{
                        distinction = "A" + COULEUR_MEDAILLE_OR;
                    }
                }
            } else if(rbAutre.isChecked()){
                distinction = "A#000000";
            } else if (rbN100.isChecked()) {
                try {
                    int note = Integer.valueOf(etNote.getText().toString());

                    if (note < 0 || note > 100) {
                        Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_entrerNote100), Snackbar.LENGTH_LONG).show();
                        isValid = false;
                    } else { distinction = "C" + note; }
                }
                catch (NumberFormatException e){
                    Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_entrerNote), Snackbar.LENGTH_LONG).show();
                    isValid = false;
                }

            } else if (rbN20.isChecked()) {
                try {
                    int note = Integer.valueOf(etNote.getText().toString());

                    if (note < 0 || note > 20) {
                        Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_entrerNote20), Snackbar.LENGTH_LONG).show();
                        isValid = false;
                    } else { distinction = "B" + note; }
                }
                catch (NumberFormatException e){
                    Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_entrerNote), Snackbar.LENGTH_LONG).show();
                    isValid = false;
                }

            } else if (rbEtoile.isChecked()) {
                if (rgEtoile.getCheckedRadioButtonId() != -1) {
                    if (rbE1.isChecked()) {
                        distinction = "D1";
                    }else if (rbE2.isChecked()) {
                        distinction = "D2";
                    }else if (rbE3.isChecked()) {
                        distinction = "D3";
                    }
                }
            }
        }

        if(isValid){
            if(nombre.trim().isEmpty()){
                Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_entrerNombre), Snackbar.LENGTH_LONG).show();
            }

            else{
                Intent data = new Intent();
                data.putExtra(EXTRA_MILLESIME, mil);
                data.putExtra(EXTRA_APOGEE, apogee);
                data.putExtra(EXTRA_DATE, date);
                data.putExtra(EXTRA_PRIX, prix);
                data.putExtra(EXTRA_NOMBRE, nombre);
                data.putExtra(EXTRA_LIEUX, lieux);
                data.putExtra(EXTRA_DISTINCTION, distinction);
                data.putExtra(EXTRA_COMMENTAIREDIS, comDis);
                data.putExtra(EXTRA_COMMENTAIRE, com);

                if(filePathPdf != null) {
                    data.putExtra(EXTRA_PDF, filePathPdf.toString());
                }

                if(filePathImg != null) {
                    data.putExtra(EXTRA_IMAGE, filePathImg.toString());
                }

                if(fav){
                    data.putExtra(EXTRA_FAVORI, true);
                }

                long id = getIntent().getLongExtra(EXTRA_ID_BOU, -1);

                if(id != -1){
                    data.putExtra(EXTRA_ID_BOU, id);
                }

                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    private void updateLabel(EditText et) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRENCH);

        et.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK && data.getData() != null) {
                    ContentResolver contentResolver = getContentResolver();
                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                    String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(data.getData()));
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    // Traitement des PDF
                    if("pdf".equals(extension)) {
                        filePathPdf = data.getData();

                        try {
                            getContentResolver().takePersistableUriPermission(filePathPdf, takeFlags);
                        } catch (SecurityException e) {
                            Log.v("_____ERREUR_____", "SecurityException PDF");
                        }

                        ibPdf.setImageResource(R.drawable.ic_clear);
                        tvPdf.setText(getText(R.string.AJOUTBOU_boutonPdfAnnuler));

                        isPdfOk = true;
                        Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_fichierSelect), Snackbar.LENGTH_LONG).show();
                    // Traitement des images
                    } else{
                        filePathImg = data.getData();

                        try {
                            getContentResolver().takePersistableUriPermission(filePathImg, takeFlags);
                        } catch (SecurityException e) {
                            Log.v("_____ERREUR_____", "SecurityException IMG");
                        }

                        ibImg.setImageResource(R.drawable.ic_clear);
                        tvImg.setText(getText(R.string.AJOUTBOU_boutonImageAnnuler));

                        isImgOk = true;
                        Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.AJOUTBOU_fichierSelect), Snackbar.LENGTH_LONG).show();
                    }
                }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
