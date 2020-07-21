package com.louis.app.cavity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_APOGEE;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_COMMENTAIRE;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_COMMENTAIREDIS;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_DATE;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_DISTINCTION;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_FAVORI;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_ID_BOU;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_IMAGE;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_LIEUX;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_MILLESIME;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_NOMBRE;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_PDF;
import static com.louis.app.cavity.ActivityAjoutBouteille.EXTRA_PRIX;

public class ActivityBouteille extends AppCompatActivity {
    public static final String EXTRA_ID_VIN_BOUTEILLE = "com.louis.app.cavity.EXTRA_ID_VIN_BOUTEILLE";
    public static final String EXTRA_VIN_NOM = "com.louis.app.cavity.EXTRA_VIN_NOM";
    public static final String EXTRA_BOU_ID = "com.louis.app.cavity.EXTRA_BOU_ID";
    public static final String COULEUR_MEDAILLE_BRONZE = "#CD7F32";
    public static final String COULEUR_MEDAILLE_ARGENT = "#C0C0C0";
    public static final String COULEUR_MEDAILLE_OR = "#FFD700";
    public static final int REQUETE_AJOUT_BOU = 1;
    public static final int REQUETE_EDIT_BOU = 2;

    public static ArrayList<String> mListeLieux = new ArrayList<>();   // Pour suggestions

