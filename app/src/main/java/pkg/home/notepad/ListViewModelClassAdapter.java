package pkg.home.notepad;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListViewModelClassAdapter extends RecyclerView.Adapter<ListViewModelClassAdapter.ListViewHolder>{
    List<ListViewModelClass> listViewModelClassList;
    ListViewModelClass note;
    Favourites favNote;
    NotesDataTable table;

    boolean isVisible;

    public void setVisibility(boolean isVisible){
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }
@SuppressLint("NotifyDataSetChanged")
public void filterList(List<ListViewModelClass> filterList){
  listViewModelClassList=filterList;
    notifyDataSetChanged();
}
    public ListViewModelClassAdapter(List<ListViewModelClass> listViewModelClassList) {
        this.listViewModelClassList = listViewModelClassList;
    }
    IonListItemClickListener itemClickListener;

    public void setItemClickListener(IonListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_view_layout,null);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
       ListViewModelClass listView=listViewModelClassList.get(position);
       holder.tvShowNoteOutput.setText(listView.getText());
       holder.tvShowTimeOutput.setText(listView.getTime());
    }

    @Override
    public int getItemCount() {
        return listViewModelClassList.size();
    }

     class ListViewHolder extends RecyclerView.ViewHolder{
        TextView tvShowNoteOutput,tvShowTimeOutput;
        CheckBox listViewCheckBox;
        ImageView ivFavourite;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShowNoteOutput=itemView.findViewById(R.id.tvShowNoteOutputInListRecyclerView);
            tvShowTimeOutput=itemView.findViewById(R.id.tvShowTimeOutputInListRecyclerView);
            ivFavourite=itemView.findViewById(R.id.ivFavourite);
            listViewCheckBox = itemView.findViewById(R.id.listViewCheckBox);
            if (isVisible) {
                listViewCheckBox.setVisibility(View.VISIBLE);
            } else {
                listViewCheckBox.setVisibility(View.INVISIBLE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            itemClickListener.onItemClick(itemView,getAdapterPosition(),tvShowNoteOutput.getText().toString());
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (itemClickListener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            itemClickListener.onLongItemClick(itemView,position);
                        }
                        return true;
                    }else {
                        return false;
                    }
                }
            });
            listViewCheckBox.setOnClickListener(new View.OnClickListener() {
                boolean isClicked=false;
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
    interface IonListItemClickListener{
        void onItemClick(View view,int position,String text);
        //data base say match kro aur wo open kro
        void onLongItemClick(View view,int position);
    }
}
