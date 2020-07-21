package com.louis.app.cavity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.louis.app.cavity.ActivityAjoutVin.EXTRA_APPELLATION;
import static com.louis.app.cavity.ActivityAjoutVin.EXTRA_COULEUR;
import static com.louis.app.cavity.ActivityAjoutVin.EXTRA_ID;
import static com.louis.app.cavity.ActivityAjoutVin.EXTRA_NOM;
import static com.louis.app.cavity.ActivityAjoutVin.EXTRA_REGION;
import static com.louis.app.cavity.ActivityBouteille.EXTRA_ID_VIN_BOUTEILLE;
import static com.louis.app.cavity.ActivityBouteille.EXTRA_VIN_NOM;
import static com.louis.app.cavity.ActivityParametres.PREFS_NIGHT;
import static com.louis.app.cavity.ActivityParametres.PREFS_TRI;

public class MainActivity extends AppCompatActivity {
    public static final int ANNEE = Calendar.getInstance().get(Calendar.YEAR);
    protected static View viewToRotate;

    public final static String DB_NAME = "vin_database";
    public final static String COULEUR_VIN_BLANC = "#fff176";
    public final static String COULEUR_VIN_ROUGE = "#A60000";
    public final static String COULEUR_VIN_LIQUOREUX = "#FFA727";
    public final static String COULEUR_VIN_ROSE = "#F48FB1";
    public final static int REQUETE_AJOUT_VIN = 1;
    public final static int REQUETE_EDIT_VIN = 2;

    public static ArrayList<String> mListeRegion = new ArrayList<>();   // Pour suggestions

    private CoordinatorLayout mCoordinatorLayout;
    private VinViewModel mVinViewModel;
    private RecyclerView mRecyclerView;
    private AdapteurVin mAdapteurVin;
    private FloatingActionButton fab;
    private SharedPreferences prefs;
    private TextView tvEmpty;
    private TextView tvTotal;

    private Observer<List<Vin>> mObserver;
    private boolean night;
    private String tri;

    final ColorDrawable mBackground = new ColorDrawable(Color.parseColor("#F05545"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("com.louis.app.cavity", Context.MODE_PRIVATE);
        tri = prefs.getString(PREFS_TRI, "region");
        night = prefs.getBoolean(PREFS_NIGHT, false);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || night){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.DarkThemeCavity);
        } else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoordinatorLayout = findViewById(R.id.MAIN_coordinator);
        mRecyclerView = findViewById(R.id.MAIN_recyclerView);
        tvTotal = findViewById(R.id.MAIN_total);
        tvEmpty = findViewById(R.id.MAIN_empty);
        viewToRotate = null;

