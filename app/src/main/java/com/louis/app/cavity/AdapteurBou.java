package com.louis.app.cavity;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class AdapteurBou extends ListAdapter<Bouteille, AdapteurBou.BouHolder> {

    private static final int VUE_MEDAILLE = 0;
    private static final int VUE_NOTE_20 = 1;
    private static final int VUE_NOTE_100 = 2;
    private static final int VUE_ETOILES = 3;

    private OnItemLongClickListener mListenerLongClick;
    private OnItemClickListener mListenerClick;
    private Context mContext;

    AdapteurBou(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    private static final DiffUtil.ItemCallback<Bouteille> DIFF_CALLBACK = new DiffUtil.ItemCallback<Bouteille>() {
        @Override
        public boolean areItemsTheSame(@NonNull Bouteille oldItem, @NonNull Bouteille newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Bouteille oldItem, @NonNull Bouteille newItem) {
            return oldItem.getNom().equals(newItem.getNom()) &&                     //nom
                    oldItem.getDistinction().equals(newItem.getDistinction()) &&    //distinction
                    oldItem.getMillesime().equals(newItem.getMillesime()) &&        // millésime
                    oldItem.getNombre().equals(newItem.getNombre());                // nombre
        }
    };

    class BouHolder extends RecyclerView.ViewHolder{
        private ImageButton mImageButtonRemove;
        private ImageButton mImageButtonInfo;
        private ImageButton mImageButtonAdd;
        private ImageView mImageViewFavorite;
        private ImageView mImageViewStar;
        private ImageView mImageViewMed;
        private TextView mTextViewNote;
        private TextView mTextViewNom;
        private TextView mTextViewMil;
        private TextView mTextViewNb;

        public BouHolder(@NonNull View itemView) {
            super(itemView);

            mImageButtonRemove = itemView.findViewById(R.id.BOUITEM_remove);
            mImageButtonInfo = itemView.findViewById(R.id.BOUITEM_info);
            mImageButtonAdd = itemView.findViewById(R.id.BOUITEM_add);
            mImageViewFavorite = itemView.findViewById(R.id.BOUITEM_favorite);
            mImageViewStar = itemView.findViewById(R.id.BOUITEM_etoileBou);
            mImageViewMed = itemView.findViewById(R.id.BOUITEM_medailleBou);
            mTextViewNote = itemView.findViewById(R.id.BOUITEM_NoteBou);
            mTextViewNom = itemView.findViewById(R.id.BOUITEM_nomBou);
            mTextViewMil = itemView.findViewById(R.id.BOUITEM_milBou);
            mTextViewNb = itemView.findViewById(R.id.BOUITEM_nombreBou);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();

                    if(mListenerLongClick != null && position != RecyclerView.NO_POSITION) {
                        mListenerLongClick.onItemLongClick(getItem(position));
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
                    }
                }
            });

            mImageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(mListenerClick != null && position != RecyclerView.NO_POSITION) {
                        mListenerClick.onRemoveClick(getItem(position), position);
                    }
                }
            });

            mImageButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(mListenerClick != null && position != RecyclerView.NO_POSITION) {
                        mListenerClick.onAddClick(getItem(position), position);
                    }
                }
            });

            mImageButtonInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(mListenerClick != null && position != RecyclerView.NO_POSITION) {
                        mListenerClick.onInfoClick(getItem(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public AdapteurBou.BouHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        switch(viewType){
            case VUE_MEDAILLE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bou_item_medaille, parent, false);
                break;

            case VUE_NOTE_20:

            case VUE_NOTE_100:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bou_item_note, parent, false);
                break;

            case VUE_ETOILES:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bou_item_star, parent, false);
                break;

            default:
                // Pas censé se produire
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bou_item_medaille, parent, false);
                Log.v("_________ERREUR_________", "case DEFAULT adapteur bouteilles");
                break;
        }

        return new BouHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapteurBou.BouHolder holder, int position) {
        Bouteille currentBou = getItem(position);

        holder.mTextViewNom.setText(currentBou.getNom());
        holder.mTextViewMil.setText(currentBou.getMillesime());
        holder.mTextViewNb.setText(currentBou.getNombre());

        switch(getItemViewType(position)){
            case VUE_MEDAILLE:
                holder.mImageViewMed.setColorFilter(Color.parseColor(currentBou.getDistinctionPure()));
                break;

            case VUE_NOTE_20:
                holder.mTextViewNote.setText(mContext.getString(R.string.BOU_note20, currentBou.getDistinctionPure()));
                break;

            case VUE_NOTE_100:
                holder.mTextViewNote.setText(mContext.getString(R.string.BOU_note100, currentBou.getDistinctionPure()));
                break;

            case VUE_ETOILES:
                if(currentBou.getDistinctionPure().equals("1")) {
                    holder.mImageViewStar.setImageResource(R.drawable.star1);
                } else if(currentBou.getDistinctionPure().equals("2")){
                    holder.mImageViewStar.setImageResource(R.drawable.star2);
                } else{
                    holder.mImageViewStar.setImageResource(R.drawable.star3);
                }
                break;
        }

        if(currentBou.isFav()){
            holder.mImageViewFavorite.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position){
        Bouteille currentBou = getItem(position);
        Character c = currentBou.getDistinction().charAt(0);
        String typeDistinction = c.toString();

        switch (typeDistinction) {
            case "A":
                return VUE_MEDAILLE;
            case "B":
                return VUE_NOTE_20;
            case "C":
                return VUE_NOTE_100;
            default:
                return VUE_ETOILES;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Bouteille bou);
        void onRemoveClick(Bouteille bou, int position);
        void onAddClick(Bouteille bou, int position);
        void onInfoClick(Bouteille bou);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(Bouteille bou);
    }

    void setOnItemLongClickListener(OnItemLongClickListener listener){ mListenerLongClick = listener; }

    void setOnItemClickListener(OnItemClickListener listener){ mListenerClick = listener; }
}
