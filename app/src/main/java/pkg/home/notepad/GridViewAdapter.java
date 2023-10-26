package pkg.home.notepad;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.GridViewHolder> {
    List<ListViewModelClass> listOfClass;
    boolean isVisible;
    ListViewModelClass note;
    Favourites favNote;
    onGridViewClickListener onGridViewClickListener;



    public void setOnGridViewClickListener(GridViewAdapter.onGridViewClickListener onGridViewClickListener) {
        this.onGridViewClickListener = onGridViewClickListener;
    }
@SuppressLint("NotifyDataSetChanged")
public void filterList(List<ListViewModelClass> list){
        listOfClass=list;
        notifyDataSetChanged();
}
    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public GridViewAdapter(List<ListViewModelClass> listOfClass) {
        this.listOfClass = listOfClass;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_layout,null);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
       ListViewModelClass listViewModelClass=listOfClass.get(position);
       holder.tvGridViewNote.setText(listViewModelClass.getText());
       holder.tvGridViewTime.setText(listViewModelClass.getTime());
    }

    @Override
    public int getItemCount() {
        return listOfClass.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder{
       CheckBox gridViewCheckBox;
       TextView tvGridViewNote,tvGridViewTime;
       ImageView ivFavourite;

       public GridViewHolder(@NonNull View itemView) {
           super(itemView);
           gridViewCheckBox=itemView.findViewById(R.id.gridViewCheckBox);
           tvGridViewNote=itemView.findViewById(R.id.tvShowNoteOutputInGridRecyclerView);
           tvGridViewTime=itemView.findViewById(R.id.tvShowTimeOutputInGridRecyclerView);
           if (isVisible){
               gridViewCheckBox.setVisibility(View.VISIBLE);
           }else {
               gridViewCheckBox.setVisibility(View.INVISIBLE);
           }
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if (onGridViewClickListener!=null){
                   int adapterPosition=getAdapterPosition();
                   if (adapterPosition!=RecyclerView.NO_POSITION){
                       onGridViewClickListener.onViewClick(itemView,adapterPosition,tvGridViewNote.getText().toString());
                   }
                   }
               }
           });
           itemView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View view) {
                   if (onGridViewClickListener!=null){
                       int position=getAdapterPosition();
                       if (position!=RecyclerView.NO_POSITION){
                           onGridViewClickListener.onViewLongClick(itemView,position);
                       }
                       return true;
                   }else {
                       return false;
                   }
               }
           });
           gridViewCheckBox.setOnClickListener(new View.OnClickListener() {
              private boolean isClicked=false;
              final NotesDataTable table=NotesDataTable.createObject(itemView.getContext());
               @Override
               public void onClick(View view) {
                   if (!isClicked){
                       note=table.getAllNotes().get(getAdapterPosition());
                       if (!table.getAllFavouriteNotes().isEmpty()){
                           favNote=table.getSelectedNote(note.getSerialNumber());
                           favNote.setChecked(true);
                           table.updateNoteInFavourite(favNote);
                       }
                       note.setChecked(true);
                       table.updateNote(note);
                       isClicked=true;
                   }else {
                       note = table.getAllNotes().get(getAdapterPosition());
                       if (!table.getAllFavouriteNotes().isEmpty()){
                           favNote = table.getSelectedNote(note.getSerialNumber());
                           favNote.setChecked(false);
                           table.updateNoteInFavourite(favNote);
                       }
                       note.setChecked(false);
                       table.updateNote(note);
                       isClicked=false;
                   }

               }
           });
       }
   }
   interface onGridViewClickListener{
        void onViewClick(View view,int position,String text);
        void onViewLongClick(View view,int position);
   }
}