        fab = findViewById(R.id.MAIN_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityAjoutVin.class);
                startActivityForResult(intent, REQUETE_AJOUT_VIN);
            }
        });

        if(tri.equals("region")){
            mAdapteurVin = new AdapteurVin(true);
        } else{
            mAdapteurVin = new AdapteurVin(false);
        }

        createObserver();
        setRecyclerViewAndViewModel();
        observeListRegion();
    }

    private void setRecyclerViewAndViewModel(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapteurVin);

        mVinViewModel = ViewModelProviders.of(this).get(VinViewModel.class);

        // Actualise le nombre total de bouteilles
        mVinViewModel.getTotalBouteilles().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tvTotal.setText(String.format(getString(R.string.MAIN_nbTotal),
                        String.valueOf(integer)));
            }
        });

        // Observe la liste de vins
        if(tri.equals("region")){
            mVinViewModel.getAllVin().observe(this, mObserver);
        } else{
            mVinViewModel.getAllVinByCouleur().observe(this, mObserver);
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Fond rouge en dessous de l'item swipé, approximatif
            @Override
            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                if(viewHolder.getItemViewType() == 0) {
                    if (dX > 0) {
                        mBackground.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                    } else {
                        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    }
                    mBackground.draw(c);
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(viewHolder.getItemViewType() == 0) {
                    final Vin vinSuppr = mAdapteurVin.getVinAt(viewHolder.getAdapterPosition());
                    mVinViewModel.dropTablTemp();                                                   // Supprime le contenu du tampon
                    mVinViewModel.insertBouInTemp(vinSuppr.getId());                                // Insère les bouteilles liées à ce vin dans une table "tampon"
                    mVinViewModel.delete(vinSuppr);                                                 // Supprime le vin, et ses bouteilles (cf onDelete: CASCADE) pour éviter les ForeignKey Exception

                    Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.MAIN_vinSuppr), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.MAIN_annulerSuppr), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mVinViewModel.insert(vinSuppr);
                                    mVinViewModel.copyTemp();                                       // Copie les bouteilles du tampon dans la table des bouteilles
                                    mVinViewModel.dropTablTemp();                                   // Supprime le contenu du tampon
                                }
                            }).setDuration(8000)
                            .show();
                }
            }
        }).attachToRecyclerView(mRecyclerView);

        mAdapteurVin.setOnItemClickListener(new AdapteurVin.OnItemClickListener() {
            @Override
            public void onItemClick(Vin vin) {
                if(!vin.getCouleur().equals("#000000")){
                    Intent intent = new Intent(MainActivity.this, ActivityBouteille.class);
                    intent.putExtra(EXTRA_ID_VIN_BOUTEILLE, vin.getId());
                    intent.putExtra(EXTRA_VIN_NOM, vin.getNom());

                    startActivity(intent);
                }
            }
        });

        mAdapteurVin.setOnItemLongClickListener(new AdapteurVin.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Vin vin) {
                if(!vin.getCouleur().equals("#000000")) {
                    Intent intent = new Intent(MainActivity.this, ActivityAjoutVin.class);
                    intent.putExtra(EXTRA_ID, vin.getId());
                    intent.putExtra(EXTRA_REGION, vin.getRegion());
                    intent.putExtra(EXTRA_COULEUR, vin.getCouleur());
                    intent.putExtra(EXTRA_APPELLATION, vin.getAppellation());
                    intent.putExtra(EXTRA_NOM, vin.getNom());

                    startActivityForResult(intent, REQUETE_EDIT_VIN);
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

    // Observe la liste des régions pour pouvoir proposer des suggestions dans ActivityAjoutVin
    private void observeListRegion(){
        mVinViewModel.getAllRegions().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Set<String> set = new HashSet<>(strings);       // Supprimme les doublons, vu que les Set ne peuvent pas en contenir

                mListeRegion.clear();
                mListeRegion.addAll(set);
            }
        });
    }

    // Anime les rectangles de couleur des vins
    private void rotateImageView(View v){
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        rotate.setDuration(600);
        rotate.setRepeatCount(2);
        v.setAnimation(rotate);
    }

    private void createObserver(){
        mObserver = new Observer<List<Vin>>() {
            @Override
            public void onChanged(List<Vin> vins) {
                mAdapteurVin.submitList(vins);

                if(vins.isEmpty()){
                    mRecyclerView.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // On ajoute un vin
        if(requestCode == REQUETE_AJOUT_VIN && resultCode == RESULT_OK){
            assert data != null;

            String region = data.getStringExtra(EXTRA_REGION);
            String appellation = data.getStringExtra(EXTRA_APPELLATION);
            String nom = data.getStringExtra(EXTRA_NOM);
            String couleur = data.getStringExtra(EXTRA_COULEUR);

            Vin vin = new Vin(region, couleur, appellation, nom);
            mVinViewModel.insert(vin);

            viewToRotate = null;

            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.MAIN_vinAjout), Snackbar.LENGTH_LONG).show();
        }

        // On modifie un vin
        else if(requestCode == REQUETE_EDIT_VIN && resultCode == RESULT_OK){
            assert data != null;

            long id = data.getLongExtra(EXTRA_ID, -1);

            if(id == -1){
                Snackbar.make(mCoordinatorLayout, "ERROR HAS OCCURED", Snackbar.LENGTH_LONG).show();
                return;
            }

            String region = data.getStringExtra(EXTRA_REGION);
            String appellation = data.getStringExtra(EXTRA_APPELLATION);
            String nom = data.getStringExtra(EXTRA_NOM);
            String couleur = data.getStringExtra(EXTRA_COULEUR);

            Vin vin = new Vin(region, couleur, appellation, nom);
            vin.setId(id);
            mVinViewModel.update(vin);

            viewToRotate = null;

            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.MAIN_vinModif), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            if(viewToRotate != null){
                rotateImageView(viewToRotate);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.MENUITEM_searchbar);
        String hint = getString(R.string.MENUITEM_searchViewHint);

        SearchView searchView = (SearchView)item.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setQueryHint(hint);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty()){
                    mVinViewModel.getAllVin().removeObserver(mObserver);
                    mVinViewModel.getAllVin().observe(MainActivity.this, mObserver);
                } else{
                    mVinViewModel.getAllVin().removeObserver(mObserver);
                    mVinViewModel.getVinBySearchQuery("%" + s + "%").observe(MainActivity.this, mObserver);
                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.MENUITEM_apogee:
                Intent intent = new Intent(MainActivity.this, ActivityApogee.class);
                startActivity(intent);
                break;

            case R.id.MENUITEM_favoris:
                Intent intentFav = new Intent(MainActivity.this, ActivityFavoris.class);
                startActivity(intentFav);
                break;

            case R.id.MENUITEM_parametres:
                Intent intentPar = new Intent(MainActivity.this, ActivityParametres.class);
                startActivity(intentPar);
                break;

            case R.id.MENUITEM_aide:
                Intent intentAide = new Intent(MainActivity.this, ActivityAide.class);
                startActivity(intentAide);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}