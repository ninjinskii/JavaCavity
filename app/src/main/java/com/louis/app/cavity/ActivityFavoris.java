package com.louis.app.cavity;

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

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.List;

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
import static com.louis.app.cavity.ActivityBouteille.EXTRA_BOU_ID;
import static com.louis.app.cavity.ActivityBouteille.REQUETE_EDIT_BOU;

public class ActivityFavoris extends AppCompatActivity {

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerView mRecyclerView;
    private AdapteurBou mAdapteurBou;
    private VinViewModel mVinViewModel;
    private TextView tvEmpty;

    private String nomVin;
    private long vinId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkThemeCavity);
        } else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCoordinatorLayout = findViewById(R.id.FAV_coordinator);
        tvEmpty = findViewById(R.id.FAV_empty);

        mAdapteurBou = new AdapteurBou(this);

        mRecyclerView = findViewById(R.id.FAV_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapteurBou);

        mVinViewModel = ViewModelProviders.of(this).get(VinViewModel.class);
        mVinViewModel.getBouteillesFavorites().observe(this, new Observer<List<Bouteille>>() {
            @Override
            public void onChanged(List<Bouteille> bouteilles) {
                mAdapteurBou.submitList(bouteilles);          // setVins -> submitlist. Méthode de DiffUtil

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
                nomVin = bou.getNom();
                vinId = bou.getVin_id();

                Intent intent = new Intent(ActivityFavoris.this, ActivityAjoutBouteille.class);
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
                Intent intent = new Intent(ActivityFavoris.this, ActivityVueBouteille.class);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // On modifie une bouteille
        if(requestCode == REQUETE_EDIT_BOU && resultCode == RESULT_OK) {
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
