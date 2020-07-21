package com.louis.app.cavity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static com.louis.app.cavity.ActivityBouteille.EXTRA_BOU_ID;
import static com.louis.app.cavity.ActivityParametres.PREFS_DEVISE;

public class ActivityVueBouteille extends AppCompatActivity {
    private Bouteille currentBou;
    private String devise;
    private long bouId;

    CollapsingToolbarLayout mCollapsingToolbarLayout;
    CoordinatorLayout mCoordinatorLayout;
    FloatingActionButton fab;
    ImageView mImageViewCollapseBack;
    VinViewModel mVinViewModel;
    TextView mTextViewDisTxt;
    ImageView mImageViewDis;
    TextView tvApogee;
    TextView tvNombre;
    TextView tvComDis;
    ImageButton ibPdf;
    TextView tvLieux;
    TextView tvPrix;
    TextView tvDate;
    TextView tvMil;
    TextView tvCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkThemeCavityVueBou);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vue_bouteille);

        Toolbar toolbar = findViewById(R.id.VUEBOU_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            bouId = extras.getLong(EXTRA_BOU_ID);
        }

        SharedPreferences prefs = this.getSharedPreferences("com.louis.app.cavity", Context.MODE_PRIVATE);
        devise = prefs.getString(PREFS_DEVISE, " €");

        findViews();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentBou.isFav()) {
                    fab.setImageResource(R.drawable.ic_favorite_border);
                    fab.hide();
                    fab.show();
                    currentBou.setFav(false);
                    mVinViewModel.update(currentBou);
                    Snackbar.make(mCoordinatorLayout, R.string.VUEBOU_retraitFav, Snackbar.LENGTH_LONG).show();
                } else{
                    fab.setImageResource(R.drawable.ic_favorite);
                    fab.hide();
                    fab.show();
                    currentBou.setFav(true);
                    mVinViewModel.update(currentBou);
                    Snackbar.make(mCoordinatorLayout, R.string.VUEBOU_ajoutFav, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        ibPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = currentBou.getPdfPath();

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
                        currentBou.setPdfPath(null);
                        mVinViewModel.update(currentBou);
                        Snackbar.make(mCoordinatorLayout, R.string.BOU_noPdf, Snackbar.LENGTH_LONG).show();
                    }
                } else{
                    Snackbar.make(mCoordinatorLayout, R.string.BOU_noPdf, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        setBouteilleInfo();

    }

    private void findViews(){
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.VUEBOU_toolbarLayout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.VUEBOU_coordinator);
        mTextViewDisTxt = (TextView) findViewById(R.id.VUEBOU_disTxt);
        mImageViewDis = (ImageView) findViewById(R.id.VUEBOU_dis);
        tvComDis = (TextView) findViewById(R.id.VUEBOU_discom);
        tvNombre = (TextView) findViewById(R.id.VUEBOU_nombre);
        tvApogee = (TextView) findViewById(R.id.VUEBOU_apogee);
        ibPdf = (ImageButton) findViewById(R.id.BOUITEM_info);
        tvLieux = (TextView) findViewById(R.id.VUEBOU_lieux);
        tvPrix = (TextView) findViewById(R.id.VUEBOU_prix);
        tvDate = (TextView) findViewById(R.id.VUEBOU_date);
        tvCom = (TextView) findViewById(R.id.VUEBOU_com);
        tvMil = (TextView) findViewById(R.id.VUEBOU_mil);
    }

    private void setBouteilleInfo(){
        mVinViewModel = ViewModelProviders.of(this).get(VinViewModel.class);
        mVinViewModel.getBouteilleById(bouId).observe(this, new Observer<Bouteille>() {
            @Override
            public void onChanged(Bouteille bouteille) {
                currentBou = bouteille;

                mCollapsingToolbarLayout.setTitle(currentBou.getNom());

                String imgPath = currentBou.getImgPath();

                if(imgPath != null) {
                    mImageViewCollapseBack = findViewById(R.id.VUEBOU_collapseBackgroung);

                    try{
                        Glide.with(ActivityVueBouteille.this)
                                .load(Uri.parse(imgPath))
                                .centerCrop()
                                .into(mImageViewCollapseBack);
                    } catch (SecurityException e){
                        currentBou.setImgPath(null);
                        mVinViewModel.update(currentBou);
                        Snackbar.make(mCoordinatorLayout, R.string.VUEBOU_imgErr, Snackbar.LENGTH_LONG).show();
                    }
                }

                tvComDis.setText(currentBou.getCommentaireNote());
                tvCom.setText(currentBou.getCommentaire());
                tvPrix.setText(String.format("%s%s", currentBou.getPrixAchat(), devise));
                tvMil.setText(currentBou.getMillesime());
                tvLieux.setText(currentBou.getLieuxAchat());
                tvDate.setText(currentBou.getDateAchat());
                tvApogee.setText(String.valueOf(currentBou.getApogee()));
                tvNombre.setText(currentBou.getNombre());

                String distinction = currentBou.getDistinction();
                String distinctionPure = currentBou.getDistinctionPure();

                switch(String.valueOf(distinction.charAt(0))) {
                    case "A":
                        mImageViewDis.setImageResource(R.drawable.rectangle);
                        mImageViewDis.setColorFilter(Color.parseColor(distinctionPure));
                        break;

                    case "B":
                        mImageViewDis.setVisibility(View.GONE);
                        mTextViewDisTxt.setVisibility(View.VISIBLE);
                        mTextViewDisTxt.setText(getApplicationContext().getString(R.string.BOU_note20, currentBou.getDistinctionPure()));
                        break;

                    case "C":
                        mImageViewDis.setVisibility(View.GONE);
                        mTextViewDisTxt.setVisibility(View.VISIBLE);
                        mTextViewDisTxt.setText(getApplicationContext().getString(R.string.BOU_note100, currentBou.getDistinctionPure()));
                        break;

                    case "D":
                        if(distinctionPure.equals("1")){
                            mImageViewDis.setImageResource(R.drawable.star1);
                        } else if(distinctionPure.equals("2")){
                            mImageViewDis.setImageResource(R.drawable.star2);
                        } else{
                            mImageViewDis.setImageResource(R.drawable.star3);
                        }
                        break;

                    default:
                        Log.v("_____ERREUR_____", "Passe dans default switch ActivityVueBouteille");
                        break;
                }

                if(currentBou.isFav()){
                    fab.setImageResource(R.drawable.ic_favorite);
                    fab.hide();
                    fab.show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
