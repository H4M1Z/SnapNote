package pkg.home.notepad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter <FavouritesAdapter.FavouritesViewHolder>{
    List<Favourites> favouritesList;
    IOnFavouriteClickListener onFavouriteClickListener;
    boolean isVisible;

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void removeItem(int position){
        favouritesList.remove(position);
    }

    public void setOnFavouriteClickListener(IOnFavouriteClickListener onFavouriteClickListener) {
        this.onFavouriteClickListener = onFavouriteClickListener;
    }

    public FavouritesAdapter(List<Favourites> favouritesList) {
        this.favouritesList = favouritesList;
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_view_layout,null);
        return new FavouritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder holder, int position) {
         Favourites favourite=favouritesList.get(position);
         holder.tvNoteOutput.setText(favourite.getText());
         holder.tvTimeOutput.setText(favourite.getTime());
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    class FavouritesViewHolder extends RecyclerView.ViewHolder{
        TextView tvNoteOutput,tvTimeOutput;
        CheckBox checkBox;
        ImageView ivFavourite;

        public FavouritesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteOutput=itemView.findViewById(R.id.tvShowNoteOutputInListRecyclerView);
            tvTimeOutput=itemView.findViewById(R.id.tvShowTimeOutputInListRecyclerView);
            checkBox=itemView.findViewById(R.id.listViewCheckBox);
            ivFavourite=itemView.findViewById(R.id.ivFavourite);
            if (isVisible){
                checkBox.setVisibility(View.VISIBLE);
            }else {
                checkBox.setVisibility(View.INVISIBLE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onFavouriteClickListener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            onFavouriteClickListener.onFavouriteItemClick(itemView,position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onFavouriteClickListener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            onFavouriteClickListener.onFavouriteItemLongClick(itemView,position,ivFavourite);
                        }
                        return true;
                    }else {
                    return false;
                    }
                }
            });
        }
    }
    interface IOnFavouriteClickListener{
        void onFavouriteItemClick(View view,int position);
        void onFavouriteItemLongClick(View view,int position,ImageView ivFavourite);
    }
}
