package com.louis.app.cavity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapteurVin extends ListAdapter<Vin, AdapteurVin.VinHolder>{

    private List<Vin> mListeVins = new ArrayList<>();
    private List<Vin> mListeVinsFilter;

    private boolean mTriRegion = false;
    private OnItemClickListener mListenerClick;
    private OnItemLongClickListener mListenerLongClick;

    AdapteurVin(boolean triRegion) {
        // DiifUtil nous permet de s'affranchir complètement de l'instanciation d'une ArrayList repéseantant
        // les données  LiveData. Il permet aussi de mettre en place une logique de filtrage pour déterminer
        // quel itemView a besoin d'être updaté/inséré/supprimé
        // La liste n'es plus accessible, mais maitenent nous pouvons directement utiliser la méthode getItem(position)
        // au lieu de mListeVins.get(position)
        super(DIFF_CALLBACK);

        if(triRegion){
            mTriRegion = true;
        }
    }

    private static final DiffUtil.ItemCallback<Vin> DIFF_CALLBACK = new DiffUtil.ItemCallback<Vin>() {
        @Override
        public boolean areItemsTheSame(@NonNull Vin oldItem, @NonNull Vin newItem) {
            // Si l'ID est le même, alors on parle du même objet
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Vin oldItem, @NonNull Vin newItem) {
            return oldItem.getRegion().equals(newItem.getRegion()) &&
                    oldItem.getAppellation().equals(newItem.getAppellation())&&
                    oldItem.getNom().equals(newItem.getNom())&&
                    oldItem.getCouleur().equals(newItem.getCouleur());
        }
    };

    class VinHolder extends RecyclerView.ViewHolder{
        private TextView mTextViewSep;
        private ImageView mImageViewColor;
        private TextView mTextViewNom;
        private TextView mTextViewAppellation;
        //private TextView mTextViewStock;

        public VinHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewSep = itemView.findViewById(R.id.VIN_ITEM_separateur);
            mImageViewColor = itemView.findViewById(R.id.VIN_ITEM_color);
            mTextViewNom = itemView.findViewById(R.id.VIN_ITEM_nom);
            mTextViewAppellation = itemView.findViewById(R.id.VIN_ITEM_appellation);
            //mTextViewStock = itemView.findViewById(R.id.VIN_ITEM_nombre);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();

                    if(mListenerLongClick != null && position != RecyclerView.NO_POSITION && getItemViewType() == 0) {
                        mListenerLongClick.onItemLongClick(getItem(position));
                        MainActivity.viewToRotate = mImageViewColor;
                    }

                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(mListenerClick != null && position != RecyclerView.NO_POSITION) {
                        mListenerClick.onItemClick(getItem(position));
                        MainActivity.viewToRotate = mImageViewColor;
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public VinHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        if(viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vin_item, parent, false);
        } else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vin_item_sep, parent, false);
        }

        return new VinHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VinHolder holder, int position) {
        Vin currentVin = getItem(position);

        if(getItemViewType(position) == 0) {
            holder.mImageViewColor.setColorFilter(Color.parseColor(currentVin.getCouleur()));
            holder.mTextViewNom.setText(currentVin.getNom());
            holder.mTextViewAppellation.setText(currentVin.getAppellation());
            //holder.mTextViewStock.setText(String.valueOf(currentVin.getNombre()));
        } else{
            if(mListeVins.size() > position + 1) {
                holder.mTextViewSep.setText(getItem(position + 1).getRegion().toUpperCase());
            }
        }
    }

    Vin getVinAt(int pos){
        return getItem(pos);
    }

    @Override
    public void submitList(@Nullable List<Vin> list) {
        mListeVins.clear();
        mListeVins.addAll(list);

        mListeVinsFilter = new ArrayList<>(mListeVins);

        if(mTriRegion) {
            String region = "";
            String region2;
            Vin vinSeparateur = new Vin("Séparateur", "#000000", "Séparateur", "Séparateur");

            if (mListeVins.size() != 0) {
                if (!mListeVins.get(0).getCouleur().equals("#000000")) {
                    for (int i = 0; i < mListeVins.size(); i++) {
                        Vin vin = mListeVins.get(i);
                        region2 = vin.getRegion();

                        if (!region.equals(region2)) {
                            region = region2;
                            mListeVins.add(i, vinSeparateur);
                            list.add(i, vinSeparateur);
                        }
                    }
                }
            }
        }
        super.submitList(list);
    }

    @Override
    public int getItemViewType(int position) {
        if(getVinAt(position).getNom().equals("Séparateur")){
            return 1;
        }
        return 0;
    }

    public interface OnItemClickListener{ void onItemClick(Vin vin); }

    public interface OnItemLongClickListener{ void onItemLongClick(Vin vin); }

    void setOnItemLongClickListener(OnItemLongClickListener listener){ mListenerLongClick = listener; }

    void setOnItemClickListener(OnItemClickListener listener){ mListenerClick = listener; }
}