    private Vin currentVin;
    private String nomVin;
    private long vinId;

    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private VinViewModel mVinViewModel;
    private AdapteurBou mAdapteurBou;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkThemeCavity);
        } else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouteille);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            vinId = extras.getLong(EXTRA_ID_VIN_BOUTEILLE);
            nomVin = extras.getString(EXTRA_VIN_NOM);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.BOU_title));

        fab = (FloatingActionButton) findViewById(R.id.BOU_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityBouteille.this, ActivityAjoutBouteille.class);
                intent.putExtra(EXTRA_VIN_NOM, nomVin);
                startActivityForResult(intent, REQUETE_AJOUT_BOU);
            }
        });

        mCoordinatorLayout = findViewById(R.id.BOU_coordinator);
        tvEmpty = findViewById(R.id.BOU_empty);
        tvEmpty.setText(getString(R.string.BOU_emptyList));

        TextView mTextViewVin = findViewById(R.id.BOU_nomVin);
        mTextViewVin.setText(nomVin);

        setRecyclerViewAndViewModel();
        observeListLieux();
        observeVin();
    }

    private void observeListLieux(){
        mVinViewModel.getPurchaseLocation().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Set<String> set = new HashSet<>(strings);       // Supprimme les doublons, vu que les Set ne peuvent pas en contenir

                mListeLieux.clear();
                mListeLieux.addAll(set);
            }
        });
    }

    private void observeVin(){
        mVinViewModel.getVinById(vinId).observe(this, new Observer<Vin>() {
            @Override
            public void onChanged(Vin vin) {
                currentVin = vin;
            }
        });
    }

    private void setRecyclerViewAndViewModel(){
        mAdapteurBou = new AdapteurBou(this);

        mRecyclerView = findViewById(R.id.BOU_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapteurBou);

        mVinViewModel = ViewModelProviders.of(this).get(VinViewModel.class);
        mVinViewModel.getBouteillesByVin(vinId).observe(this, new Observer<List<Bouteille>>() {
            @Override
            public void onChanged(List<Bouteille> bouteilles) {
                mAdapteurBou.submitList(bouteilles);

                if(bouteilles.isEmpty()){
                    mRecyclerView.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                }
            }
        });

        mAdapteurBou.setOnItemLongClickListener(new AdapteurBou.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Bouteille bou) {
                Intent intent = new Intent(ActivityBouteille.this, ActivityAjoutBouteille.class);
                intent.putExtra(EXTRA_ID_BOU, bou.getId());
                intent.putExtra(EXTRA_MILLESIME, bou.getMillesime());
                intent.putExtra(EXTRA_APOGEE, bou.getApogee());
                intent.putExtra(EXTRA_DATE, bou.getDateAchat());
                intent.putExtra(EXTRA_PRIX, bou.getPrixAchat());
                intent.putExtra(EXTRA_NOMBRE, bou.getNombre());
                intent.putExtra(EXTRA_LIEUX, bou.getLieuxAchat());
                intent.putExtra(EXTRA_DISTINCTION, bou.getDistinction());
                intent.putExtra(EXTRA_COMMENTAIRE, bou.getCommentaire());
                intent.putExtra(EXTRA_COMMENTAIREDIS, bou.getCommentaireNote());
                intent.putExtra(EXTRA_PDF, bou.getPdfPath());
                intent.putExtra(EXTRA_IMAGE, bou.getImgPath());
                intent.putExtra(EXTRA_FAVORI, bou.isFav());

                startActivityForResult(intent, REQUETE_EDIT_BOU);
            }
        });

        mAdapteurBou.setOnItemClickListener(new AdapteurBou.OnItemClickListener() {
            @Override
            public void onItemClick(Bouteille bou) {
                Intent intent = new Intent(ActivityBouteille.this, ActivityVueBouteille.class);
                intent.putExtra(EXTRA_BOU_ID, bou.getId());
                startActivity(intent);
            }

            @Override
            public void onRemoveClick(Bouteille bou, int pos) {
                if(!bou.getNombre().equals("0")) {
                    int cpt = Integer.valueOf(bou.getNombre());
                    cpt -= 1;
                    bou.setNombre(String.valueOf(cpt));
                    mVinViewModel.update(bou);
                    mAdapteurBou.notifyItemChanged(pos);
                } else{
                    Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.BOU_epuise), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.BOU_suppr), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mVinViewModel.delete(bou);
                        }
                    }).show();
                }
            }

            @Override
            public void onAddClick(Bouteille bou, int pos) {
                int cpt = Integer.valueOf(bou.getNombre());
                cpt += 1;
                bou.setNombre(String.valueOf(cpt));
                mVinViewModel.update(bou);
                mAdapteurBou.notifyItemChanged(pos);
            }

            @Override
            public void onInfoClick(Bouteille bou) {
                String path = bou.getPdfPath();

                if(path != null && !path.trim().equals("")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(path), "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    try{
                        startActivity(intent);
                    } catch(ActivityNotFoundException a){
                        Snackbar.make(mCoordinatorLayout, R.string.BOU_noPdfApp, Snackbar.LENGTH_LONG).show();
                    } catch(SecurityException e){
                        bou.setPdfPath(null);
                        mVinViewModel.update(bou);
                        Snackbar.make(mCoordinatorLayout, R.string.BOU_noPdf, Snackbar.LENGTH_LONG).show();
                    }
                } else{
                    Snackbar.make(mCoordinatorLayout, R.string.BOU_noPdf, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0  && fab.isShown()){
                    fab.hide();
                }

                if(dy < 0  && !fab.isShown()){
                    fab.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // On ajoute une bouteille
        if(requestCode == REQUETE_AJOUT_BOU && resultCode == RESULT_OK){
            assert data != null;

            String mil = data.getStringExtra(EXTRA_MILLESIME);
            int apogee = data.getIntExtra(EXTRA_APOGEE, 0);
            String date = data.getStringExtra(EXTRA_DATE);
            String prix = data.getStringExtra(EXTRA_PRIX);
            String nombre = data.getStringExtra(EXTRA_NOMBRE);
            String lieux = data.getStringExtra(EXTRA_LIEUX);
            String dis = data.getStringExtra(EXTRA_DISTINCTION);
            String comdis = data.getStringExtra(EXTRA_COMMENTAIREDIS);
            String pdf = data.getStringExtra(EXTRA_PDF);
            String img = data.getStringExtra(EXTRA_IMAGE);
            String com = data.getStringExtra(EXTRA_COMMENTAIRE);

            Bouteille bou = new Bouteille(nomVin, nombre, mil, prix, lieux, date, dis, vinId);
            if(apogee != 0){ bou.setApogee(apogee); }
            if(pdf != null){ bou.setPdfPath(pdf); }
            if(img != null){ bou.setImgPath(img); }
            if(com != null && !com.trim().isEmpty()){ bou.setCommentaire(com); }
            if(comdis != null && !comdis.trim().isEmpty()){ bou.setCommentaireNote(comdis); }

            mVinViewModel.insert(bou);

            currentVin.setNombre(currentVin.getNombre() + Integer.valueOf(nombre));
            mVinViewModel.update(currentVin);   // Le vin n'adapte pas tout de suite son nombre

            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.BOU_bouAjout), Snackbar.LENGTH_LONG).show();
        }


        // On modifie une bouteille
        else if(requestCode == REQUETE_EDIT_BOU && resultCode == RESULT_OK) {
            assert data != null;

            long id = data.getLongExtra(EXTRA_ID_BOU, -1);

            if (id == -1) {
                Snackbar.make(mCoordinatorLayout, "ERROR HAS OCCURED", Snackbar.LENGTH_LONG).show();
                return;
            }

            String mil = data.getStringExtra(EXTRA_MILLESIME);
            int apogee = data.getIntExtra(EXTRA_APOGEE, 0);
            String date = data.getStringExtra(EXTRA_DATE);
            String prix = data.getStringExtra(EXTRA_PRIX);
            String nombre = data.getStringExtra(EXTRA_NOMBRE);
            String lieux = data.getStringExtra(EXTRA_LIEUX);
            String dis = data.getStringExtra(EXTRA_DISTINCTION);
            String comdis = data.getStringExtra(EXTRA_COMMENTAIREDIS);
            String pdf = data.getStringExtra(EXTRA_PDF);
            String img = data.getStringExtra(EXTRA_IMAGE);
            String com = data.getStringExtra(EXTRA_COMMENTAIRE);
            boolean fav = data.getBooleanExtra(EXTRA_FAVORI, false);

            Bouteille bou = new Bouteille(nomVin, nombre, mil, prix, lieux, date, dis, vinId);
            if (apogee != 0) { bou.setApogee(apogee); }
            if (pdf != null) { bou.setPdfPath(pdf); }
            if (img != null) { bou.setImgPath(img); }
            if (com != null && !com.trim().isEmpty()) { bou.setCommentaire(com); }
            if (comdis != null && !comdis.trim().isEmpty()) { bou.setCommentaireNote(comdis); }
            if (fav) { bou.setFav(true); }

            bou.setId(id);
            mVinViewModel.update(bou);

            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.BOU_bouModif), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
